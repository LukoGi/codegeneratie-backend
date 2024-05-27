package spring.group.spring.models.dto.bankaccounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.dto.users.UserNameDTO;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountATMLoginResponse {
    private Integer account_id;

    private UserNameDTO user;

    private BigDecimal balance;

    private String token;
}
