package spring.group.spring.models.dto.bankaccounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.group.spring.models.AccountType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountRequestDTO {

    @NotNull
    private Integer user_id;

    @NotBlank
    private String iban;

    @NotNull
    private BigDecimal balance;

    @NotNull
    private AccountType account_type;

    @NotNull
    private Boolean is_active;

    @NotNull
    private BigDecimal absolute_limit;

    @NotBlank
    private String pincode;

}
