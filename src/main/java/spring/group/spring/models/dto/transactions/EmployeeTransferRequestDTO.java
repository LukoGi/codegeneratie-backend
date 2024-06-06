package spring.group.spring.models.dto.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTransferRequestDTO {
    private String fromAccountIban;
    private String toAccountIban;
    private Integer employeeId;
    private BigDecimal transferAmount;
    private String description;
}
