package spring.group.spring.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exceptions.IncorrectPincodeException;
import spring.group.spring.exceptions.InsufficientFundsException;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
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
    // TODO Implement the following methods:

    // atmLogin()

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount atmLogin(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());
        if (bankAccount != null) {
            String fullName = bankAccount.getUser().getFirst_name() + " " + bankAccount.getUser().getLast_name();
            if (fullName.equals(loginRequest.getFullName())) {
                if (passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
                    return bankAccount;
                } else {
                    throw new IncorrectPincodeException("Incorrect pincode.");
                }
            }
        }
        return null;
    }

    public WithdrawDepositResponseDTO withdrawMoney(Integer id, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElse(null);

        if (bankAccount == null) {
            return null;
        }

        if (bankAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
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
                .orElse(null);

        if (bankAccount == null) {
            return null;
        }

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

    public BankAccountDTO convertToDTO(BankAccount bankAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();

        bankAccountDTO.setAccount_id(bankAccount.getAccount_id());
        bankAccountDTO.setUser(userService.convertToNameDTO(bankAccount.getUser()));
        bankAccountDTO.setIban(bankAccount.getIban());
        bankAccountDTO.setBalance(bankAccount.getBalance());
        bankAccountDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountDTO.setIs_active(bankAccount.getIs_active());
        bankAccountDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());

        return bankAccountDTO;
    }

    public BankAccountRequestDTO convertToRequestDTO(BankAccount bankAccount) {
        BankAccountRequestDTO bankAccountRequestDTO = new BankAccountRequestDTO();

        bankAccountRequestDTO.setUser_id(bankAccount.getUser().getUser_id());
        bankAccountRequestDTO.setIban(bankAccount.getIban());
        bankAccountRequestDTO.setBalance(bankAccount.getBalance());
        bankAccountRequestDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountRequestDTO.setIs_active(bankAccount.getIs_active());
        bankAccountRequestDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());
        bankAccountRequestDTO.setPincode(bankAccount.getPincode());

        return bankAccountRequestDTO;
    }

    public BankAccountResponseDTO convertToResponseDTO(BankAccount bankAccount) {
        BankAccountResponseDTO bankAccountResponseDTO = new BankAccountResponseDTO();
        bankAccountResponseDTO.setAccount_id(bankAccount.getAccount_id());
        bankAccountResponseDTO.setUser(userService.convertToDTO(bankAccount.getUser()));
        bankAccountResponseDTO.setIban(bankAccount.getIban());
        bankAccountResponseDTO.setBalance(bankAccount.getBalance());
        bankAccountResponseDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountResponseDTO.setIs_active(bankAccount.getIs_active());
        bankAccountResponseDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());
        return bankAccountResponseDTO;
    }

    public BankAccount convertToEntity(BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = new BankAccount();

        User user = new User();
        user.setUser_id(bankAccountDTO.getUser_id());

        bankAccount.setUser(user);
        bankAccount.setIban(bankAccountDTO.getIban());
        bankAccount.setBalance(bankAccountDTO.getBalance());
        bankAccount.setAccount_type(bankAccountDTO.getAccount_type());
        bankAccount.setIs_active(bankAccountDTO.getIs_active());
        bankAccount.setAbsolute_limit(bankAccountDTO.getAbsolute_limit());
        bankAccount.setPincode(bankAccountDTO.getPincode());

        return bankAccount;
    }

    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$");
    }
}
