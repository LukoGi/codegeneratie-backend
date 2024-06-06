package spring.group.spring.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    // @PreAuthorize("hasRole('Employee ')")
    public Page<Transaction> getAllTransactions(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        LocalDateTime dateTime = (date != null) ? LocalDateTime.parse(date) : null;

        return transactionService.getAllTransactions(dateTime, minAmount, maxAmount, iban, offset, limit);
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Integer id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/")
    public TransactionResponseDTO createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @PostMapping("/createWithIban")
    public TransactionResponseDTO createTransactionFromIban(@RequestBody TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        return transactionService.createTransactionFromIban(transactionCreateFromIbanRequestDTO);
    }

    @GetMapping("/customer/{customerId}")
    public List<TransactionsDTO> getTransactionsByCustomerId(
            @PathVariable Integer customerId,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {

        Page<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId, offset, limit);
        return transactions.getContent().stream().map(transactionService::convertToDTO).toList();
    }

    @PostMapping("/transfer")
    public TransactionResponseDTO transferFunds(@RequestBody TransferRequestDTO transferRequestDTO) {
        return transactionService.transferFunds(transferRequestDTO);
    }
}
