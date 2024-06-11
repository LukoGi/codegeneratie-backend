package spring.group.spring.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<TransactionOverviewDTO> getAllTransactions(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        LocalDateTime dateTime = (date != null) ? LocalDateTime.parse(date) : null;

        Page<Transaction> transactions = transactionService.getAllTransactions(dateTime, minAmount, maxAmount, iban, offset, limit);
        return transactions.map(transactionService::convertToDTO);
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
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isRequestValid(authentication, #transactionCreateFromIbanRequestDTO.initiator_user_id)")
    public TransactionResponseDTO createTransactionFromIban(@RequestBody TransactionCreateFromIbanRequestDTO transactionCreateFromIbanRequestDTO) {
        return transactionService.createTransactionFromIban(transactionCreateFromIbanRequestDTO);
    }

    @GetMapping("/customer/{customerId}")
    public List<TransactionOverviewDTO> getTransactionsByCustomerId(
            @PathVariable Integer customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        Page<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId, startDate, endDate, minAmount, maxAmount, iban, offset, limit);
        return transactions.getContent().stream().map(transactionService::convertToDTO).toList();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isRequestValid(authentication, #transferRequestDTO.userId)")
    public TransactionResponseDTO transferFunds(@RequestBody TransferRequestDTO transferRequestDTO) {
        return transactionService.transferFunds(transferRequestDTO);
    }

    @PostMapping("/employeeTransfer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TransactionResponseDTO employeeTransferFunds(@RequestBody EmployeeTransferRequestDTO employeeTransferRequestDTO) {
        return transactionService.employeeTransferFunds(employeeTransferRequestDTO);
    }
}
