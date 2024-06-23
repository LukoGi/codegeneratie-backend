package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponse {

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String bsnNumber;

    private String phoneNumber;
}
