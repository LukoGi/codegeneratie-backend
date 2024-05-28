package spring.group.spring.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.dto.TransactionRequestDTO;
import spring.group.spring.models.dto.TransactionResponseDTO;
import spring.group.spring.models.dto.TransactionUpdateRequestDTO;
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

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Integer id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping
    public TransactionResponseDTO createTransaction(@Valid @RequestBody TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(transactionRequestDTO);
    }

    @PutMapping("/{id}")
    public TransactionResponseDTO updateTransaction(@PathVariable Integer id, @Valid @RequestBody TransactionUpdateRequestDTO transactionUpdateRequestDTO) {
        return transactionService.updateTransaction(id, transactionUpdateRequestDTO);
    }

    @GetMapping
    public List<Transaction> getAllTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban) {

        LocalDateTime startDateTime = (startDate != null) ? LocalDateTime.parse(startDate) : null;
        LocalDateTime endDateTime = (endDate != null) ? LocalDateTime.parse(endDate) : null;

        return transactionService.getAllTransactions(startDateTime, endDateTime, minAmount, maxAmount, iban);
    }
}
