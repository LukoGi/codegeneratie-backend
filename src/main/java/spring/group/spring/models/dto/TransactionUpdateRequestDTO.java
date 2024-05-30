package spring.group.spring.models.dto;

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
    private Integer to_account_id;

    @NonNull
    @NotNull
    private Integer from_account_id;

    @NonNull
    @NotNull
    private Integer initiator_user_id;

    @NonNull
    @NotNull
    private BigDecimal transfer_amount;

    @NonNull
    @NotNull
    private LocalDateTime date;

    @NonNull
    @NotNull
    private String description;
}