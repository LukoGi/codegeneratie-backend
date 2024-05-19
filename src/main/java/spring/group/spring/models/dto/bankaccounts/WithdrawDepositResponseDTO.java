package spring.group.spring.models.dto.bankaccounts;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawDepositResponseDTO {
    private BigDecimal balance;
}
