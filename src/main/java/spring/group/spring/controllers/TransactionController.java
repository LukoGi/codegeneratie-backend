package spring.group.spring.controllers;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Integer id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/create")
    public TransactionResponseDTO createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @PostMapping("/createWithIban")
    public TransactionResponseDTO createTransactionFromIban(@RequestBody TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        return transactionService.createTransactionFromIban(transactionCreateFromIbanRequestDTO);
    }

    @PutMapping("/{id}")
    public TransactionResponseDTO updateTransaction(@PathVariable Integer id, @Valid @RequestBody TransactionUpdateRequestDTO transactionUpdateRequestDTO) {
        return transactionService.updateTransaction(id, transactionUpdateRequestDTO);
    }

    @GetMapping
    // @PreAuthorize("hasRole('Employee ')")
    public List<Transaction> getAllTransactions(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban) {

        LocalDateTime dateTime = (date != null) ? LocalDateTime.parse(date) : null;

        return transactionService.getAllTransactions(dateTime, minAmount, maxAmount, iban);
    }

    @GetMapping("/customer/{customerId}")
    public List<TransactionsDTO> getTransactionsByCustomerId(@PathVariable Integer customerId) {
        List<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        return transactions.stream().map(transactionService::convertToDTO).toList();
    }
}
