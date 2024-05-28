package spring.group.spring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateRequestDTO {

    private String to_account_iban;
    private Integer initiator_user_id;
    private BigDecimal transfer_amount;
    private String description;
}
