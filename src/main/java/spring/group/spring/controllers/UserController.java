package spring.group.spring.controllers;


import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.LoginRequestDTO;
import spring.group.spring.models.dto.users.LoginResponseDTO;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.models.dto.users.UserRequest;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.security.SecureRandom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import java.util.Random;


@RestController
@RequestMapping()
public class UserController {

    private final UserService userService;
    // prob not this for pincode but aight
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final BankAccountService bankAccountService;

    public UserController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return userService.convertToDTO(user);
    }


    @GetMapping("/all")
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return userDTOs;
    }

    @PostMapping("/createUser")
    public UserDTO createUser(@RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        User newUser = userService.createUser(user);
        return userService.convertToDTO(newUser);
    }

    @PutMapping("/acceptUser/{id}")
    public UserDTO acceptUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        BankAccount checkingAccount = createBankAccount(user, AccountType.CHECKINGS);
        bankAccountService.createBankAccount(checkingAccount);
        BankAccount savingsAccount = createBankAccount(user, AccountType.SAVINGS);
        bankAccountService.createBankAccount(savingsAccount);

        user.setIs_approved(true);
        User updatedUser = userService.updateUser(user);

        return userService.convertToDTO(updatedUser);
    }

    private BankAccount createBankAccount(User user, AccountType accountType) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("NL" + (100000000 + SECURE_RANDOM.nextInt(900000000)));
        bankAccount.setUser(user);
        bankAccount.setIs_active(true);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setPincode(String.format("%04d", SECURE_RANDOM.nextInt(10000)));
        bankAccount.setAccount_type(accountType);

        return bankAccount;
    }


    @PutMapping("/updateUser/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return userService.convertToDTO(updatedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            return ResponseEntity.ok(userService.login(loginRequest));
        } catch (AuthenticationException | JwtException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/home")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> inside() {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "test");
        return ResponseEntity.ok(responseBody);
    }

}
