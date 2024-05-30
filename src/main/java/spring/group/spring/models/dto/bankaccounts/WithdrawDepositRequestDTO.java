package spring.group.spring.models.dto.bankaccounts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawDepositRequestDTO {
    @NotNull
    private BigDecimal amount;
}
