package spring.group.spring.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public DataSeeder(BankAccountRepository bankAccountRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User user = seedUser();
        seedBankAccount(user);

    }

    private void seedBankAccount(User user) {
        BankAccount bankAccount1 = new BankAccount(
                "NL91ABNA0417164300",
                new BigDecimal("500.00"),
                "savings",
                true,
                new BigDecimal("1000.00"),
                passwordEncoder.encode("1111"),
                user
        );
        bankAccountRepository.save(bankAccount1);
    }

    private User seedUser() {
        User user1 = new User();
        user1.setFirst_name("John");
        user1.setLast_name("Doe");
        user1.setEmail("John@gmail.com");
        user1.setPassword(passwordEncoder.encode("password"));
        user1.setBsn_number("123456789");
        user1.setPhone_number("0612345678");
        user1.setRole("customer");
        user1.setIs_approved(true);
        user1.setIs_archived(false);
        user1.setDaily_transfer_limit(new BigDecimal("1000.00"));
        userRepository.save(user1);
        return user1;
    }
}
