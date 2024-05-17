package spring.group.spring.controllers;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountDTO;
import spring.group.spring.models.dto.bankaccounts.BankAccountRequestDTO;
import spring.group.spring.models.dto.bankaccounts.BankAccountResponseDTO;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    public BankAccountController(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public BankAccountDTO getBankAccountById(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        return bankAccountService.convertToDTO(bankAccount);
    }

    @PutMapping("/{id}")
    public BankAccountResponseDTO updateBankAccount(@PathVariable Integer id, @RequestBody BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = bankAccountService.convertToEntity(bankAccountDTO);
        bankAccount.setAccount_id(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        return bankAccountService.convertToResponseDTO(bankAccountResult);
    }
}
