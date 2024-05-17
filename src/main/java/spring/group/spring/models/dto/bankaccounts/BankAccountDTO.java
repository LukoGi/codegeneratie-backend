package spring.group.spring.models.dto.bankaccounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountDTO {

    private Integer account_Id;

    private Integer user_id;

    private String iban;

    private BigDecimal balance;

    private String account_type;

    private Boolean is_active;

    private BigDecimal absolute_limit;

    private String pincode;

}
