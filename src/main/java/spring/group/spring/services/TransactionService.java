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
        User initiatorUser = getUserById(customerTransactionRequestDTO.getInitiatorUserId());
        BankAccount toAccount = getBankAccountByIban(customerTransactionRequestDTO.getToAccountIban());
        BankAccount fromAccount = getBankAccountByUserId(customerTransactionRequestDTO.getInitiatorUserId());

        validateTransaction(fromAccount, toAccount, customerTransactionRequestDTO.getTransferAmount());

        updateAccountBalances(fromAccount, toAccount, customerTransactionRequestDTO.getTransferAmount());

        Transaction transaction = createAndSaveTransaction(toAccount, fromAccount, initiatorUser,
                customerTransactionRequestDTO.getTransferAmount(), customerTransactionRequestDTO.getDescription());

        return createTransactionResponseDTO(transaction);
    }

    public TransactionResponseDTO customerCreateInternalTransaction(InternalTransactionRequestDTO internalTransactionRequestDTO) {
        User user = getUserById(internalTransactionRequestDTO.getInitiatorUserId());
        BankAccount fromAccount = getBankAccountByUserAndType(user, AccountType.valueOf(internalTransactionRequestDTO.getFromAccountType().toUpperCase()));
        BankAccount toAccount = getBankAccountByUserAndType(user, AccountType.valueOf(internalTransactionRequestDTO.getToAccountType().toUpperCase()));

        validateTransaction(fromAccount, toAccount, internalTransactionRequestDTO.getTransferAmount());

        updateAccountBalances(fromAccount, toAccount, internalTransactionRequestDTO.getTransferAmount());

        Transaction transaction = createAndSaveTransaction(fromAccount, toAccount, user,
                internalTransactionRequestDTO.getTransferAmount(), "Transfer between checkings/savings accounts");

        return createTransactionResponseDTO(transaction);
    }

    public TransactionResponseDTO employeeCreateTransaction(EmployeeTransactionRequestDTO employeeTransactionRequestDTO) {
        BankAccount fromAccount = getBankAccountByIban(employeeTransactionRequestDTO.getFromAccountIban());
        BankAccount toAccount = getBankAccountByIban(employeeTransactionRequestDTO.getToAccountIban());

        validateTransaction(fromAccount, toAccount, employeeTransactionRequestDTO.getTransferAmount());

        updateAccountBalances(fromAccount, toAccount, employeeTransactionRequestDTO.getTransferAmount());

        Transaction transaction = createAndSaveTransaction(fromAccount, toAccount, getUserById(employeeTransactionRequestDTO.getInitiatorUserId()),
                employeeTransactionRequestDTO.getTransferAmount(), employeeTransactionRequestDTO.getDescription());

        return createTransactionResponseDTO(transaction);
    }

    private void validateTransaction(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferAmount) {
        if (!toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsExceeded(fromAccount, transferAmount);

            if (fromAccount.getAccountType() == AccountType.SAVINGS || toAccount.getAccountType() == AccountType.SAVINGS) {
                throw new TransactionWithSavingsAccountException();
            }
        }

        checkIfAbsoluteLimitIsExceeded(fromAccount, transferAmount);
    }

    private void updateAccountBalances(BankAccount fromAccount, BankAccount toAccount, BigDecimal transferAmount) {
        BigDecimal newFromAccountBalance = fromAccount.getBalance().subtract(transferAmount);
        fromAccount.setBalance(newFromAccountBalance);

        BigDecimal newToAccountBalance = toAccount.getBalance().add(transferAmount);
        toAccount.setBalance(newToAccountBalance);

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
    }

    private Transaction createAndSaveTransaction(BankAccount fromAccount, BankAccount toAccount, User initiatorUser,
                                                 BigDecimal transferAmount, String description) {
        Transaction transaction = new Transaction();
        transaction.setToAccount(toAccount);
        transaction.setFromAccount(fromAccount);
        transaction.setInitiatorUser(initiatorUser);
        transaction.setTransferAmount(transferAmount);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description);

        return transactionRepository.save(transaction);
    }

    private BankAccount getBankAccountByUserAndType(User user, AccountType accountType) {
        return bankAccountRepository.findByUserAndAccountType(user, accountType)
                .orElseThrow(() -> new EntityNotFoundException(accountType + " account not found for user " + user.getUserId()));
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private BankAccount getBankAccountByIban(String iban) {
        BankAccount bankAccount = bankAccountRepository.findByIban(iban);
        if (bankAccount == null) {
            throw new EntityNotFoundException("Bank account not found for IBAN: " + iban);
        }
        return bankAccount;
    }

    private BankAccount getBankAccountByUserId(Integer userId) {
        return bankAccountRepository.findByUseruserIdAndAccountTypeAndIsActive(userId, AccountType.CHECKINGS, true)
                .orElseThrow(() -> new ActiveCheckingAccountNotFoundException("Active checking account not found for user ID: " + userId));
    }

    // Luko - Einde
}
