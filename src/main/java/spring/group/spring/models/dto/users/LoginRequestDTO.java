package spring.group.spring.models.dto.users;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
