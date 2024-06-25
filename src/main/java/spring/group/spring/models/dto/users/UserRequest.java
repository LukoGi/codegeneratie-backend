package spring.group.spring.models.dto.users;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String bsnNumber;

    @NotBlank
    private String phoneNumber;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @NotNull
    private Boolean isApproved;

    @NotNull
    private Boolean isArchived;

    private BigDecimal dailyTransferLimit;
}

