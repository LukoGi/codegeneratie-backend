package spring.group.spring.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import spring.group.spring.models.User;
import spring.group.spring.services.BankAccountService;
import spring.group.spring.services.UserService;

import java.io.Serializable;

// K - Custom Permission Evaluator
@Component("customPermissionEvaluator")
public class CustomPermissionEvaluator {
    private final BankAccountService bankAccountService;
    private final UserService userService;

    public CustomPermissionEvaluator(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    public boolean isUserAccountOwner(Authentication authentication, Serializable targetId) {
        if ((authentication == null) || (targetId == null) || (authentication.getName() == null)){
            return false;
        }
        Integer accountId = (Integer) targetId;
        String username = authentication.getName();
        return bankAccountService.isUserAccountOwner(username, accountId);
    }

    public boolean isRequestValid(Authentication authentication, Integer userId) {
        if (authentication == null || userId == null) {
            return false;
        }

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        return user != null && user.getUser_id().equals(userId);
    }
}
