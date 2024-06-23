package spring.group.spring.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.Role;
import spring.group.spring.models.Transaction;
import spring.group.spring.models.User;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.repositories.TransactionRepository;
import spring.group.spring.repositories.UserRepository;
import spring.group.spring.services.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public DataSeeder(BankAccountRepository bankAccountRepository, UserRepository userRepository, TransactionRepository transactionRepository, BCryptPasswordEncoder passwordEncoder, UserService userService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        User user = seedUser();
        User user2 = seedAnotherUser();
        User user3 = seedSecondUser();

        // we now have 1 admin :)
        seedAdminUser();


        BankAccount bankAccount1 = seedBankAccount(user);
        BankAccount bankAccount3 = seedThirdBankAccount(user);
        BankAccount bankAccount2 = seedAnotherBankAccount(user2);
        BankAccount bankAccount = seedSavingsAccount(user2);

        seedTransactions(bankAccount1, bankAccount2, user);
    }

    private BankAccount seedBankAccount(User user) {
        BankAccount bankAccount1 = new BankAccount(
                "NL91ABNA0417164305",
                new BigDecimal("500.00"),
                AccountType.CHECKINGS,
                true,
                new BigDecimal("-100.00"),
                passwordEncoder.encode("1111"),
                user
        );
        return bankAccountRepository.save(bankAccount1);
    }

    private BankAccount seedAnotherBankAccount(User user) {
        BankAccount bankAccount2 = new BankAccount(
                "NL91ABNA0417164306",
                new BigDecimal("1800.00"),
                AccountType.CHECKINGS,
                true,
                new BigDecimal("-200.00"),
                passwordEncoder.encode("2222"),
                user
        );
        return bankAccountRepository.save(bankAccount2);
    }

    private BankAccount seedThirdBankAccount(User user) {
        BankAccount bankAccount3 = new BankAccount(
                "NL91ABNA0417164307",
                new BigDecimal("1800.00"),
                AccountType.SAVINGS,
                true,
                new BigDecimal("0"),
                passwordEncoder.encode("3333"),
                user
        );
        return bankAccountRepository.save(bankAccount3);
    }


    private User seedUser() {
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setUsername("JohnDoe");
        user1.setEmail("John@gmail.com");
        user1.setPassword("test");
        user1.setBsnNumber("123456789");
        user1.setPhoneNumber("0612345678");
        user1.setRoles(List.of(Role.ROLE_USER));
        user1.setIsApproved(true);
        user1.setIsArchived(false);
        user1.setDailyTransferLimit(new BigDecimal("1000.00"));
        userService.createUser(user1);
        return user1;
    }

    private User seedSecondUser() {
        User user = new User();
        user.setFirstName("Hans");
        user.setLastName("Pan");
        user.setUsername("HansPan");
        user.setEmail("Hans@gmail.com");
        user.setPassword("test");
        user.setBsnNumber("123456787");
        user.setPhoneNumber("0612345678");
        user.setRoles(List.of(Role.ROLE_USER));
        user.setIsApproved(false);
        user.setIsArchived(false);
        user.setDailyTransferLimit(new BigDecimal("1000.00"));
        userService.createUser(user);
        return user;

    }

    private User seedAnotherUser() {
        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setUsername("JaneDoe");
        user2.setEmail("Jane@gmail.com");
        user2.setPassword("test");
        user2.setBsnNumber("987654321");
        user2.setPhoneNumber("0687654321");
        user2.setRoles(List.of(Role.ROLE_USER));
        user2.setIsApproved(true);
        user2.setIsArchived(false);
        user2.setDailyTransferLimit(new BigDecimal("200.00"));
        userService.createUser(user2);
        return user2;
    }


    private User seedAdminUser() {
        User user3 = new User();
        user3.setFirstName("Admin");
        user3.setLastName("Admin");
        user3.setUsername("Admin");
        user3.setEmail(null);
        user3.setPassword("admin");
        user3.setBsnNumber(null);
        user3.setPhoneNumber(null);
        user3.setRoles(List.of(Role.ROLE_ADMIN));
        user3.setIsApproved(true);
        user3.setIsArchived(false);
        user3.setDailyTransferLimit(null);
        userService.createUser(user3);
        return user3;
    }


    private void seedTransactions(BankAccount fromAccount, BankAccount toAccount, User user) {
        Transaction transaction1 = new Transaction();
        transaction1.setToAccount(toAccount);
        transaction1.setFromAccount(fromAccount);
        transaction1.setInitiatorUser(user);
        transaction1.setTransferAmount(new BigDecimal("50.00"));
        transaction1.setDate(LocalDateTime.now());
        transaction1.setDescription("Test Transaction 1");
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setToAccount(fromAccount);
        transaction2.setFromAccount(toAccount);
        transaction2.setInitiatorUser(user);
        transaction2.setTransferAmount(new BigDecimal("100.00"));
        transaction2.setDate(LocalDateTime.now());
        transaction2.setDescription("Test Transaction 2");
        transactionRepository.save(transaction2);
    }

    private BankAccount seedSavingsAccount(User user) {
        BankAccount bankAccount = new BankAccount(
                "NL91ABNA0417164308",
                new BigDecimal("1800.00"),
                AccountType.SAVINGS,
                true,
                new BigDecimal("0"),
                passwordEncoder.encode("2222"),
                user
        );
        return bankAccountRepository.save(bankAccount);
    }
}