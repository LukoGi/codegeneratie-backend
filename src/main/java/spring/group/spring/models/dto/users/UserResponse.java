package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private String username;

    private String first_name;

    private String last_name;

    private String email;

    private String password;

    private String bsn_number;

    private String phone_number;
}
