package spring.group.spring.controllers;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.BankAccountDTO;
import spring.group.spring.models.dto.BankAccountResponseDTO;
import spring.group.spring.services.BankAccountService;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
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
