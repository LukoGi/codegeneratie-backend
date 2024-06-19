package spring.group.spring.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "app_users")
public class User {

    @Id
    @GeneratedValue
    private Integer user_id;

    @NonNull
    private String username;

    private String first_name;

    private String last_name;


    private String email;

    @NonNull
    private String password;


    private String bsn_number;


    private String phone_number;

    @NonNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @NonNull
    private Boolean is_approved;

    @NonNull
    private Boolean is_archived;

    private BigDecimal dailyTransferLimit;



    @OneToMany(mappedBy = "user")
    private Set<BankAccount> accounts = new HashSet<>();

}
