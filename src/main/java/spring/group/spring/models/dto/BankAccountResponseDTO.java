package spring.group.spring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountResponseDTO {
    private Integer account_id;

    private UserDTO user;

    private String iban;

    private BigDecimal balance;

    private String account_Type;

    private Boolean is_Active;

    private BigDecimal absolute_Limit;
}
