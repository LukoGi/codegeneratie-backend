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

    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws AuthenticationException{
        User user = userRepository.findUserByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid Credentials"));

        String rawPassword = loginRequest.getPassword();
        String encodedPassword = user.getPassword();

        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(jwtProvider.createToken(user.getUsername(), user.getRoles()));
            response.setUserId(user.getUserId());
            response.setRoles(user.getRoles());
            response.setUsername(user.getUsername());
            return response;
        } else {
            throw new AuthenticationException("Invalid Credentials");
        }

    }

    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getUserId()).orElseThrow(EntityNotFoundException::new);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setBsnNumber(user.getBsnNumber());
        return userRepository.save(existingUser);
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(EntityNotFoundException::new);
    }

    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username).orElse(null);
    }


}
