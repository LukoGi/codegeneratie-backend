package spring.group.spring.controllers;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.BankAccountDTO;
import spring.group.spring.models.dto.BankAccountResponseDTO;
import spring.group.spring.services.BankAccountService;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping
    public List<BankAccountDTO> getAllBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts();
        return bankAccounts.stream().map(bankAccountService::convertToDTO).toList();
    }
    @PostMapping
    public BankAccountResponseDTO createBankAccount(@RequestBody BankAccount bankAccount) {
        BankAccount bankAccountResult = bankAccountService.createBankAccount(bankAccount);
        return bankAccountService.convertToResponseDTO(bankAccountResult);
    }

    @GetMapping("/{id}")
    public BankAccountDTO getBankAccountById(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        return bankAccountService.convertToDTO(bankAccount);
    }

    @PutMapping("/{id}")
    public BankAccountResponseDTO updateBankAccount(@PathVariable Integer id, @RequestBody BankAccount bankAccount) {
        bankAccount.setAccount_id(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        return bankAccountService.convertToResponseDTO(bankAccountResult);
    }
}
