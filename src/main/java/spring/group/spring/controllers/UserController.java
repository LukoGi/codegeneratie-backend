package spring.group.spring.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.models.dto.users.UserRequest;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hibernate.annotations.UuidGenerator.Style.RANDOM;

@RestController
@RequestMapping("users")
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
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return ResponseEntity.ok(userDTOs);
    }

    @PostMapping("/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(userService.convertToDTO(newUser));
    }

    @PutMapping("/acceptUser/{id}")
    public ResponseEntity<UserDTO> acceptUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        BankAccount checkingAccount = createBankAccount(user, AccountType.CHECKINGS);
        bankAccountService.createBankAccount(checkingAccount);
        BankAccount savingsAccount = createBankAccount(user, AccountType.SAVINGS);
        bankAccountService.createBankAccount(savingsAccount);

        user.setIs_approved(true);
        User updatedUser = userService.updateUser(user);

        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
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
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
    }
}
