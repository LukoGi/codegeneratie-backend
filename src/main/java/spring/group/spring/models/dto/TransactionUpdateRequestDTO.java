package spring.group.spring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionUpdateRequestDTO {

    private Integer to_account_id;
    private Integer from_account_id;
    private Integer initiator_user_id;
    private BigDecimal transfer_amount;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private String description;
}
