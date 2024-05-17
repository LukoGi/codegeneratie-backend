package spring.group.spring.models.dto.bankaccounts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankAccountATMLoginRequest {
    private String fullName;
    private String iban;
    private Integer pincode;
}
