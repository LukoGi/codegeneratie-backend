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
    private Integer accountId;

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
    private AccountType accountType;

    @NonNull
    @Column(nullable = false)
    @NotNull
    private Boolean isActive;

    @NonNull
    @Column(nullable = false)
    @NotNull
    private BigDecimal absoluteLimit;

    @NonNull
    @Column(nullable = false)
    @NotBlank
    private String pincode;

    @ManyToOne
    @NotNull
    private User user;

    public BankAccount(@NonNull String iban, @NonNull BigDecimal balance, @NonNull AccountType accountType, @NonNull Boolean isActive, BigDecimal absoluteLimit, @NonNull String pincode, User user) {
        this.iban = iban;
        this.balance = balance;
        this.accountType = accountType;
        this.isActive = isActive;
        this.absoluteLimit = absoluteLimit;
        this.pincode = pincode;
        this.user = user;
    }
}
