package spring.group.spring.models.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetDailyLimitRequestDTO {
    private BigDecimal dailyLimit;
}
