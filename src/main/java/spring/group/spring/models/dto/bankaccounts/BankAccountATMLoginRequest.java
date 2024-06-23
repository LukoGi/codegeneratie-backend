package spring.group.spring.models.dto.bankaccounts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountATMLoginRequest {
    @NotBlank
    private String fullName;
    @NotBlank
    private String iban;
    @NotNull
    private Integer pinCode;
}
