package spring.group.spring.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Integer account_id;

    @NonNull
    @Column(nullable = false, unique = true)
    @NotBlank
    private String iban;

    @NonNull
    @Column(nullable = false)
    @NotNull
    private BigDecimal balance;

    @NonNull
    @Column(nullable = false)
    @NotNull
    private AccountType account_type;

    @NonNull
    @Column(nullable = false)
    @NotNull
    private Boolean is_active;

    private BigDecimal absolute_limit;

    @NonNull
    @Column(nullable = false)
    @NotBlank
    private String pincode;

    @ManyToOne
    @NotNull
    private User user;

    public BankAccount(@NonNull String iban, @NonNull BigDecimal balance, @NonNull AccountType account_type, @NonNull Boolean is_active, BigDecimal absolute_limit, @NonNull String pincode, User user) {
        this.iban = iban;
        this.balance = balance;
        this.account_type = account_type;
        this.is_active = is_active;
        this.absolute_limit = absolute_limit;
        this.pincode = pincode;
        this.user = user;
    }
}
