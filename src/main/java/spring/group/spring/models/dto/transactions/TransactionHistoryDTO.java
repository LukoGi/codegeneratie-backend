package spring.group.spring.models.dto.transactions;

import lombok.Data;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryDTO {
    private BankAccount toAccount;
    private User initiatorUser;
    private BigDecimal transferAmount;
    private LocalDateTime date;
    private String description;
}
