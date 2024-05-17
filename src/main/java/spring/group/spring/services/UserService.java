package spring.group.spring.services;

import org.springframework.stereotype.Service;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.users.UserDTO;
import spring.group.spring.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

// TODO User: add all user fields when done and add all UserConvert functions, see BankAccountService for example

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(user.getUser_id());
        userDTO.setFirst_name(user.getFirst_name());
        userDTO.setLast_name(user.getLast_name());
        return userDTO;
    }
}
