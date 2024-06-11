package spring.group.spring.models.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionOverviewResponseDTO {
    private LocalDateTime date;
    private BigDecimal transferAmount;
    private String description;
    private String recipientName;
    private String fromAccountIban;
    private String toAccountIban;
    private String initiatorName;
}
