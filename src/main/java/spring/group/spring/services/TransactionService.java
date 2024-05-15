package spring.group.spring.services;

import org.springframework.stereotype.Service;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.TransactionResponseDTO;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.TransactionRepository;
import spring.group.spring.repositories.UserRepository;

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
        Optional<BankAccount> toAccount = bankAccountRepository.findById(transactionRequestDTO.getTo_account_id());
        Optional<BankAccount> fromAccount = bankAccountRepository.findById(transactionRequestDTO.getFrom_account_id());
        Optional<User> initiatorUser = userRepository.findById(transactionRequestDTO.getInitiator_user_id());

        if (toAccount.isPresent() && fromAccount.isPresent() && initiatorUser.isPresent()) {
            Transaction transaction = new Transaction();
            transaction.setTo_account(toAccount.get());
            transaction.setFrom_account(fromAccount.get());
            transaction.setInitiator_user(initiatorUser.get());
            transaction.setTransfer_amount(transactionRequestDTO.getTransfer_amount());
            transaction.setStart_date(transactionRequestDTO.getStart_date());
            transaction.setEnd_date(transactionRequestDTO.getEnd_date());
            transaction.setDescription(transactionRequestDTO.getDescription());

            transactionRepository.save(transaction);

            TransactionResponseDTO responseDTO = new TransactionResponseDTO();
            responseDTO.setTransaction_id(transaction.getTransaction_id());

            return responseDTO;
        } else {
            throw new IllegalArgumentException("Invalid account or user ID");
        }
    }
}
