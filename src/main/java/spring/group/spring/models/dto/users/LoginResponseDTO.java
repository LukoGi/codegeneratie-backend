package spring.group.spring.models.dto.users;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private Integer user_id;
}
