package spring.group.spring.controllers;


import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.SetDailyLimitRequestDTO;
import spring.group.spring.models.dto.users.AcceptUserRequestDTO;
import spring.group.spring.models.dto.users.LoginRequestDTO;
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

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    // prob not this for pincode but aight
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;

    public UserController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.mapper = new ModelMapper();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return userService.convertToDTO(user);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return userDTOs;

    }

    @PostMapping("/createUser")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        User newUser = userService.createUser(user);
        return mapper.map(newUser, UserDTO.class);
    }

    @PutMapping("/acceptUser/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDTO acceptUser(@PathVariable Integer id, @RequestBody AcceptUserRequestDTO acceptUserRequestDTO) {
        User user = userService.getUserById(id);

        BankAccount checkingAccount = createBankAccount(user, AccountType.CHECKINGS, acceptUserRequestDTO.getAbsolute_transfer_limit());
        bankAccountService.createBankAccount(checkingAccount);
        BankAccount savingsAccount = createBankAccount(user, AccountType.SAVINGS, acceptUserRequestDTO.getAbsolute_transfer_limit());
        bankAccountService.createBankAccount(savingsAccount);

        user.setIs_approved(true);
        user.setDaily_transfer_limit(acceptUserRequestDTO.getDaily_transfer_limit());
        User updatedUser = userService.updateUser(user);

        return userService.convertToDTO(updatedUser);
    }

    private BankAccount createBankAccount(User user, AccountType accountType, BigDecimal absolute_limit) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("NL" + (100000000 + SECURE_RANDOM.nextInt(900000000)));
        bankAccount.setUser(user);
        bankAccount.setIs_active(true);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setPincode(String.format("%04d", SECURE_RANDOM.nextInt(10000)));
        bankAccount.setAccount_type(accountType);
        bankAccount.setAbsolute_limit(absolute_limit);

        return bankAccount;
    }


    @PutMapping("/updateUser/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest userRequestDTO) {
        if (userRequestDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UserRequest cannot be null");
        }
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return mapper.map(updatedUser, UserDTO.class);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            return ResponseEntity.ok(userService.login(loginRequest));
        } catch (AuthenticationException | JwtException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/userinfo")
    public ResponseEntity<UserDTO> getMyUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        UserDTO userDTO = userService.convertToDTO(user);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/getUnapprovedUsers")
    public List<UserDTO> getUnapprovedUsers() {
        List<User> users = userService.getUnapprovedUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return userDTOs;
    }

    @GetMapping("/getUsersWithoutBankAccount")
    public List<UserDTO> getUsersWithoutBankAccount() {
        List<User> users = userService.getUsersWithoutBankAccount();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return userDTOs;
    }

    // TODO: inegrate this into the user update endpoint
    @PutMapping("{id}/setDailyLimit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> setDailyLimit(@PathVariable Integer id, @RequestParam BigDecimal dailyLimit) {
        User user = userService.getUserById(id);
        user.setDaily_transfer_limit(dailyLimit);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok().build();
    }


}
