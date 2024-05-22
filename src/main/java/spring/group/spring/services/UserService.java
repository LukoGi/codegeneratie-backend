package spring.group.spring.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import spring.group.spring.exception.exceptions.EntityNotFoundException;

import spring.group.spring.models.Role;

import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.*;
import spring.group.spring.repositories.UserRepository;
import spring.group.spring.security.JwtProvider;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
  
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        if (!userRepository.findUserByUsername(user.getUsername()).isEmpty()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        user.setRoles(Arrays.asList(Role.ROLE_USER));
        user.setIs_approved(false);
        user.setIs_archived(false);
        user.setDaily_transfer_limit(BigDecimal.valueOf(1000.00));
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

    // TODO: Modelmapper
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(user.getUser_id());
        userDTO.setFirst_name(user.getFirst_name());
        userDTO.setLast_name(user.getLast_name());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone_number(user.getPhone_number());
        userDTO.setBsn_number(user.getBsn_number());
        userDTO.setRoles(user.getRoles());
        userDTO.setIs_approved(user.getIs_approved());
        userDTO.setIs_archived(user.getIs_archived());
        userDTO.setDaily_transfer_limit(user.getDaily_transfer_limit());

        return userDTO;
    }

    public UserRequest convertToRequestDTO(User user) {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirst_name(user.getFirst_name());
        userRequest.setLast_name(user.getLast_name());
        userRequest.setEmail(user.getEmail());
        userRequest.setPhone_number(user.getPhone_number());
        userRequest.setBsn_number(user.getBsn_number());
        userRequest.setRoles(user.getRoles());
        userRequest.setIs_approved(user.getIs_approved());
        userRequest.setIs_archived(user.getIs_archived());
        userRequest.setDaily_transfer_limit(user.getDaily_transfer_limit());

        return userRequest;
    }

    public UserResponse convertToResponseDTO(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFirst_name(user.getFirst_name());
        userResponse.setLast_name(user.getLast_name());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone_number(user.getPhone_number());
        userResponse.setBsn_number(user.getBsn_number());

        return userResponse;
    }

    public UserNameDTO convertToNameDTO(User user) {
        UserNameDTO userNameDTO = new UserNameDTO();
        userNameDTO.setUser_id(user.getUser_id());
        userNameDTO.setFirst_name(user.getFirst_name());
        userNameDTO.setLast_name(user.getLast_name());

        return userNameDTO;
    }

    public User convertToEntity(UserRequest userRequest) {
        User user = new User();
        user.setFirst_name(userRequest.getFirst_name());
        user.setLast_name(userRequest.getLast_name());
        user.setEmail(userRequest.getEmail());
        user.setPhone_number(userRequest.getPhone_number());
        user.setBsn_number(userRequest.getBsn_number());

        return user;
    }
}
