package spring.group.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.TransactionRepository;
import spring.group.spring.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    private TransactionResponseDTO createTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransactionId(transaction.getTransactionId());
        responseDTO.setToAccount(transaction.getToAccount());
        responseDTO.setFromAccount(transaction.getFromAccount());
        responseDTO.setInitiatorUser(transaction.getInitiatorUser());
        responseDTO.setTransferAmount(transaction.getTransferAmount());
        responseDTO.setDate(transaction.getDate());
        responseDTO.setDescription(transaction.getDescription());
        return responseDTO;
    }

    private Transaction createTransactionEntity(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, CustomerTransactionRequestDTO customerTransactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setInitiatorUser(initiatorUser);
        transaction.setTransferAmount(customerTransactionRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(customerTransactionRequestDTO.getDescription());

        return transactionRepository.save(transaction);
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        BankAccount toAccount = null;
        BankAccount fromAccount = null;
        User initiatorUser = null;

        if (transactionRequestDTO.getToAccountId() != null) {
            toAccount = bankAccountRepository.findById(transactionRequestDTO.getToAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionRequestDTO.getToAccountId() + " not found"));
        }

        if (transactionRequestDTO.getFromAccountId() != null) {
            fromAccount = bankAccountRepository.findById(transactionRequestDTO.getFromAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionRequestDTO.getFromAccountId() + " not found"));
            validateAndApplyTransferLimits(fromAccount, toAccount, transactionRequestDTO);
        }

        if (transactionRequestDTO.getInitiatorUserId() != null) {
            initiatorUser = userRepository.findById(transactionRequestDTO.getInitiatorUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionRequestDTO.getInitiatorUserId() + " not found"));
        }

        Transaction transaction = createAndSaveTransaction(toAccount, fromAccount, initiatorUser, transactionRequestDTO);
        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransactionId(transaction.getTransactionId());

        return responseDTO;
    }
// Julian
    public Page<Transaction> getAllTransactions(LocalDateTime date, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllTransactionsWithFilters(date, minAmount, maxAmount, iban, pageable);
    }

    public TransactionRequestDTO createTransactionRequestDTO(Integer toAccountId, Integer fromAccountId, Integer initiatorUserId, BigDecimal transferAmount, String description) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setToAccountId(toAccountId);
        transactionRequestDTO.setFromAccountId(fromAccountId);
        transactionRequestDTO.setInitiatorUserId(initiatorUserId);
        transactionRequestDTO.setTransferAmount(transferAmount);
        transactionRequestDTO.setDate(LocalDateTime.now());
        transactionRequestDTO.setDescription(description);
        return transactionRequestDTO;
    }

    private void validateAndApplyTransferLimits(BankAccount fromAccount, BankAccount toAccount, TransactionRequestDTO transactionRequestDTO) {
        checkIfAbsoluteLimitIsExceeded(fromAccount, transactionRequestDTO.getTransferAmount());
        if (transactionRequestDTO.getToAccountId() != null && !toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsExceeded(fromAccount, transactionRequestDTO.getTransferAmount());
        } else if (transactionRequestDTO.getToAccountId() == null) {
            checkIfDailyLimitIsExceeded(fromAccount, transactionRequestDTO.getTransferAmount());
        }
    }

    private Transaction createAndSaveTransaction(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setInitiatorUser(initiatorUser);
        transaction.setTransferAmount(transactionRequestDTO.getTransferAmount());
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setDescription(transactionRequestDTO.getDescription());

        return transaction;
    }

    // K - checkIfAbsoluteLimitIsHit
    public void checkIfAbsoluteLimitIsExceeded(BankAccount fromAccount, BigDecimal transferAmount) {
        BigDecimal absoluteLimit = fromAccount.getAbsoluteLimit();
        BigDecimal currentBalance = fromAccount.getBalance();
        BigDecimal newBalance = currentBalance.subtract(transferAmount);

        if (newBalance.compareTo(absoluteLimit) < 0){
            throw new AbsoluteLimitExceededException();
        }
    }

    // K - checkIfDailyLimitIsHit
    public void checkIfDailyLimitIsExceeded(BankAccount fromAccount, BigDecimal transferAmount) {
        BigDecimal dailyLimit = fromAccount.getUser().getDailyTransferLimit();
        BigDecimal sumOfTodaysTransactions = transactionRepository.getSumOfTodaysTransaction(fromAccount, LocalDateTime.now());
        if (sumOfTodaysTransactions == null){
            sumOfTodaysTransactions = new BigDecimal(0);
        }
        BigDecimal sumOfTodaysTransactionsWithNewTransaction = sumOfTodaysTransactions.add(transferAmount);

        if (sumOfTodaysTransactionsWithNewTransaction.compareTo(dailyLimit) > 0){
            throw new DailyTransferLimitExceededException();
        }
    }
// Julian

    public Page<Transaction> getTransactionsByUserId(Integer customerId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllByInitiatorUserIdWithFilters(customerId, startDate, endDate, minAmount, maxAmount, iban, pageable);
    }
