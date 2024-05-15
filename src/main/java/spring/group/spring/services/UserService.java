package spring.group.spring.services;

import spring.group.spring.models.User;
import spring.group.spring.models.dto.UserDTO;

public class UserService {

// TODO User: add all user fields when done and add all UserConvert functions, see BankAccountService for example

    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(user.getUser_id());
        userDTO.setFirst_name(user.getFirst_name());
        return userDTO;
    }
}
