package spring.group.spring.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDTO {

    private Integer account_Id;

    private UserDTO user;

    private String iban;

    private BigDecimal balance;

    private String account_Type;

    private Boolean is_Active;

    private BigDecimal absolute_Limit;

    private Integer pincode;

}
