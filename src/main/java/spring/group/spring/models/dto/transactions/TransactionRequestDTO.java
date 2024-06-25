package spring.group.spring.models.dto.transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    private Integer toAccountId;
    private Integer fromAccountId;
    private Integer initiatorUserId;
    @NotNull
    private BigDecimal transferAmount;
    @NotNull
    private LocalDateTime date;
    @NotBlank
    private String description;
}
