package spring.group.spring.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.models.dto.users.UserRequest;
import spring.group.spring.services.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequestDTO) {
        User user = userService.convertToEntity(userRequestDTO);
        user.setUser_id(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(userService.convertToDTO(updatedUser));
    }



}
