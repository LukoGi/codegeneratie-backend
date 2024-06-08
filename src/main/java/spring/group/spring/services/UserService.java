package spring.group.spring.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import spring.group.spring.exception.exceptions.EntityNotFoundException;

import spring.group.spring.models.Role;

import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.*;
import spring.group.spring.repositories.UserRepository;
import spring.group.spring.security.JwtProvider;

import javax.naming.AuthenticationException;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<User> getUnapprovedUsers() {
        return userRepository.findUnapprovedUsers();
    }

    public User archiveUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        user.setIs_archived(true);
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User createUser(User user) {
        if (!userRepository.findUserByUsername(user.getUsername()).isEmpty()) {
            throw new IllegalArgumentException("Username is already taken");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws AuthenticationException {
        User user = userRepository.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("User not found"));

        String rawPassword = loginRequest.getPassword();
        String encodedPassword = user.getPassword();

        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwtProvider.createToken(user.getUsername(), user.getRoles()));
            response.setUser_id(user.getUser_id());
            response.setRoles(user.getRoles());
            return response;
        } else {
            throw new AuthenticationException("Invalid password");
        }

    }

    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUser_id()).orElseThrow(EntityNotFoundException::new);

        existingUser.setFirst_name(user.getFirst_name());
        existingUser.setLast_name(user.getLast_name());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone_number(user.getPhone_number());
        existingUser.setBsn_number(user.getBsn_number());
        return userRepository.save(existingUser);
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    public List<User> getUsersWithoutBankAccount() {
        if(userRepository.findByAccountsIsEmpty(Role.ROLE_USER).isEmpty()) {
            throw new EntityNotFoundException();
        }
        return userRepository.findByAccountsIsEmpty(Role.ROLE_USER);
    }
}
