package spring.group.spring.services;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.EntityNotFoundException;
import spring.group.spring.exception.exceptions.IncorrectPincodeException;
import spring.group.spring.exception.exceptions.InsufficientFundsException;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.repositories.BankAccountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionService transactionService;
    private final ModelMapper mapper = new ModelMapper();

    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService, TransactionService transactionService, BCryptPasswordEncoder passwordEncoder) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
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

        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);
        return bankAccountRepository.save(bankAccount);
    }

    // TODO jwt
    public BankAccount atmLogin(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());
        if (bankAccount == null) {
            throw new EntityNotFoundException();
        }

        String fullName = bankAccount.getUser().getFirst_name() + " " + bankAccount.getUser().getLast_name();

        if (!fullName.equals(loginRequest.getFullname())) {
            throw new EntityNotFoundException();
        }
        if (!passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
            throw new IncorrectPincodeException();
        }

        return bankAccount;
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
                .toList();
    }
    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}\\d{2}[A-Z\\d]{4}\\d{7}[A-Z\\d]{0,16}$");
    }

    public boolean getBankAccountByIban(String iban) {
        return bankAccountRepository.findByIban(iban) != null;
    }

    public boolean checkIban(String iban) {
        return isValidIban(iban) && !getBankAccountByIban(iban);
    }
}
