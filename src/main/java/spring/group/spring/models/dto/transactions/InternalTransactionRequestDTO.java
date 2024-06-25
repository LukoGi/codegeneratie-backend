package spring.group.spring.models.dto.transactions;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InternalTransactionRequestDTO {
    private Integer initiatorUserId;
    private String fromAccountType;
    private String toAccountType;
    private BigDecimal transferAmount;
}
