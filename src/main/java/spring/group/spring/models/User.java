package spring.group.spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name = "app_users")
public class User {
    // TODO User: finish model
    @Id
    @GeneratedValue
    private Integer user_id;

    private String first_name;

    @OneToMany(mappedBy = "user")
    private Set<BankAccount> accounts = new HashSet<>();
}
