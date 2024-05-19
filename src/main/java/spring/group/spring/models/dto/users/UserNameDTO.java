package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNameDTO {
    private Integer user_id;
    private String first_name;
    private String last_name;
}
