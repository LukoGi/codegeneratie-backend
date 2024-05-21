package spring.group.spring.models.dto.users;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import spring.group.spring.models.Role;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer user_id;

    private String username;

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

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
