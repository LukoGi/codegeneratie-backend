package spring.group.spring.services;

import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.EntityNotFoundException;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.TransactionCreateRequestDTO;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.TransactionResponseDTO;
import spring.group.spring.models.dto.TransactionUpdateRequestDTO;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.TransactionRepository;
import spring.group.spring.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public TransactionResponseDTO createTransactionFromIban(TransactionCreateRequestDTO transactionCreateRequestDTO) {
        BankAccount toAccount = null;
        BankAccount fromAccount = null;
        User initiatorUser = null;

        toAccount = bankAccountRepository.findByIban(transactionCreateRequestDTO.getTo_account_iban());
        if (toAccount == null) {
            throw new IllegalArgumentException("BankAccount with IBAN " + transactionCreateRequestDTO.getTo_account_iban() + " not found");
        }

        fromAccount = bankAccountRepository.findByIban(transactionCreateRequestDTO.getFrom_account_iban());
        if (fromAccount == null) {
            throw new IllegalArgumentException("BankAccount with IBAN " + transactionCreateRequestDTO.getFrom_account_iban() + " not found");
        }

        if (transactionCreateRequestDTO.getInitiator_user_id() != null) {
            initiatorUser = userRepository.findById(transactionCreateRequestDTO.getInitiator_user_id())
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionCreateRequestDTO.getInitiator_user_id() + " not found"));
        }

        Transaction transaction = new Transaction();
        transaction.setTo_account(toAccount);
        transaction.setFrom_account(fromAccount);
        transaction.setInitiator_user(initiatorUser);
        transaction.setTransfer_amount(transactionCreateRequestDTO.getTransfer_amount());
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(transactionCreateRequestDTO.getDescription());

        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());

        return responseDTO;
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

    public TransactionResponseDTO updateTransaction(Integer id, TransactionUpdateRequestDTO transactionUpdateRequestDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + id + " not found"));

        BankAccount toAccount = bankAccountRepository.findById(transactionUpdateRequestDTO.getTo_account_id())
                .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionUpdateRequestDTO.getTo_account_id() + " not found"));
        transaction.setTo_account(toAccount);

        BankAccount fromAccount = bankAccountRepository.findById(transactionUpdateRequestDTO.getFrom_account_id())
                .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionUpdateRequestDTO.getFrom_account_id() + " not found"));
        transaction.setFrom_account(fromAccount);

        User initiatorUser = userRepository.findById(transactionUpdateRequestDTO.getInitiator_user_id())
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionUpdateRequestDTO.getInitiator_user_id() + " not found"));
        transaction.setInitiator_user(initiatorUser);

        transaction.setTransfer_amount(transactionUpdateRequestDTO.getTransfer_amount());
        transaction.setDate(transactionUpdateRequestDTO.getDate());
        transaction.setDescription(transactionUpdateRequestDTO.getDescription());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(updatedTransaction.getTransaction_id());

        return responseDTO;
    }

    public List<Transaction> getAllTransactions(LocalDateTime date, BigDecimal minAmount, BigDecimal maxAmount, String iban) {
        return transactionRepository.findAllTransactionsWithFilters(date, minAmount, maxAmount, iban);
    }
}
