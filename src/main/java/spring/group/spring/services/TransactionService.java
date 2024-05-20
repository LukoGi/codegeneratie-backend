package spring.group.spring.services;

import org.springframework.stereotype.Service;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.TransactionResponseDTO;
import spring.group.spring.models.dto.TransactionUpdateRequestDTO;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.TransactionRepository;
import spring.group.spring.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return transactionRepository.findById(id).orElse(null);
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
        transaction.setStart_date(transactionRequestDTO.getStart_date());
        transaction.setEnd_date(transactionRequestDTO.getEnd_date());
        transaction.setDescription(transactionRequestDTO.getDescription());

        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());

        return responseDTO;
    }

    public TransactionResponseDTO updateTransaction(Integer id, TransactionUpdateRequestDTO transactionUpdateRequestDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + id + " not found"));

        if (transactionUpdateRequestDTO.getTo_account_id() != null) {
            BankAccount toAccount = bankAccountRepository.findById(transactionUpdateRequestDTO.getTo_account_id())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionUpdateRequestDTO.getTo_account_id() + " not found"));
            transaction.setTo_account(toAccount);
        }

        if (transactionUpdateRequestDTO.getFrom_account_id() != null) {
            BankAccount fromAccount = bankAccountRepository.findById(transactionUpdateRequestDTO.getFrom_account_id())
                    .orElseThrow(() -> new IllegalArgumentException("BankAccount with ID " + transactionUpdateRequestDTO.getFrom_account_id() + " not found"));
            transaction.setFrom_account(fromAccount);
        }

        if (transactionUpdateRequestDTO.getInitiator_user_id() != null) {
            User initiatorUser = userRepository.findById(transactionUpdateRequestDTO.getInitiator_user_id())
                    .orElseThrow(() -> new IllegalArgumentException("User with ID " + transactionUpdateRequestDTO.getInitiator_user_id() + " not found"));
            transaction.setInitiator_user(initiatorUser);
        }

        if (transactionUpdateRequestDTO.getTransfer_amount() != null) {
            transaction.setTransfer_amount(transactionUpdateRequestDTO.getTransfer_amount());
        }

        if (transactionUpdateRequestDTO.getStart_date() != null) {
            transaction.setStart_date(transactionUpdateRequestDTO.getStart_date());
        }

        if (transactionUpdateRequestDTO.getEnd_date() != null) {
            transaction.setEnd_date(transactionUpdateRequestDTO.getEnd_date());
        }

        if (transactionUpdateRequestDTO.getDescription() != null) {
            transaction.setDescription(transactionUpdateRequestDTO.getDescription());
        }

        transactionRepository.save(transaction);

        TransactionResponseDTO responseDTO = new TransactionResponseDTO();
        responseDTO.setTransaction_id(transaction.getTransaction_id());

        return responseDTO;
    }

    public List<Transaction> getAllTransactions(LocalDateTime startDate, LocalDateTime endDate, BigDecimal minAmount, BigDecimal maxAmount, String iban) {
        return transactionRepository.findAllTransactionsWithFilters(startDate, endDate, minAmount, maxAmount, iban);
    }
}
