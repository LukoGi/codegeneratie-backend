package spring.group.spring.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class User {
    // TODO finish user
    @Id
    @GeneratedValue
    private int user_id;

    private String username;

    @OneToMany
    private Set<BankAccount> accounts = new HashSet<>();
}
