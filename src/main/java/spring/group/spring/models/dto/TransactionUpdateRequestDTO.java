package spring.group.spring.models.dto;

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
    private Integer to_account_id;

    @NonNull
    private Integer from_account_id;

    @NonNull
    private Integer initiator_user_id;

    @NonNull
    private BigDecimal transfer_amount;

    @NonNull
    private LocalDateTime start_date;

    @NonNull
    private LocalDateTime end_date;

    @NonNull
    private String description;
}