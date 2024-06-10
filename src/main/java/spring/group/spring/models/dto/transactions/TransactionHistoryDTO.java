package spring.group.spring.models.dto.transactions;

import lombok.Data;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryDTO {
    private BankAccount to_account;
    private User initiator_user;
    private BigDecimal transfer_amount;
    private LocalDateTime date;
    private String description;
}
