package spring.group.spring.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.exceptions.IncorrectPincodeException;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountATMLoginRequest;
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
    public ResponseEntity<BankAccountDTO> getBankAccountById(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        if (bankAccount != null) {
            return ResponseEntity.ok(bankAccountService.convertToDTO(bankAccount));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountResponseDTO> updateBankAccount(@PathVariable Integer id, @RequestBody BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = bankAccountService.convertToEntity(bankAccountDTO);
        bankAccount.setAccount_id(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        if (bankAccountResult != null) {
            return ResponseEntity.ok(bankAccountService.convertToResponseDTO(bankAccountResult));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Object> atmLogin(@RequestBody BankAccountATMLoginRequest loginRequest) {
        // TODO add JWT token
        try {
            BankAccount bankAccount = bankAccountService.atmLogin(loginRequest);
            if (bankAccount != null) {
                return ResponseEntity.ok(bankAccountService.convertToDTO(bankAccount));
            }
            return ResponseEntity.notFound().build();
        } catch (IncorrectPincodeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
