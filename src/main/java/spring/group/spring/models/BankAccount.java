package spring.group.spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class BankAccount {

    @Id
    @GeneratedValue
    private int account_id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String iban;

    @NonNull
    @Column(nullable = false)
    private BigDecimal balance;

    @NonNull
    @Column(nullable = false)
    private String account_type;

    @NonNull
    @Column(nullable = false)
    private Boolean is_active;

    private BigDecimal absolute_limit;

    @NonNull
    @Column(nullable = false)
    private int pincode;

    @ManyToOne
    private User user;
}
