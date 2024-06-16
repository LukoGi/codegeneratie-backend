package spring.group.spring.models.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {

    private Integer transactionId;
    private BankAccount toAccount;
    private BankAccount fromAccount;
    private User initiatorUser;
    private BigDecimal transferAmount;
    private LocalDateTime date;
    private String description;
}