// Julian

    public Page<Transaction> getTransactionsByAccountId(Integer accountId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllTransactionsWithAccountIdAndFilters(accountId, startDate, endDate, minAmount, maxAmount, iban, pageable);
    }

    // Luko - Begin

    public TransactionResponseDTO customerCreateTransaction(CustomerTransactionRequestDTO customerTransactionRequestDTO) {
        User initiatorUser = getInitiatorUser(customerTransactionRequestDTO.getInitiatorUserId());
        BankAccount toAccount = getToAccount(customerTransactionRequestDTO.getToAccountIban());
        BankAccount fromAccount = getFromAccount(customerTransactionRequestDTO.getInitiatorUserId());

        if (toAccount.getAccountType() == AccountType.SAVINGS) {
            throw new TransactionWithSavingsAccountException();
        }

        checkAccountBalance(fromAccount, customerTransactionRequestDTO.getTransferAmount());

        if (!toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsExceeded(fromAccount, customerTransactionRequestDTO.getTransferAmount());
        }
        checkIfAbsoluteLimitIsExceeded(fromAccount, customerTransactionRequestDTO.getTransferAmount());

        updateAccountBalances(fromAccount, toAccount, customerTransactionRequestDTO.getTransferAmount());

        Transaction transaction = createTransactionEntity(toAccount, fromAccount, initiatorUser, customerTransactionRequestDTO);

        return createTransactionResponseDTO(transaction);
    }

    public TransactionResponseDTO customerCreateInternalTransaction(InternalTransactionRequestDTO internalTransactionRequestDTO) {
        User user = userRepository.findById(internalTransactionRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BankAccount fromAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(internalTransactionRequestDTO.getFromAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));

        BankAccount toAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(internalTransactionRequestDTO.getToAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        checkIfAbsoluteLimitIsExceeded(fromAccount, internalTransactionRequestDTO.getTransferAmount());

        fromAccount.setBalance(fromAccount.getBalance().subtract(internalTransactionRequestDTO.getTransferAmount()));
        toAccount.setBalance(toAccount.getBalance().add(internalTransactionRequestDTO.getTransferAmount()));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setInitiatorUser(user);
        transaction.setTransferAmount(internalTransactionRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription("Transfer between checkings/savings accounts");

        transactionRepository.save(transaction);

        return createTransactionResponseDTO(transaction);
    }

    public TransactionResponseDTO employeeCreateTransaction(EmployeeTransactionRequestDTO employeeTransactionRequestDTO) {
        BankAccount fromAccount = validateAndGetAccount(employeeTransactionRequestDTO.getFromAccountIban());
        BankAccount toAccount = validateAndGetAccount(employeeTransactionRequestDTO.getToAccountIban());
        validateTransferDetails(fromAccount, employeeTransactionRequestDTO.getTransferAmount());

        performTransfer(fromAccount, toAccount, employeeTransactionRequestDTO.getTransferAmount());

        return createAndSaveEmployeeTransaction(fromAccount, toAccount, employeeTransactionRequestDTO);
    }

    private User getInitiatorUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
    }

    private BankAccount getToAccount(String iban) {
        return bankAccountRepository.findByIban(iban);
    }

    private BankAccount getFromAccount(Integer userId) {
        return bankAccountRepository.findByUseruserIdAndAccountTypeAndIsActive(userId, AccountType.CHECKINGS, true)
                .orElseThrow(ActiveCheckingAccountNotFoundException::new);
    }

    private void checkAccountBalance(BankAccount fromAccount, BigDecimal transferAmount) {
        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new InsufficientFundsException();
        }
    }

    private void updateAccountBalances(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferAmount) {
        BigDecimal newFromAccountBalance = fromAccount.getBalance().subtract(transferAmount);
        fromAccount.setBalance(newFromAccountBalance);

        BigDecimal newToAccountBalance = toAccount.getBalance().add(transferAmount);
        toAccount.setBalance(newToAccountBalance);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
    }

    private BankAccount validateAndGetAccount(String iban) {
        BankAccount account = bankAccountRepository.findByIban(iban);
        if (account == null || account.getAccountType() != AccountType.CHECKINGS) {
            throw new EntityNotFoundException();
        }
        return account;
    }

    private void validateTransferDetails(BankAccount fromAccount, BigDecimal transferAmount) {
        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new InsufficientFundsException();
        }
        checkTransferLimits(fromAccount, transferAmount);
    }

    private void checkTransferLimits(BankAccount fromAccount, BigDecimal transferAmount) {
        checkIfDailyLimitIsExceeded(fromAccount, transferAmount);
        checkIfAbsoluteLimitIsExceeded(fromAccount, transferAmount);
    }

    private void performTransfer(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferAmount) {
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));
        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
    }

    private TransactionResponseDTO createAndSaveEmployeeTransaction(BankAccount fromAccount, BankAccount toAccount, EmployeeTransactionRequestDTO employeeTransactionRequestDTO) {
        User employee = userRepository.findById(employeeTransactionRequestDTO.getEmployeeId())
                .orElseThrow(EntityNotFoundException::new);

        Transaction transaction = new Transaction();
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setInitiatorUser(employee);
        transaction.setTransferAmount(employeeTransactionRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(employeeTransactionRequestDTO.getDescription());

        transactionRepository.save(transaction);

        return createTransactionResponseDTO(transaction);
    }

    // Luko - Einde
}
