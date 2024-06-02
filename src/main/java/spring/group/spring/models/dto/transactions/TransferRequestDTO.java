package spring.group.spring.models.dto.transactions;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {
    private Integer userId;
    private String fromAccountType;
    private String toAccountType;
    private BigDecimal transferAmount;
}
