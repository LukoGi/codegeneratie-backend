package spring.group.spring.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.security.JwtProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionService transactionService;
    private final ModelMapper mapper = new ModelMapper();
    private final JwtProvider jwtProvider;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService, TransactionService transactionService, BCryptPasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
        this.jwtProvider = jwtProvider;
    }

    public BankAccount createBankAccount(BankAccount bankAccount) {
        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);

        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccountATMLoginResponse atmLogin(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());

        if (bankAccount == null) {
            throw new EntityNotFoundException();
        }

        String fullName = bankAccount.getUser().getFirst_name() + " " + bankAccount.getUser().getLast_name();

        if (!fullName.equals(loginRequest.getFullname())) {
            throw new IncorrectFullnameOnCardException();
        }
        if (!passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
            throw new IncorrectPincodeException();
        }

        BankAccountATMLoginResponse response = mapper.map(bankAccount, BankAccountATMLoginResponse.class);
        response.setToken(jwtProvider.createToken(bankAccount.getUser().getUsername(), bankAccount.getUser().getRoles()));

        return response;
    }

    public WithdrawDepositResponseDTO withdrawMoney(Integer id, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
        bankAccountRepository.save(bankAccount);

        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());

        TransactionRequestDTO transactionRequestDTO = createTransactionRequestDTO(null, id, bankAccount.getUser().getUser_id(), amount, "Withdraw");
        transactionService.createTransaction(transactionRequestDTO);

        return withdrawDepositResponseDTO;
    }

    public WithdrawDepositResponseDTO depositMoney(Integer id, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        bankAccount.setBalance(bankAccount.getBalance().add(amount));
        bankAccountRepository.save(bankAccount);

        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());

        TransactionRequestDTO transactionRequestDTO = createTransactionRequestDTO(id, null, bankAccount.getUser().getUser_id(), amount, "Deposit");
        transactionService.createTransaction(transactionRequestDTO);

        return withdrawDepositResponseDTO;
    }

    private TransactionRequestDTO createTransactionRequestDTO(Integer toAccountId, Integer fromAccountId, Integer initiatorUserId, BigDecimal transferAmount, String description) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setTo_account_id(toAccountId);
        transactionRequestDTO.setFrom_account_id(fromAccountId);
        transactionRequestDTO.setInitiator_user_id(initiatorUserId);
        transactionRequestDTO.setTransfer_amount(transferAmount);
        transactionRequestDTO.setStart_date(LocalDateTime.now());
        transactionRequestDTO.setEnd_date(null);
        transactionRequestDTO.setDescription(description);
        return transactionRequestDTO;
    }

    public List<BankAccountResponseDTO> convertToResponseDTO(List<BankAccount> bankAccounts) {
        return bankAccounts.stream()
                .map(bankAccount -> mapper.map(bankAccount, BankAccountResponseDTO.class))
                .collect(Collectors.toList());
    }

    public void isUserAccountOwner(String username, Integer accountId) {
        String bankAccountUsername = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new).getUser().getUsername();
        if (!bankAccountUsername.equals(username)) {
            throw new AccessDeniedException("You are not the owner of this account");
        }
    }

    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$");
    }
}
