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
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @GeneratedValue
    private Integer account_id;

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
    private Integer pincode;

    @ManyToOne
    private User user;

    public BankAccount(@NonNull String iban, @NonNull BigDecimal balance, @NonNull String account_type, @NonNull Boolean is_active, BigDecimal absolute_limit, @NonNull Integer pincode, User user) {
        this.iban = iban;
        this.balance = balance;
        this.account_type = account_type;
        this.is_active = is_active;
        this.absolute_limit = absolute_limit;
        this.pincode = pincode;
        this.user = user;
    }
}
