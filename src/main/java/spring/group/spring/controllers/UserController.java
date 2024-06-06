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
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.AcceptUserRequestDTO;
import spring.group.spring.models.dto.users.LoginRequestDTO;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.models.dto.users.UserRequest;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;

    public UserController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.mapper = new ModelMapper();
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return userDTOs;

    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return userService.convertToDTO(user);
    }

    @PostMapping("/createUser")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        User newUser = userService.createUser(user);
        return mapper.map(newUser, UserDTO.class);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return mapper.map(updatedUser, UserDTO.class);
    }

    @PutMapping("/acceptUser/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserDTO acceptUser(@PathVariable Integer id, @RequestBody AcceptUserRequestDTO acceptUserRequestDTO) {
        User user = userService.getUserById(id);

        BankAccount checkingAccount = bankAccountService.createBankAccountEntity(user, AccountType.CHECKINGS, acceptUserRequestDTO.getAbsolute_transfer_limit());
        bankAccountService.createBankAccount(checkingAccount);
        BankAccount savingsAccount = bankAccountService.createBankAccountEntity(user, AccountType.SAVINGS, acceptUserRequestDTO.getAbsolute_transfer_limit());
        bankAccountService.createBankAccount(savingsAccount);

        user.setIs_approved(true);
        user.setDaily_transfer_limit(acceptUserRequestDTO.getDaily_transfer_limit());
        User updatedUser = userService.updateUser(user);

        return userService.convertToDTO(updatedUser);
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

    // TODO: integrate this into the user update endpoint
    @PutMapping("{id}/setDailyLimit")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> setDailyLimit(@PathVariable Integer id, @RequestParam BigDecimal dailyLimit) {
        User user = userService.getUserById(id);
        user.setDaily_transfer_limit(dailyLimit);
        userService.updateUser(user);
        return ResponseEntity.ok().build();
    }
}
