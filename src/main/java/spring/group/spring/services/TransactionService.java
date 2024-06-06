package spring.group.spring.services;

import org.modelmapper.ModelMapper;
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

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper = new ModelMapper();

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public TransactionResponseDTO createTransactionFromIban(TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        try {
            User initiatorUser = getInitiatorUser(transactionCreateFromIbanRequestDTO.getInitiator_user_id());
            BankAccount toAccount = getToAccount(transactionCreateFromIbanRequestDTO.getTo_account_iban());
            BankAccount fromAccount = getFromAccount(transactionCreateFromIbanRequestDTO.getInitiator_user_id());

            if (toAccount.getAccount_type() == AccountType.SAVINGS) {
                throw new TransactionToSavingsAccountException();
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

    // TODO: maybe make this method smaller / split it into multiple methods for better readability ;(
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

            checkIfAbsoluteLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
            if (transactionRequestDTO.getTo_account_id() != null && !toAccount.getUser().equals(fromAccount.getUser())) {
                checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
            } else if (transactionRequestDTO.getTo_account_id() == null) {
                checkIfDailyLimitIsHit(fromAccount, transactionRequestDTO.getTransfer_amount());
            }
        }

        if (transactionRequestDTO.getInitiator_user_id() != null) {
            initiatorUser = userRepository.findById(transactionRequestDTO.getInitiator_user_id())
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionRequestDTO.getInitiator_user_id() + " not found"));
        }

        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(transactionRequestDTO.getTransfer_amount());
        transaction.setDate(transactionRequestDTO.getDate());
        transaction.setDescription(transactionRequestDTO.getDescription());

        transactionRepository.save(transaction);

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
        BigDecimal newBalance = fromAccount.getBalance().subtract(transferAmount);

        if (newBalance.compareTo(absoluteLimit) < 0){
            throw new AbsoluteTransferLimitHitException();
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

    public Page<Transaction> getTransactionsByCustomerId(Integer customerId, Integer offset, Integer limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        return transactionRepository.findAllByInitiatorUserId(customerId, pageable);
    }

    public TransactionsDTO convertToDTO(Transaction transaction) {
        TransactionsDTO transactionsDTO = mapper.map(transaction, TransactionsDTO.class);
        String recipientName = transaction.getTo_account().getUser().getFirst_name() + " " + transaction.getTo_account().getUser().getLast_name();
        transactionsDTO.setRecipientName(recipientName);
        String initiatorName = transaction.getInitiator_user().getFirst_name() + " " + transaction.getInitiator_user().getLast_name();
        transactionsDTO.setInitiatorName(initiatorName);
        return transactionsDTO;
    }

    public TransactionResponseDTO transferFunds(TransferRequestDTO transferRequestDTO) {
        User user = userRepository.findById(transferRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BankAccount fromAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(transferRequestDTO.getFromAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));

        BankAccount toAccount = bankAccountRepository.findByUserAndAccountType(user, AccountType.valueOf(transferRequestDTO.getToAccountType().toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        if (fromAccount.getBalance().compareTo(transferRequestDTO.getTransferAmount()) < 0) {
            throw new InsufficientFundsException();
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
}
