package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer user_id;

    private String first_name;

    private String last_name;

    private String email;

    private String password;

    private String bsn_number;

    private String phone_number;

    private String role;

    private Boolean is_approved;

    private Boolean is_archived;

    private BigDecimal daily_transfer_limit;
}
