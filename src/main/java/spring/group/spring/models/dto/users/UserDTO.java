package spring.group.spring.models.dto.users;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.Role;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String bsnNumber;

    private String phoneNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    private Boolean isApproved;

    private Boolean isArchived;

    private BigDecimal dailyTransferLimit;

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Role> getRoles() {
        return roles;
    }
}
