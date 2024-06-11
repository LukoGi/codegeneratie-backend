package spring.group.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import spring.group.spring.services.BankAccountService;

import java.io.Serializable;

// K - Custom Permission Evaluator
@Component("customPermissionEvaluator")
public class CustomPermissionEvaluator {
    private final BankAccountService bankAccountService;

    public CustomPermissionEvaluator(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public boolean isUserAccountOwner(Authentication authentication, Serializable targetId) {
        if ((authentication == null) || (targetId == null) || (authentication.getName() == null)){
            return false;
        }
        Integer accountId = (Integer) targetId;
        String username = authentication.getName();
        return bankAccountService.isUserAccountOwner(username, accountId);
    }

}
