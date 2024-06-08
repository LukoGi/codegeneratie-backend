package spring.group.spring.controllers;


import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.*;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final ModelMapper mapper;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        return users.getContent().stream()
                .map(user -> mapper.map(user, UserDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return mapper.map(user, UserDTO.class);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(@Valid @RequestBody UserRequest userRequestDTO) {
        User user = mapper.map(userRequestDTO, User.class);
        User newUser = userService.createUser(user);
        return mapper.map(newUser, UserDTO.class);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequest userRequestDTO) {
        User user = mapper.map(userRequestDTO, User.class);
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

        return mapper.map(updatedUser, UserDTO.class);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest) throws AuthenticationException {
            return userService.login(loginRequest);
    }

    @GetMapping("/userinfo")
    public UserDTO getMyUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByUsername(auth.getName());
        return mapper.map(user, UserDTO.class);
    }

    @GetMapping("/getUnapprovedUsers")
    public List<UserDTO> getUnapprovedUsers() {
        List<User> users = userService.getUnapprovedUsers();
        return users.stream().map(user -> mapper.map(user, UserDTO.class)).toList();
    }

    @GetMapping("/getUsersWithoutBankAccount")
    public List<UserDTO> getUsersWithoutBankAccount() {
        List<User> users = userService.getUsersWithoutBankAccount();
        return users.stream().map(user -> mapper.map(user, UserDTO.class)).toList();
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

    @GetMapping("/myAccounts")
    public List<BankAccount> getBankAccountsByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.getUserByUsername(username);
        return bankAccountService.getBankAccountsByUserId(user.getUser_id());
    }
}
