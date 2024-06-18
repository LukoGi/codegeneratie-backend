package spring.group.spring.models.dto.bankaccounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.dto.users.UserNameDTO;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountResponseDTO {
    private Integer accountId;

    private UserNameDTO user;

    private String iban;

    private BigDecimal balance;

    private AccountType accountType;

    private Boolean isActive;

    private BigDecimal absoluteLimit;
}
