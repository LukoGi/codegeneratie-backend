package spring.group.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.group.spring.exception.exceptions.EntityNotFoundException;
import spring.group.spring.models.Role;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.LoginRequestDTO;
import spring.group.spring.models.dto.users.LoginResponseDTO;
import spring.group.spring.repositories.UserRepository;
import spring.group.spring.security.JwtProvider;

import javax.naming.AuthenticationException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;



    private UserService userService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1);

        assertEquals(user, result);
        verify(userRepository).findById(1);
    }
    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1));

        verify(userRepository).findById(1);
    }

    @Test
    public void testGetUnapprovedUsers() {
        List<User> users = Arrays.asList(new User(), new User());

        when(userRepository.findUnapprovedUsers()).thenReturn(users);

        List<User> result = userService.getUnapprovedUsers();

        assertEquals(users, result);
        verify(userRepository).findUnapprovedUsers();
    }

    @Test
    public void testGetAllUsers() {
        Page<User> users = new PageImpl<>(Arrays.asList(new User(), new User()));
        Pageable pageable = PageRequest.of(0, 5);

        when(userRepository.findAll(pageable)).thenReturn(users);

        Page<User> result = userService.getAllUsers(pageable);

        assertEquals(users, result);
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setFirstName("Jan");
        user.setLastName("Pan");
        user.setUsername("JanPan");
        user.setEmail("Jan@gmail.com");
        user.setPassword("test");
        user.setBsnNumber("123456789");
        user.setPhoneNumber("0612345678");
        user.setRoles(List.of(Role.ROLE_USER));
        user.setIsApproved(false);
        user.setIsArchived(false);
        user.setDailyTransferLimit(new BigDecimal("1000.00"));

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("test")).thenReturn("encodedPassword"); // changed this line
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(user);

        assertEquals("encodedPassword", result.getPassword());
        verify(userRepository).findUserByUsername(user.getUsername());
        verify(passwordEncoder).encode("test"); // changed this line
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testLogin() throws AuthenticationException {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("JaneDoe");
        loginRequest.setPassword("test");

        User user = new User();
        user.setUserId(1);
        user.setUsername("JaneDoe");
        user.setPassword("encodedPassword");
        user.setRoles(List.of(Role.ROLE_USER));

        when(userRepository.findUserByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.createToken(user.getUsername(), user.getRoles())).thenReturn("token");

        LoginResponseDTO result = userService.login(loginRequest);

        assertEquals("token", result.getToken());
        assertEquals(1, result.getUserId());
        verify(userRepository).findUserByUsername(loginRequest.getUsername());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtProvider).createToken(user.getUsername(), user.getRoles());
    }

    private User createUser() {
        User user = new User();
        user.setUserId(2);
        user.setFirstName("Jan");
        user.setLastName("Pan");
        user.setUsername("JanPan");
        user.setEmail("Jan@gmail.com");
        user.setPassword("test");
        user.setBsnNumber("123456789");
        user.setPhoneNumber("0612345678");
        user.setRoles(List.of(Role.ROLE_USER));
        user.setIsApproved(false);
        user.setIsArchived(false);
        user.setDailyTransferLimit(new BigDecimal("1000.00"));
        return user;
    }

    private User createUpdatedUser() {
        User updatedUser = new User();
        updatedUser.setUserId(2);
        updatedUser.setFirstName("JanUpdated");
        updatedUser.setLastName("PanUpdated");
        updatedUser.setUsername("JanPanUpdated");
        updatedUser.setEmail("JanUpdated@gmail.com");
        updatedUser.setPassword("testUpdated");
        updatedUser.setBsnNumber("987654321");
        updatedUser.setPhoneNumber("876543210");
        updatedUser.setRoles(List.of(Role.ROLE_USER));
        updatedUser.setIsApproved(true);
        updatedUser.setIsArchived(false);
        updatedUser.setDailyTransferLimit(new BigDecimal("2000.00"));
        return updatedUser;
    }

    @Test
    public void testUpdateUser() {
        User user = createUser();
        User updatedUser = createUpdatedUser();

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(updatedUser);

        assertEquals(updatedUser, result);
        verify(userRepository).findById(updatedUser.getUserId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User();
        user.setUserId(2);
        user.setUsername("JanPan");

        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(user.getUsername());

        assertEquals(user, result);
        verify(userRepository).findUserByUsername(user.getUsername());
    }
}
