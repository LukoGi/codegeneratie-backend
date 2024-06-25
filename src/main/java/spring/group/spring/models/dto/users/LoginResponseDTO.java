package spring.group.spring.models.dto.users;

import lombok.Data;
import spring.group.spring.models.Role;

import java.util.List;

@Data
public class LoginResponseDTO {
    private String token;
    private Integer userId;
    private List<Role> roles;
    private String username;
}
