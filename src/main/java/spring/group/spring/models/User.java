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
    private Integer userId;

    @NonNull
    private String username;

    private String firstName;

    private String lastName;


    private String email;

    @NonNull
    private String password;


    private String bsnNumber;


    private String phoneNumber;

    @NonNull
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    @NonNull
    private Boolean isApproved;

    @NonNull
    private Boolean isArchived;

    private BigDecimal dailyTransferLimit;

    @OneToMany(mappedBy = "user")
    private Set<BankAccount> accounts = new HashSet<>();

}
