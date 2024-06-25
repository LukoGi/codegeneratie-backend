package spring.group.spring.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TransactionDTO> getAllTransactions(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        LocalDateTime dateTime = (date != null) ? LocalDateTime.parse(date) : null;

        Page<Transaction> transactions = transactionService.getAllTransactions(dateTime, minAmount, maxAmount, iban, offset, limit);
        return transactions.getContent().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    public TransactionResponseDTO createTransaction(@Valid @RequestBody TransactionRequestDTO transactionResponseDTO) {
        return transactionService.createTransaction(transactionResponseDTO);
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<TransactionDTO> getTransactionsByAccountId(
            @PathVariable Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        Page<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId, startDate, endDate, minAmount, maxAmount, iban, offset, limit);
        return transactions.getContent().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TransactionDTO> getTransactionsByUserId(
            @PathVariable Integer userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String iban,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "10") Integer limit) {
        Page<Transaction> transactions = transactionService.getTransactionsByUserId(userId, startDate, endDate, minAmount, maxAmount, iban, offset, limit);
        return transactions.getContent().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();
    }

    // Luko - Begin

    @PostMapping("/customer")
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isRequestValid(authentication, #customerTransactionRequestDTO.initiatorUserId)")
    public TransactionResponseDTO customerCreateTransaction(@RequestBody CustomerTransactionRequestDTO customerTransactionRequestDTO) {
        return transactionService.customerCreateTransaction(customerTransactionRequestDTO);
    }

    @PostMapping("/customer/internal")
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isRequestValid(authentication, #internalTransactionRequestDTO.initiatorUserId)")
    public TransactionResponseDTO customerCreateInternalTransaction(@RequestBody InternalTransactionRequestDTO internalTransactionRequestDTO) {
        return transactionService.customerCreateInternalTransaction(internalTransactionRequestDTO);
    }

    @PostMapping("/employee")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TransactionResponseDTO employeeCreateTransaction(@RequestBody EmployeeTransactionRequestDTO employeeTransactionRequestDTO) {
        return transactionService.employeeCreateTransaction(employeeTransactionRequestDTO);
    }

    // Luko - Einde
}
