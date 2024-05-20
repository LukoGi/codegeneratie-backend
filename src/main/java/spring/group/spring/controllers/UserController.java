package spring.group.spring.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.LoginRequestDTO;
import spring.group.spring.models.dto.users.LoginResponseDTO;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.models.dto.users.UserRequest;
import spring.group.spring.services.UserService;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping()
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userService.convertToDTO(user));
    }

    @GetMapping("users/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            userDTOs.add(userService.convertToDTO(user));
        }
        return ResponseEntity.ok(userDTOs);
    }

    @PostMapping("users/createUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(userService.convertToDTO(newUser));
    }

    @PutMapping("users/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) throws
            AuthenticationException {
        return userService.login(loginRequest);
    }

    @GetMapping("/home")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String inside() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "Yippie je heb het goed gedaan \n Je bent ingelogd als " + auth.getName() + " met de rol " + auth.getAuthorities().toArray()[0] + "!";
    }





}
