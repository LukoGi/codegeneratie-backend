package spring.group.spring.models.dto.users;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.Role;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequest {

    private String first_name;

    private String last_name;

    private String email;

    private String password;

    private String bsn_number;

    private String phone_number;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    private Boolean is_approved;

    private Boolean is_archived;

    private BigDecimal daily_transfer_limit;
}
