package spring.group.spring.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.services.BankAccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;

    // K - added Pageable
    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankAccountResponseDTO> getAllBankAccounts(Pageable pageable) {
        Page<BankAccount> bankAccounts = bankAccountService.getAllBankAccounts(pageable);
        return bankAccounts.getContent().stream()
                .map(bankAccount -> mapper.map(bankAccount, BankAccountResponseDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BankAccountDTO getBankAccountById(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        return mapper.map(bankAccount, BankAccountDTO.class);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<String> getIbanByUsername(@PathVariable String username) {
        return bankAccountService.getIbanByUsername(username);
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public BankAccountResponseDTO createBankAccount(@Valid @RequestBody BankAccount bankAccount) {
        BankAccount bankAccountResult = bankAccountService.createBankAccount(bankAccount);
        return mapper.map(bankAccountResult, BankAccountResponseDTO.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BankAccountResponseDTO updateBankAccount(@PathVariable Integer id, @Valid @RequestBody BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = mapper.map(bankAccountDTO, BankAccount.class);
        bankAccount.setAccountId(id);
        BankAccount bankAccountResult = bankAccountService.updateBankAccount(bankAccount);
        return mapper.map(bankAccountResult, BankAccountResponseDTO.class);
    }

    @PostMapping("/login")
    public BankAccountATMLoginResponse atmLogin(@Valid @RequestBody BankAccountATMLoginRequest loginRequest) {
        return bankAccountService.atmLogin(loginRequest);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BankAccountResponseDTO> getBankAccountsByUserId(@PathVariable Integer userId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByUserId(userId);
        return bankAccounts.stream()
                .map(bankAccount -> mapper.map(bankAccount, BankAccountResponseDTO.class))
                .collect(Collectors.toList());
    }

    // K - Note: CustomPermissionEvaluator usage
    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isUserAccountOwner(authentication, #id)")
    public WithdrawDepositResponseDTO withdrawMoney(@PathVariable Integer id, @Valid @RequestBody WithdrawDepositRequestDTO withdrawRequest) {
        return bankAccountService.withdrawMoney(id, withdrawRequest.getAmount());
    }

    @PostMapping("/{id}/deposit")
    @PreAuthorize("hasRole('ROLE_USER') and @customPermissionEvaluator.isUserAccountOwner(authentication, #id)")
    public WithdrawDepositResponseDTO depositMoney(@PathVariable Integer id, @Valid @RequestBody WithdrawDepositRequestDTO depositRequest) {
        return bankAccountService.depositMoney(id, depositRequest.getAmount());
    }

    @PutMapping("/{id}/setAbsoluteLimit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void setAbsoluteLimit(@PathVariable Integer id, @RequestParam BigDecimal absoluteLimit) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        if (absoluteLimit.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Absolute limit must be lesser than or equal to 0");
        }
        bankAccount.setAbsoluteLimit(absoluteLimit);
        bankAccountService.updateBankAccount(bankAccount);
    }

    @PutMapping("/{id}/closeAccount")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> closeAccount(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccountService.getBankAccountById(id);
        bankAccount.setIsActive(false);
        bankAccountService.updateBankAccount(bankAccount);
        return ResponseEntity.ok().build();
    }

}
