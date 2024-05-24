package spring.group.spring.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.services.BankAccountService;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
        this.mapper = new ModelMapper();
    }

    @GetMapping("/all")
    public List<BankAccountResponseDTO> getAllBankAccounts() {
        return bankAccountService.convertToResponseDTO(bankAccountService.getAllBankAccounts());
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponseDTO createBankAccount(@RequestBody BankAccount bankAccount) {
        if (!bankAccountService.checkIban(bankAccount.getIban())) {
            throw new IllegalArgumentException("Invalid IBAN");
        }

        BankAccount bankAccountResult = bankAccountService.createBankAccount(bankAccount);
        return mapper.map(bankAccountResult, BankAccountResponseDTO.class);
    }

    @GetMapping("/{id}")
    public BankAccountDTO getBankAccountById(@PathVariable Integer id) {

        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        return mapper.map(bankAccount, BankAccountDTO.class);
    }

    @PutMapping("/{id}")
    public BankAccountResponseDTO updateBankAccount(@PathVariable Integer id, @RequestBody BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = mapper.map(bankAccountDTO, BankAccount.class);
        bankAccount.setAccount_id(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        return mapper.map(bankAccountResult, BankAccountResponseDTO.class);
    }

    @PostMapping("/login")
    public BankAccountDTO atmLogin(@RequestBody BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountService.atmLogin(loginRequest);
        return mapper.map(bankAccount, BankAccountDTO.class);
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
