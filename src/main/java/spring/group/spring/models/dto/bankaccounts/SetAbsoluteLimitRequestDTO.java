package spring.group.spring.models.dto.bankaccounts;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetAbsoluteLimitRequestDTO {
    @NotNull
    private BigDecimal absolute_limit;
}
