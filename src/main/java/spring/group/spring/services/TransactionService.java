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

    public TransactionResponseDTO createTransactionFromIban(TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        try {
            User initiatorUser = getInitiatorUser(transactionCreateFromIbanRequestDTO.getInitiator_user_id());
            BankAccount toAccount = getToAccount(transactionCreateFromIbanRequestDTO.getTo_account_iban());
            BankAccount fromAccount = getFromAccount(transactionCreateFromIbanRequestDTO.getInitiator_user_id());

            if (toAccount.getAccount_type() == AccountType.SAVINGS) {
                throw new TransactionWithSavingsAccountException();
            }

            checkAccountBalance(fromAccount, transactionCreateFromIbanRequestDTO.getTransfer_amount());

            if (!toAccount.getUser().equals(fromAccount.getUser())) {
                checkIfDailyLimitIsHit(fromAccount, transactionCreateFromIbanRequestDTO.getTransfer_amount());
            }
            checkIfAbsoluteLimitIsHit(fromAccount, transactionCreateFromIbanRequestDTO.getTransfer_amount());

            updateAccountBalances(fromAccount, toAccount, transactionCreateFromIbanRequestDTO.getTransfer_amount());

            Transaction transaction = createTransactionEntity(toAccount, fromAccount, initiatorUser, transactionCreateFromIbanRequestDTO);

            TransactionResponseDTO responseDTO = new TransactionResponseDTO();
            responseDTO.setTransaction_id(transaction.getTransaction_id());

            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the transaction", e);
        }
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

    private Transaction createTransactionEntity(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(transactionCreateFromIbanRequestDTO.getTransfer_amount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(transactionCreateFromIbanRequestDTO.getDescription());

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

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());

        return responseDTO;
    }

    public Page<Transaction> getAllTransactions(LocalDateTime date, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllTransactionsWithFilters(date, minAmount, maxAmount, iban, pageable);
    }

    private void checkIfAbsoluteLimitIsHit(BankAccount fromAccount, BigDecimal transferAmount) {
        BigDecimal absoluteLimit = fromAccount.getAbsolute_limit();
        BigDecimal currentBalance = fromAccount.getBalance();
        BigDecimal newBalance = currentBalance.subtract(transferAmount);

        if (newBalance.compareTo(absoluteLimit) < 0){
            throw new AbsoluteLimitHitException();
        }
    }

    private void checkIfDailyLimitIsHit(BankAccount fromAccount, BigDecimal transferAmount) {
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

    public Page<Transaction> getTransactionsByCustomerId(Integer customerId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String iban, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllByInitiatorUserIdWithFilters(customerId, startDate, endDate, minAmount, maxAmount, iban, pageable);
    }

    public TransactionOverviewDTO convertToDTO(Transaction transaction) {
        TransactionOverviewDTO transactionsDTO = new TransactionOverviewDTO();

        transactionsDTO.setDate(transaction.getDate());
        transactionsDTO.setTransferAmount(transaction.getTransfer_amount());
        transactionsDTO.setDescription(transaction.getDescription());

        if (transaction.getFrom_account() != null) {
            transactionsDTO.setFromAccountIban(transaction.getFrom_account().getIban());
        }

        if (transaction.getTo_account() != null) {
            transactionsDTO.setToAccountIban(transaction.getTo_account().getIban());
            transactionsDTO.setRecipientName(transaction.getTo_account().getUser().getFirst_name() + " " + transaction.getTo_account().getUser().getLast_name());
        }

        if (transaction.getInitiator_user() != null) {
            transactionsDTO.setInitiatorName(transaction.getInitiator_user().getFirst_name() + " " + transaction.getInitiator_user().getLast_name());
        }

        return transactionsDTO;
    }

    public TransactionResponseDTO transferFunds(TransferRequestDTO transferRequestDTO) {
        User user = userRepository.findById(transferRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BankAccount fromAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(transferRequestDTO.getFromAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));

        BankAccount toAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(transferRequestDTO.getToAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        if (fromAccount.getAbsolute_limit().compareTo(transferRequestDTO.getTransferAmount()) < 0) {
            throw new AbsoluteLimitHitException();
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferRequestDTO.getTransferAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transferRequestDTO.getTransferAmount()));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setFrom_account(fromAccount);
        transaction.setTo_account(toAccount);
        transaction.setInitiator_user(user);
        transaction.setTransfer_amount(transferRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription("Transfer between checkings/savings accounts");

        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());

        return responseDTO;
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

    public TransactionResponseDTO employeeTransferFunds(EmployeeTransferRequestDTO employeeTransferRequestDTO) {
        try{
            BankAccount fromAccount = retrieveAndValidateAccounts(employeeTransferRequestDTO);

            checkTransferLimits(fromAccount, employeeTransferRequestDTO.getTransferAmount());

            performTransfer(fromAccount, employeeTransferRequestDTO);

            return recordTransaction(fromAccount, employeeTransferRequestDTO);
        }catch (Exception e) {
            throw new RuntimeException("An error occurred while creating the transaction", e);
        }
    }

    private BankAccount retrieveAndValidateAccounts(EmployeeTransferRequestDTO employeeTransferRequestDTO) {
        BankAccount fromAccount = bankAccountRepository.findByIban(employeeTransferRequestDTO.getFromAccountIban());
        BankAccount toAccount = bankAccountRepository.findByIban(employeeTransferRequestDTO.getToAccountIban());

        if (fromAccount == null || toAccount == null) {
            throw new EntityNotFoundException();
        }
        if (fromAccount.getAccount_type() != AccountType.CHECKINGS || toAccount.getAccount_type() != AccountType.CHECKINGS) {
            throw new TransactionWithSavingsAccountException();
        }
        if (fromAccount.getBalance().compareTo(employeeTransferRequestDTO.getTransferAmount()) < 0) {
            throw new InsufficientFundsException();
        }

        return fromAccount;
    }

    private void checkTransferLimits(BankAccount fromAccount, BigDecimal transferAmount) {
        checkIfDailyLimitIsHit(fromAccount, transferAmount);
        checkIfAbsoluteLimitIsHit(fromAccount, transferAmount);
    }

    private void performTransfer(BankAccount fromAccount, EmployeeTransferRequestDTO employeeTransferRequestDTO) {
        BigDecimal transferAmount = employeeTransferRequestDTO.getTransferAmount();
        BankAccount toAccount = bankAccountRepository.findByIban(employeeTransferRequestDTO.getToAccountIban());

        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
    }

    private TransactionResponseDTO recordTransaction(BankAccount fromAccount, EmployeeTransferRequestDTO employeeTransferRequestDTO) {
        User employee = userRepository.findById(employeeTransferRequestDTO.getEmployeeId())
                .orElseThrow(EntityNotFoundException::new);
        BankAccount toAccount = bankAccountRepository.findByIban(employeeTransferRequestDTO.getToAccountIban());

        Transaction transaction = new Transaction();
        transaction.setFrom_account(fromAccount);
        transaction.setTo_account(toAccount);
        transaction.setInitiator_user(employee);
        transaction.setTransfer_amount(employeeTransferRequestDTO.getTransferAmount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(employeeTransferRequestDTO.getDescription());

        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());
        return responseDTO;
    }

    private Transaction createAndSaveTransaction(BankAccount toAccount, BankAccount fromAccount, User initiatorUser, TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(transactionRequestDTO.getTransfer_amount());
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setDescription(transactionRequestDTO.getDescription());

        return transactionRepository.save(transaction);
    }

    private void validateAndApplyTransferLimits(BankAccount fromAccount, BankAccount toAccount, TransactionRequestDTO transactionRequestDTO) {
        checkIfAbsoluteLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        if (transactionRequestDTO.getTo_account_id() != null && !toAccount.getUser().equals(fromAccount.getUser())) {
            checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        } else if (transactionRequestDTO.getTo_account_id() == null) {
            checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
        }
    }
}
