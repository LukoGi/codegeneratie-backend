package spring.group.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
    public void testCreateTransactionFromIban() {
        // Arrange
        User mockFromUser = new User();
        mockFromUser.setFirst_name("John");
        mockFromUser.setLast_name("Doe");
        mockFromUser.setUsername("JohnDoe");
        mockFromUser.setEmail("John@gmail.com");
        mockFromUser.setPassword("test");
        mockFromUser.setBsn_number("123456789");
        mockFromUser.setPhone_number("0612345678");
        mockFromUser.setRoles(List.of(Role.ROLE_USER));
        mockFromUser.setIs_approved(false);
        mockFromUser.setIs_archived(false);
        mockFromUser.setDaily_transfer_limit(new BigDecimal("1000.00"));

        BankAccount mockFromBankAccount = new BankAccount();
        mockFromBankAccount.setIban("NL91ABNA0417164305");
        mockFromBankAccount.setBalance(new BigDecimal("500.00"));
        mockFromBankAccount.setAccount_type(AccountType.CHECKINGS);
        mockFromBankAccount.setIs_active(true);
        mockFromBankAccount.setAbsolute_limit(new BigDecimal("-100.00"));
        mockFromBankAccount.setUser(mockFromUser);

        User mockToUser = new User();
        mockToUser.setFirst_name("Jane");
        mockToUser.setLast_name("Doe");
        mockToUser.setUsername("JaneDoe");
        mockToUser.setEmail("Jane@gmail.com");
        mockToUser.setPassword("test");
        mockToUser.setBsn_number("987654321");
        mockToUser.setPhone_number("0687654321");
        mockToUser.setRoles(List.of(Role.ROLE_USER));
        mockToUser.setIs_approved(true);
        mockToUser.setIs_archived(false);
        mockToUser.setDaily_transfer_limit(new BigDecimal("200.00"));

        BankAccount mockToBankAccount = new BankAccount();
        mockToBankAccount.setIban("NL91ABNA0417164306");
        mockToBankAccount.setBalance(new BigDecimal("1800.00"));
        mockToBankAccount.setAccount_type(AccountType.CHECKINGS);
        mockToBankAccount.setIs_active(true);
        mockToBankAccount.setAbsolute_limit(new BigDecimal("-200.00"));
        mockToBankAccount.setUser(mockToUser);

        TransactionCreateFromIbanRequestDTO request = new TransactionCreateFromIbanRequestDTO();
        request.setTo_account_iban("NL91ABNA0417164306");
        request.setInitiator_user_id(1);
        request.setTransfer_amount(new BigDecimal(1));
        request.setDescription("test");

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setFrom_account(mockFromBankAccount);
        expectedTransaction.setTo_account(mockToBankAccount);
        expectedTransaction.setInitiator_user(mockFromUser);
        expectedTransaction.setTransfer_amount(request.getTransfer_amount());
        expectedTransaction.setDescription(request.getDescription());
        // We don't set the date because it's set to the current time in the service method
        // We don't set the transaction_id because it's generated when the transaction is saved

        when(userRepository.findById(any(Integer.class))).thenReturn(Optional.of(mockFromUser));
        when(bankAccountRepository.findByIban(any(String.class))).thenReturn(mockToBankAccount);
        when(bankAccountRepository.findByUserUser_idAndAccountTypeAndIsActive(any(Integer.class), eq(AccountType.CHECKINGS), eq(true)))
                .thenReturn(Optional.of(mockFromBankAccount));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(expectedTransaction);

        // Act
        TransactionResponseDTO response = transactionService.createTransactionFromIban(request);

        // Assert
        assertEquals(expectedTransaction.getTransaction_id(), response.getTransaction_id());
        assertEquals(expectedTransaction.getFrom_account(), response.getFrom_account());
        assertEquals(expectedTransaction.getTo_account(), response.getTo_account());
        assertEquals(expectedTransaction.getInitiator_user(), response.getInitiator_user());
        assertEquals(expectedTransaction.getTransfer_amount(), response.getTransfer_amount());
        assertEquals(expectedTransaction.getDescription(), response.getDescription());
    }

    @Test
    public void testTransferFunds() {
        // Arrange
        User mockUser = new User();
        mockUser.setFirst_name("Jane");
        mockUser.setLast_name("Doe");
        mockUser.setUsername("JaneDoe");
        mockUser.setEmail("Jane@gmail.com");
        mockUser.setPassword("test");
        mockUser.setBsn_number("987654321");
        mockUser.setPhone_number("0687654321");
        mockUser.setRoles(List.of(Role.ROLE_USER));
        mockUser.setIs_approved(true);
        mockUser.setIs_archived(false);
        mockUser.setDaily_transfer_limit(new BigDecimal("200.00"));

        BankAccount mockFromBankAccount = new BankAccount();
        mockFromBankAccount.setIban("NL91ABNA0417164306");
        mockFromBankAccount.setBalance(new BigDecimal("1800.00"));
        mockFromBankAccount.setAccount_type(AccountType.CHECKINGS);
        mockFromBankAccount.setIs_active(true);
        mockFromBankAccount.setAbsolute_limit(new BigDecimal("-200.00"));
        mockFromBankAccount.setUser(mockUser);

        BankAccount mockToBankAccount = new BankAccount();
        mockToBankAccount.setIban("NL91ABNA0417164308");
        mockToBankAccount.setBalance(new BigDecimal("1800.00"));
        mockToBankAccount.setAccount_type(AccountType.SAVINGS);
        mockToBankAccount.setIs_active(true);
        mockToBankAccount.setAbsolute_limit(new BigDecimal("200.00"));
        mockToBankAccount.setUser(mockUser);

        TransferRequestDTO request = new TransferRequestDTO();
        request.setUserId(2);
        request.setFromAccountType("CHECKINGS");
        request.setToAccountType("SAVINGS");
        request.setTransferAmount(new BigDecimal(2));

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setFrom_account(mockFromBankAccount);
        expectedTransaction.setTo_account(mockToBankAccount);
        expectedTransaction.setInitiator_user(mockUser);
        expectedTransaction.setTransfer_amount(request.getTransferAmount());
        expectedTransaction.setDescription("Transfer between checkings/savings accounts");
        // We don't set the date because it's set to the current time in the service method
        // We don't set the transaction_id because it's generated when the transaction is saved

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
        TransactionResponseDTO response = transactionService.transferFunds(request);

        // Assert
        assertEquals(expectedTransaction.getTransaction_id(), response.getTransaction_id());
        assertEquals(expectedTransaction.getFrom_account(), response.getFrom_account());
        assertEquals(expectedTransaction.getTo_account(), response.getTo_account());
        assertEquals(expectedTransaction.getInitiator_user(), response.getInitiator_user());
        assertEquals(expectedTransaction.getTransfer_amount(), response.getTransfer_amount());
        assertEquals(expectedTransaction.getDescription(), response.getDescription());
    }
}
