package spring.group.spring.controllers;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.services.BankAccountService;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/all")
    public List<BankAccountResponseDTO> getAllBankAccounts() {
        return bankAccountService.convertToResponseDTO(bankAccountService.getAllBankAccounts());
    }

    @PostMapping("/create")
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
    public BankAccountResponseDTO updateBankAccount(@PathVariable Integer id, @RequestBody BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = bankAccountService.convertToEntity(bankAccountDTO);
        bankAccount.setAccount_id(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        return bankAccountService.convertToResponseDTO(bankAccountResult);
    }

    @PostMapping("/login")
    public BankAccountDTO atmLogin(@RequestBody BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountService.atmLogin(loginRequest);
        return bankAccountService.convertToDTO(bankAccount);
    }

    @PostMapping("/{id}/withdraw")
    public WithdrawDepositResponseDTO withdrawMoney(@PathVariable Integer id, @RequestBody WithdrawDepositRequestDTO withdrawRequest) {
        return bankAccountService.withdrawMoney(id, withdrawRequest.getAmount());
    }

    @PostMapping("/{id}/deposit")
    public WithdrawDepositResponseDTO depositMoney(@PathVariable Integer id, @RequestBody WithdrawDepositRequestDTO depositRequest) {
        return bankAccountService.depositMoney(id, depositRequest.getAmount());
    }

}
