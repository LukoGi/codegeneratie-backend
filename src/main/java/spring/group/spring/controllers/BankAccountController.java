package spring.group.spring.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;

import org.springframework.http.HttpStatus;

import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import spring.group.spring.exception.exceptions.AccessDeniedException;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.security.JwtProvider;
import spring.group.spring.services.BankAccountService;

import java.util.List;

@RestController
@RequestMapping("accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;
    private final JwtProvider jwtProvider;

    public BankAccountController(BankAccountService bankAccountService, JwtProvider jwtProvider) {
        this.bankAccountService = bankAccountService;
        this.mapper = new ModelMapper();
        this.jwtProvider = jwtProvider;
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
    public BankAccountATMLoginResponse atmLogin(@RequestBody BankAccountATMLoginRequest loginRequest) {
        BankAccountATMLoginResponse bankAccount = bankAccountService.atmLogin(loginRequest);
        return bankAccount;
    }

    @PostMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('ROLE_USER')")
    public WithdrawDepositResponseDTO withdrawMoney(@PathVariable Integer id, @RequestBody WithdrawDepositRequestDTO withdrawRequest, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtProvider.getUsernameFromToken(token);
        bankAccountService.isUserAccountOwner(username, id);
        return bankAccountService.withdrawMoney(id, withdrawRequest.getAmount());
    }

    @PostMapping("/{id}/deposit")
    @PreAuthorize("hasRole('ROLE_USER')")
    public WithdrawDepositResponseDTO depositMoney(@PathVariable Integer id, @RequestBody WithdrawDepositRequestDTO depositRequest, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtProvider.getUsernameFromToken(token);
        bankAccountService.isUserAccountOwner(username, id);
        return bankAccountService.depositMoney(id, depositRequest.getAmount());
    }



}
