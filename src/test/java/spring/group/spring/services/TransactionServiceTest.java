package spring.group.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.*;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCustomerCreateTransaction() {
        // Arrange
        User mockFromUser = createMockUser("John", "Doe", "JohnDoe", "John@gmail.com", "test", "123456789", "0612345678", List.of(Role.ROLE_USER), false, false, new BigDecimal("1000.00"));
        BankAccount mockFromBankAccount = createMockBankAccount("NL91ABNA0417164305", new BigDecimal("500.00"), AccountType.CHECKINGS, true, new BigDecimal("-100.00"), mockFromUser);
        User mockToUser = createMockUser("Jane", "Doe", "JaneDoe", "Jane@gmail.com", "test", "987654321", "0687654321", List.of(Role.ROLE_USER), true, false, new BigDecimal("200.00"));
        BankAccount mockToBankAccount = createMockBankAccount("NL91ABNA0417164306", new BigDecimal("1800.00"), AccountType.CHECKINGS, true, new BigDecimal("-200.00"), mockToUser);

        CustomerTransactionRequestDTO request = new CustomerTransactionRequestDTO();
        request.setToAccountIban("NL91ABNA0417164306");
        request.setInitiatorUserId(1);
        request.setTransferAmount(new BigDecimal(1));
        request.setDescription("test");

        Transaction expectedTransaction = createExpectedTransaction(mockFromBankAccount, mockToBankAccount, mockFromUser, request.getTransferAmount(), request.getDescription());

        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(mockFromUser));
        when(bankAccountRepository.findByIban(any(String.class))).thenReturn(mockToBankAccount);
        when(bankAccountRepository.findByUserUser_idAndAccountTypeAndIsActive(any(Integer.class), eq(AccountType.CHECKINGS), eq(true)))
                .thenReturn(Optional.of(mockFromBankAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.customerCreateTransaction(request);

        // Assert
        assertEquals(expectedTransaction.getTransaction_id(), response.getTransactionId());
        assertEquals(expectedTransaction.getFrom_account(), response.getFromAccount());
        assertEquals(expectedTransaction.getTo_account(), response.getToAccount());
        assertEquals(expectedTransaction.getInitiator_user(), response.getInitiatorUser());
        assertEquals(expectedTransaction.getTransfer_amount(), response.getTransferAmount());
        assertEquals(expectedTransaction.getDescription(), response.getDescription());
    }

    @Test
    public void testCustomerCreateInternalTransaction() {
        // Arrange
        User mockUser = createMockUser("Jane", "Doe", "JaneDoe", "Jane@gmail.com", "test", "987654321", "0687654321", List.of(Role.ROLE_USER), true, false, new BigDecimal("200.00"));
        BankAccount mockFromBankAccount = createMockBankAccount("NL91ABNA0417164306", new BigDecimal("1800.00"), AccountType.CHECKINGS, true, new BigDecimal("-200.00"), mockUser);
        BankAccount mockToBankAccount = createMockBankAccount("NL91ABNA0417164308", new BigDecimal("1800.00"), AccountType.SAVINGS, true, new BigDecimal("200.00"), mockUser);

        InternalTransactionRequestDTO request = new InternalTransactionRequestDTO();
        request.setUserId(2);
        request.setFromAccountType("CHECKINGS");
        request.setToAccountType("SAVINGS");
        request.setTransferAmount(new BigDecimal(2));

        Transaction expectedTransaction = createExpectedTransaction(mockFromBankAccount, mockToBankAccount, mockUser, request.getTransferAmount(), "Transfer between checkings/savings accounts");

        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(mockUser));
        when(bankAccountRepository.findByUserAndAccountType(any(User.class), any(AccountType.class)))
                .thenAnswer(invocation -> {
                    AccountType accountType = invocation.getArgument(1);
                    if (accountType == AccountType.CHECKINGS) {
                        return Optional.of(mockFromBankAccount);
                    } else if (accountType == AccountType.SAVINGS) {
                        return Optional.of(mockToBankAccount);
                    }
                    return Optional.empty();
                });
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.customerCreateInternalTransaction(request);

        // Assert
        assertEquals(expectedTransaction.getTransaction_id(), response.getTransactionId());
        assertEquals(expectedTransaction.getFrom_account(), response.getFromAccount());
        assertEquals(expectedTransaction.getTo_account(), response.getToAccount());
        assertEquals(expectedTransaction.getInitiator_user(), response.getInitiatorUser());
        assertEquals(expectedTransaction.getTransfer_amount(), response.getTransferAmount());
        assertEquals(expectedTransaction.getDescription(), response.getDescription());
    }

    @Test
    public void testEmployeeCreateTransaction() {
        // Arrange
        User mockEmployee = createMockUser("Admin", "Admin", "Admin", null, "admin", null, null, List.of(Role.ROLE_ADMIN), true, false, null);
        User mockFromUser = createMockUser("John", "Doe", "JohnDoe", "John@gmail.com", "test", "123456789", "0612345678", List.of(Role.ROLE_USER), false, false, new BigDecimal("1000.00"));
        User mockToUser = createMockUser("Jane", "Doe", "JaneDoe", "Jane@gmail.com", "test", "987654321", "0687654321", List.of(Role.ROLE_USER), true, false, new BigDecimal("200.00"));

        BankAccount mockFromBankAccount = createMockBankAccount("NL91ABNA0417164305", new BigDecimal("500.00"), AccountType.CHECKINGS, true, new BigDecimal("-100.00"), mockFromUser);
        BankAccount mockToBankAccount = createMockBankAccount("NL91ABNA0417164306", new BigDecimal("1800.00"), AccountType.CHECKINGS, true, new BigDecimal("-200.00"), mockToUser);

        EmployeeTransactionRequestDTO request = new EmployeeTransactionRequestDTO();
        request.setEmployeeId(3);
        request.setFromAccountIban("NL91ABNA0417164305");
        request.setToAccountIban("NL91ABNA0417164306");
        request.setTransferAmount(new BigDecimal(1));
        request.setDescription("testEmployeeTransfer");

        Transaction expectedTransaction = createExpectedTransaction(mockFromBankAccount, mockToBankAccount, mockEmployee, request.getTransferAmount(), request.getDescription());

        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(mockEmployee));
        when(bankAccountRepository.findByIban(mockFromBankAccount.getIban())).thenReturn(mockFromBankAccount);
        when(bankAccountRepository.findByIban(mockToBankAccount.getIban())).thenReturn(mockToBankAccount);
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            return transaction;
        });

        // Act
        TransactionResponseDTO response = transactionService.employeeCreateTransaction(request);

        // Assert
        assertEquals(expectedTransaction.getTransaction_id(), response.getTransactionId());
        assertEquals(expectedTransaction.getFrom_account(), response.getFromAccount());
        assertEquals(expectedTransaction.getTo_account(), response.getToAccount());
        assertEquals(expectedTransaction.getInitiator_user(), response.getInitiatorUser());
        assertEquals(expectedTransaction.getTransfer_amount(), response.getTransferAmount());
        assertEquals(expectedTransaction.getDescription(), response.getDescription());
    }

    @Test
    public void testCheckIfAbsoluteLimitIsHit() {
        // Arrange
        BankAccount mockFromBankAccount = createMockBankAccount("NL91ABNA0417164305", new BigDecimal("100.00"), AccountType.CHECKINGS, true, new BigDecimal("-100.00"), null);

        // Act & Assert
        assertDoesNotThrow(() -> transactionService.checkIfAbsoluteLimitIsHit(mockFromBankAccount, new BigDecimal("200")));
        assertThrows(AbsoluteLimitHitException.class, () -> transactionService.checkIfAbsoluteLimitIsHit(mockFromBankAccount, new BigDecimal("201")));
    }

    @Test
    public void testCheckIfDailyLimitIsHit() {
        // daily limit is 1000
        // Arrange
        User mockUser = createMockUser("John", "Doe", "JohnDoe", "test@test.com", "test", "123456789", "0612345678", List.of(Role.ROLE_USER), true, false, new BigDecimal("1000.00"));
        BankAccount mockFromBankAccount = createMockBankAccount("NL91ABNA0417164305", new BigDecimal("100.00"), AccountType.CHECKINGS, true, new BigDecimal("-100.00"), mockUser);
        when(transactionRepository.getSumOfTodaysTransaction(any(BankAccount.class), any(LocalDateTime.class))).thenReturn(new BigDecimal("100.00"));

        // Act & Assert
        assertDoesNotThrow(() -> transactionService.checkIfDailyLimitIsHit(mockFromBankAccount, new BigDecimal("900")));
        assertThrows(DailyTransferLimitHitException.class, () -> transactionService.checkIfDailyLimitIsHit(mockFromBankAccount, new BigDecimal("901")));
    }

    private User createMockUser(String firstName, String lastName, String username, String email, String password, String bsnNumber, String phoneNumber, List<Role> roles, boolean isApproved, boolean isArchived, BigDecimal dailyTransferLimit) {
        User mockUser = new User();
        mockUser.setFirst_name(firstName);
        mockUser.setLast_name(lastName);
        mockUser.setUsername(username);
        mockUser.setEmail(email);
        mockUser.setPassword(password);
        mockUser.setBsn_number(bsnNumber);
        mockUser.setPhone_number(phoneNumber);
        mockUser.setRoles(roles);
        mockUser.setIs_approved(isApproved);
        mockUser.setIs_archived(isArchived);
        mockUser.setDaily_transfer_limit(dailyTransferLimit);
        return mockUser;
    }

    private BankAccount createMockBankAccount(String iban, BigDecimal balance, AccountType accountType, boolean isActive, BigDecimal absoluteLimit, User user) {
        BankAccount mockBankAccount = new BankAccount();
        mockBankAccount.setIban(iban);
        mockBankAccount.setBalance(balance);
        mockBankAccount.setAccount_type(accountType);
        mockBankAccount.setIs_active(isActive);
        mockBankAccount.setAbsolute_limit(absoluteLimit);
        mockBankAccount.setUser(user);
        return mockBankAccount;
    }

    private Transaction createExpectedTransaction(BankAccount fromAccount, BankAccount toAccount, User initiatorUser, BigDecimal transferAmount, String description) {
        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setFrom_account(fromAccount);
        expectedTransaction.setTo_account(toAccount);
        expectedTransaction.setInitiator_user(initiatorUser);
        expectedTransaction.setTransfer_amount(transferAmount);
        expectedTransaction.setDescription(description);
        return expectedTransaction;
    }

    @Test
    void getAllTransactions() {
        // Assign
        LocalDateTime date=LocalDateTime.now();
        when(transactionRepository.findAllTransactionsWithFilters(eq(date), eq(BigDecimal.ZERO), eq(BigDecimal.ONE), eq("iban"), any(Pageable.class))).thenReturn(Page.empty());
        // Act & Assert
        Page<Transaction>  result =   transactionService.getAllTransactions(date,BigDecimal.ZERO, BigDecimal.ONE,"iban",1,10);
        // Assert
        assertEquals(0, result.getTotalElements());
    }


    @Test
    void getTransactionsByUserId() {
        LocalDateTime date=LocalDateTime.now();
        when(transactionRepository.findAllByInitiatorUserIdWithFilters(eq(1),eq(date),eq(date), eq(BigDecimal.ONE), eq(BigDecimal.ONE), eq("iban"), any(Pageable.class))).thenReturn(Page.empty());
        // Act & Assert
        Page<Transaction>  result =   transactionService.getTransactionsByUserId(1,date,date,BigDecimal.ONE,BigDecimal.ONE,"iban",0,1);
        // Assert
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getTransactionsByAccountId() {
        LocalDateTime date=LocalDateTime.now();
        when(transactionRepository.findAllTransactionsWithAccountIdAndFilters(eq(1),eq(date),eq(date), eq(BigDecimal.ONE), eq(BigDecimal.ONE), eq("iban"), any(Pageable.class))).thenReturn(Page.empty());
        // Act & Assert
        Page<Transaction>  result =   transactionService.getTransactionsByAccountId(1,date,date,BigDecimal.ONE,BigDecimal.ONE,"iban",0,1);
        // Assert
        assertEquals(0, result.getTotalElements());
    }
}
