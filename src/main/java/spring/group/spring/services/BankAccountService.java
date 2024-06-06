package spring.group.spring.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.TransactionRequestDTO;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.models.dto.transactions.TransactionsDTO;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.security.JwtProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionService transactionService;
    private final ModelMapper mapper = new ModelMapper();
    private final JwtProvider jwtProvider;
    private final UserService userService;

    public BankAccountService(BankAccountRepository bankAccountRepository, TransactionService transactionService, BCryptPasswordEncoder passwordEncoder, JwtProvider jwtProvider, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
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
        if (bankAccountRepository.findById(bankAccount.getAccount_id()).isEmpty()) {
            throw new EntityNotFoundException();
        }
        if (isPincodeEncrypted(bankAccount.getPincode())) {
            String encryptedPincode = passwordEncoder.encode(bankAccount.getPincode());
            bankAccount.setPincode(encryptedPincode);
        }

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

        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());

        TransactionRequestDTO transactionRequestDTO = createTransactionRequestDTO(null, id, bankAccount.getUser().getUser_id(), amount, "Withdraw");
        transactionService.createTransaction(transactionRequestDTO);
        bankAccountRepository.save(bankAccount);

        return withdrawDepositResponseDTO;
    }

    public WithdrawDepositResponseDTO depositMoney(Integer id, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        bankAccount.setBalance(bankAccount.getBalance().add(amount));

        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());

        TransactionRequestDTO transactionRequestDTO = createTransactionRequestDTO(id, null, bankAccount.getUser().getUser_id(), amount, "Deposit");
        transactionService.createTransaction(transactionRequestDTO);
        bankAccountRepository.save(bankAccount);

        return withdrawDepositResponseDTO;
    }

    private TransactionRequestDTO createTransactionRequestDTO(Integer toAccountId, Integer fromAccountId, Integer initiatorUserId, BigDecimal transferAmount, String description) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setTo_account_id(toAccountId);
        transactionRequestDTO.setFrom_account_id(fromAccountId);
        transactionRequestDTO.setInitiator_user_id(initiatorUserId);
        transactionRequestDTO.setTransfer_amount(transferAmount);
        transactionRequestDTO.setDate(LocalDateTime.now());
        transactionRequestDTO.setDescription(description);
        return transactionRequestDTO;
    }

    public List<BankAccountResponseDTO> convertToResponseDTO(List<BankAccount> bankAccounts) {
        return bankAccounts.stream()
                .map(bankAccount -> mapper.map(bankAccount, BankAccountResponseDTO.class))
                .toList();
    }


    public boolean isUserAccountOwner(String username, Integer accountId) {
        String bankAccountUsername = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new).getUser().getUsername();
        return bankAccountUsername.equals(username);
    }
  
    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}\\d{2}[A-Z\\d]{4}\\d{7}[A-Z\\d]{0,16}$");
    }

    public boolean getBankAccountByIban(String iban) {
        return bankAccountRepository.findByIban(iban) != null;
    }

    public List<BankAccount> getBankAccountsByUserId(Integer userId) {
        User user = userService.getUserById(userId);
        return bankAccountRepository.findByUser(user);
    }

    public TransactionsDTO convertToDTO(BankAccount bankAccount) {
        return mapper.map(bankAccount, TransactionsDTO.class);
    }

    public boolean checkIban(String iban) {
        return isValidIban(iban) && !getBankAccountByIban(iban);
    }

    public BankAccount closeBankAccount(int accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);
        bankAccount.setIs_active(false);
        return bankAccountRepository.save(bankAccount);
    }

    private boolean isPincodeEncrypted(String pincode) {
        String encryptedPincode = passwordEncoder.encode(pincode);
        return !passwordEncoder.matches(pincode, encryptedPincode);
    }
}
