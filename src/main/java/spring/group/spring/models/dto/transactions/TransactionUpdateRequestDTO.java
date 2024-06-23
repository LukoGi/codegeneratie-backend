package spring.group.spring.models.dto.transactions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequestDTO {

    @NonNull
    @NotNull
    private Integer toAccountId;

    @NonNull
    @NotNull
    private Integer fromAccountId;

    @NonNull
    @NotNull
    private Integer initiatorUserId;

    @NonNull
    @NotNull
    private BigDecimal transferAmount;

    @NonNull
    @NotNull
    private LocalDateTime date;

    @NonNull
    @NotNull
    private String description;
}