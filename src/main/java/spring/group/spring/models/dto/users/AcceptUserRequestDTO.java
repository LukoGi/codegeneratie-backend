package spring.group.spring.models.dto.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcceptUserRequestDTO {
    private BigDecimal daily_transfer_limit;
    private BigDecimal absolute_transfer_limit;
}
