package spring.group.spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "toAccountId", nullable = true)
    private BankAccount toAccount;

    @ManyToOne
    @JoinColumn(name = "fromAccountId", nullable = true)
    private BankAccount fromAccount;

    @ManyToOne
    @JoinColumn(name = "initiatorUserId", nullable = true)
    private User initiatorUser;

    private BigDecimal transferAmount;
    private LocalDateTime date;
    private String description;
}
