package spring.group.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue
    private Integer user_id;

    private String username;

    private String first_name;

    private String last_name;

    @NonNull
    private String email;

    @NonNull
    private String password;

    @NonNull
    private String bsn_number;

    @NonNull
    private String phone_number;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    private Boolean is_approved;

    private Boolean is_archived;

    private BigDecimal daily_transfer_limit;



    @OneToMany(mappedBy = "user")
    private Set<BankAccount> accounts = new HashSet<>();
}
