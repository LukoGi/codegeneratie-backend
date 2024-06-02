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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        // we now have 2 admins :)
        seedAdminUser();
        User admin = seedAdmin();

        BankAccount bankAccount1 = seedBankAccount(user);
        BankAccount bankAccount2 = seedAnotherBankAccount(user2);
        BankAccount bankAccount = seedSavingsAccount(user2);
        BankAccount bankAccount3 = seedLastBankAccount(admin);

        seedTransactions(bankAccount1, bankAccount2, user);
    }

    private BankAccount seedBankAccount(User user) {
        BankAccount bankAccount1 = new BankAccount(
                "NL91ABNA0417164305",
                new BigDecimal("500.00"),
                AccountType.CHECKINGS,
                true,
                new BigDecimal("100.00"),
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
                new BigDecimal("200.00"),
                passwordEncoder.encode("2222"),
                user
        );
        return bankAccountRepository.save(bankAccount2);
    }

    private BankAccount seedLastBankAccount(User user) {
        BankAccount bankAccount3 = new BankAccount(
                "NL91ABNA0417164307",
                new BigDecimal("10000.00"),
                AccountType.CHECKINGS,
                true,
                new BigDecimal("1000.00"),
                passwordEncoder.encode("3333"),
                user
        );
        return bankAccountRepository.save(bankAccount3);
    }

    private User seedUser() {
        User user1 = new User();
        user1.setFirst_name("John");
        user1.setLast_name("Doe");
        user1.setUsername("JohnDoe");
        user1.setEmail("John@gmail.com");
        user1.setPassword("test");
        user1.setBsn_number("123456789");
        user1.setPhone_number("0612345678");
        user1.setRoles(List.of(Role.ROLE_USER));
        user1.setIs_approved(false);
        user1.setIs_archived(false);
        user1.setDaily_transfer_limit(new BigDecimal("1000.00"));
        userService.createUser(user1);
        return user1;
    }

    private User seedAnotherUser() {
        User user2 = new User();
        user2.setFirst_name("Jane");
        user2.setLast_name("Doe");
        user2.setUsername("JaneDoe");
        user2.setEmail("Jane@gmail.com");
        user2.setPassword("test");
        user2.setBsn_number("987654321");
        user2.setPhone_number("0687654321");
        user2.setRoles(List.of(Role.ROLE_USER));
        user2.setIs_approved(true);
        user2.setIs_archived(false);
        user2.setDaily_transfer_limit(new BigDecimal("200.00"));
        userService.createUser(user2);
        return user2;
    }


    private User seedAdminUser() {
        User user3 = new User();
        user3.setFirst_name("Admin");
        user3.setLast_name("Admin");
        user3.setUsername("Admin");
        user3.setEmail(null);
        user3.setPassword("admin");
        user3.setBsn_number(null);
        user3.setPhone_number(null);
        user3.setRoles(List.of(Role.ROLE_ADMIN));
        user3.setIs_approved(true);
        user3.setIs_archived(false);
        user3.setDaily_transfer_limit(null);
        userService.createUser(user3);
        return user3;
    }
 
    private User seedAdmin() {
        User admin = new User();
        admin.setFirst_name("Jan");
        admin.setLast_name("Man");
        admin.setUsername("JanMan");
        admin.setEmail("Jan@gmail.com");
        admin.setPassword("test");
        admin.setBsn_number("3453213454");
        admin.setPhone_number("34565434556");
        admin.setRoles(List.of(Role.ROLE_ADMIN));
        admin.setIs_approved(true);
        admin.setIs_archived(false);
        admin.setDaily_transfer_limit(new BigDecimal("1000.00"));
        userService.createUser(admin);
        return admin;

    }

    private void seedTransactions(BankAccount fromAccount, BankAccount toAccount, User user) {
        Transaction transaction1 = new Transaction();
        transaction1.setTo_account(toAccount);
        transaction1.setFrom_account(fromAccount);
        transaction1.setInitiator_user(user);
        transaction1.setTransfer_amount(new BigDecimal("50.00"));
        transaction1.setDate(LocalDateTime.now());
        transaction1.setDescription("Test Transaction 1");
        transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction();
        transaction2.setTo_account(fromAccount);
        transaction2.setFrom_account(toAccount);
        transaction2.setInitiator_user(user);
        transaction2.setTransfer_amount(new BigDecimal("100.00"));
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
                new BigDecimal("200.00"),
                passwordEncoder.encode("2222"),
                user
        );
        return bankAccountRepository.save(bankAccount);
    }
}