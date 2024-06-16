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
        responseDTO.setTransactionId(transaction.getTransaction_id());
        responseDTO.setToAccount(transaction.getTo_account());
        responseDTO.setFromAccount(transaction.getFrom_account());
        responseDTO.setInitiatorUser(transaction.getInitiator_user());
        responseDTO.setTransferAmount(transaction.getTransfer_amount());
        responseDTO.setDate(transaction.getDate());
        responseDTO.setDescription(transaction.getDescription());
        return responseDTO;
    }

    private Transaction createTransactionEntity(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, CustomerTransactionRequestDTO customerTransactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(customerTransactionRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(customerTransactionRequestDTO.getDescription());

        return transactionRepository.save(transaction);
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        BankAccount toAccount = null;
        BankAccount fromAccount = null;
        User initiatorUser = null;

        if (transactionRequestDTO.getTo_account_id() != null) {
            toAccount = bankAccountRepository.findById(transactionRequestDTO.getTo_account_id())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionRequestDTO.getTo_account_id() + " not found"));
        }

        if (transactionRequestDTO.getFrom_account_id() != null) {
            fromAccount = bankAccountRepository.findById(transactionRequestDTO.getFrom_account_id())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionRequestDTO.getFrom_account_id() + " not found"));
            validateAndApplyTransferLimits(fromAccount, toAccount, transactionRequestDTO);
        }

        if (transactionRequestDTO.getInitiator_user_id() != null) {
            initiatorUser = userRepository.findById(transactionRequestDTO.getInitiator_user_id())
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionRequestDTO.getInitiator_user_id() + " not found"));
        }

        Transaction transaction = createAndSaveTransaction(toAccount, fromAccount, initiatorUser, transactionRequestDTO);
        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransactionId(transaction.getTransaction_id());

        return responseDTO;
    }
// Julian
    public Page<Transaction> getAllTransactions(LocalDateTime date, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllTransactionsWithFilters(date, minAmount, maxAmount, iban, pageable);
    }

    public TransactionRequestDTO createTransactionRequestDTO(Integer toAccountId, Integer fromAccountId, Integer initiatorUserId, BigDecimal transferAmount, String description) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        transactionRequestDTO.setTo_account_id(toAccountId);
        transactionRequestDTO.setFrom_account_id(fromAccountId);
        transactionRequestDTO.setInitiator_user_id(initiatorUserId);
        transactionRequestDTO.setTransfer_amount(transferAmount);
        transactionRequestDTO.setDate(LocalDateTime.now());
        transactionRequestDTO.setDescription(description);
        return transactionRequestDTO;
    }

    private void validateAndApplyTransferLimits(BankAccount fromAccount, BankAccount toAccount, TransactionRequestDTO transactionRequestDTO) {
        checkIfAbsoluteLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        if (transactionRequestDTO.getTo_account_id() != null && !toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        } else if (transactionRequestDTO.getTo_account_id() == null) {
            checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        }
    }

    private Transaction createAndSaveTransaction(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(transactionRequestDTO.getTransfer_amount());
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setDescription(transactionRequestDTO.getDescription());

        return transaction;
    }

    // K - checkIfAbsoluteLimitIsHit
    public void checkIfAbsoluteLimitIsHit(BankAccount fromAccount, BigDecimal transferAmount) {
        BigDecimal absoluteLimit = fromAccount.getAbsolute_limit();
        BigDecimal currentBalance = fromAccount.getBalance();
        BigDecimal newBalance = currentBalance.subtract(transferAmount);

        if (newBalance.compareTo(absoluteLimit) < 0){
            throw new AbsoluteLimitHitException();
        }
    }

    // K - checkIfDailyLimitIsHit
    public void checkIfDailyLimitIsHit(BankAccount fromAccount, BigDecimal transferAmount) {
        BigDecimal dailyLimit = fromAccount.getUser().getDaily_transfer_limit();
        BigDecimal sumOfTodaysTransactions = transactionRepository.getSumOfTodaysTransaction(fromAccount, LocalDateTime.now());
        if (sumOfTodaysTransactions == null){
            sumOfTodaysTransactions = new BigDecimal(0);
        }
        BigDecimal sumOfTodaysTransactionsWithNewTransaction = sumOfTodaysTransactions.add(transferAmount);

        if (sumOfTodaysTransactionsWithNewTransaction.compareTo(dailyLimit) > 0){
            throw new DailyTransferLimitHitException();
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

        if (toAccount.getAccount_type() == AccountType.SAVINGS) {
            throw new TransactionWithSavingsAccountException();
        }

        checkAccountBalance(fromAccount, customerTransactionRequestDTO.getTransferAmount());

        if (!toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsHit(fromAccount, customerTransactionRequestDTO.getTransferAmount());
        }
        checkIfAbsoluteLimitIsHit(fromAccount, customerTransactionRequestDTO.getTransferAmount());

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

        checkIfAbsoluteLimitIsHit(fromAccount, internalTransactionRequestDTO.getTransferAmount());

        fromAccount.setBalance(fromAccount.getBalance().subtract(internalTransactionRequestDTO.getTransferAmount()));
        toAccount.setBalance(toAccount.getBalance().add(internalTransactionRequestDTO.getTransferAmount()));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFrom_account(fromAccount);
        transaction.setTo_account(toAccount);
        transaction.setInitiator_user(user);
        transaction.setTransfer_amount(internalTransactionRequestDTO.getTransferAmount());
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
        return bankAccountRepository.findByUserUser_idAndAccountTypeAndIsActive(userId, AccountType.CHECKINGS, true)
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
        if (account == null || account.getAccount_type() != AccountType.CHECKINGS) {
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
        checkIfDailyLimitIsHit(fromAccount, transferAmount);
        checkIfAbsoluteLimitIsHit(fromAccount, transferAmount);
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
        transaction.setFrom_account(fromAccount);
        transaction.setTo_account(toAccount);
        transaction.setInitiator_user(employee);
        transaction.setTransfer_amount(employeeTransactionRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(employeeTransactionRequestDTO.getDescription());

        transactionRepository.save(transaction);

        return createTransactionResponseDTO(transaction);
    }

    // Luko - Einde
}
