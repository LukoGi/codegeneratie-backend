package spring.group.spring.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.UserRepository;

import java.math.BigDecimal;

@Component
public class DataSeeder implements ApplicationRunner {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    public DataSeeder(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        User user = seedUser();
        seedBankAccount(user);
    }

    private void seedBankAccount(User user) {
        BankAccount bankAccount1 = new BankAccount(
                "NL91ABNA0417164305",
                new BigDecimal("500.00"),
                "savings",
                true,
                new BigDecimal("1000.00"),
                Integer.valueOf(1111),
                user
        );
        bankAccountRepository.save(bankAccount1);
    }

    private User seedUser() {
        User user1 = new User();
        user1.setFirst_name("John");
        userRepository.save(user1);
        return user1;
    }
}
