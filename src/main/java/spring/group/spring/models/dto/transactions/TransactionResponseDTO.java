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
public class TransactionResponseDTO {

    private Integer to_account_id;
    private Integer from_account_id;
    private Integer initiator_user_id;
    @NotNull
    private BigDecimal transfer_amount;
    @NotNull
    private LocalDateTime date;
    @NotBlank
    private String description;
}
