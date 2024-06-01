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
public class TransactionsDTO {
    private Integer transaction_id;
    private BankAccount to_account;
    private BankAccount from_account;
    private User initiator_user;
    private BigDecimal transfer_amount;
    private LocalDateTime date;
    private String description;
    private String recipientName;
    private String initiatorName;
}
