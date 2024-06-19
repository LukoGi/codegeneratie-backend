package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptUserRequestDTO {
    private BigDecimal dailyTransferLimit;
    private BigDecimal absoluteLimit;
}
