package spring.group.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.group.spring.exception.exceptions.BankAccountNotFoundException;
import spring.group.spring.exception.exceptions.EntityNotFoundException;
import spring.group.spring.exception.exceptions.UserNotFoundException;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountATMLoginRequest;
import spring.group.spring.models.dto.bankaccounts.BankAccountATMLoginResponse;
import spring.group.spring.models.dto.bankaccounts.WithdrawDepositResponseDTO;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.security.JwtProvider;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserService userService;
    @Mock
    private JwtProvider jwtProvider;

    private BankAccountService bankAccountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ModelMapper();
        bankAccountService = new BankAccountService(bankAccountRepository, passwordEncoder, transactionService, mapper, jwtProvider, userService);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(jwtProvider.createToken(anyString(), anyList())).thenReturn("dummyToken");

    }

    @Test
    void getBankAccountById() {
        // Arrange
        BankAccount expectedBankAccount = new BankAccount();
        expectedBankAccount.setAccountId(1);
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(expectedBankAccount));

        // Act
        BankAccount actualBankAccount = bankAccountService.getBankAccountById(1);

        // Assert
        assertEquals(expectedBankAccount, actualBankAccount);
    }

    @Test
    void updateBankAccount() {
        // Arrange
        BankAccount existingBankAccount = arrangeExistingBankAccount();
        BankAccount updatedBankAccount = arrangeUpdatedBankAccount();
        when(bankAccountRepository.findById(1)).thenReturn(Optional.of(existingBankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        BankAccount resultBankAccount = bankAccountService.updateBankAccount(updatedBankAccount);

        // Assert
        assertEquals(updatedBankAccount.getAccountId(), resultBankAccount.getAccountId());
        assertEquals(updatedBankAccount.getIban(), resultBankAccount.getIban());
        assertEquals(updatedBankAccount.getPinCode(), resultBankAccount.getPinCode());
    }

    @Test
    void atmLogin() {
        // Arrange
        BankAccountATMLoginRequest loginRequest = arrangeBankAccountATMLoginRequest();
        BankAccount bankAccount = arrangeExistingBankAccountWithUser();

        when(bankAccountRepository.findByIban(loginRequest.getIban())).thenReturn(bankAccount);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.createToken(bankAccount.getUser().getUsername(), bankAccount.getUser().getRoles())).thenReturn("dummyToken");
        when(bankAccountRepository.findByIban(loginRequest.getIban())).thenReturn(bankAccount);

        BankAccountATMLoginResponse expectedResponse = new BankAccountATMLoginResponse();
        expectedResponse.setToken("dummyToken");

        // Act
        BankAccountATMLoginResponse actualResponse = bankAccountService.atmLogin(loginRequest);

        // Assert
        assertEquals(expectedResponse.getToken(), actualResponse.getToken());
    }

    @Test
    void withdrawMoney() {
        // Arrange
        Integer id = 1;
        BigDecimal amount = BigDecimal.valueOf(100.00);
        BankAccount bankAccount = arrangeExistingBankAccount();

        BigDecimal initialBalance = bankAccount.getBalance();
        BigDecimal expectedBalance = initialBalance.subtract(amount);

        when(bankAccountRepository.findById(id)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        WithdrawDepositResponseDTO actualResponse = bankAccountService.withdrawMoney(id, amount);

        // Assert
        assertEquals(expectedBalance, actualResponse.getBalance());
    }

    @Test
    void depositMoney() {
        // Arrange
        Integer id = 1;
        BigDecimal amount = BigDecimal.valueOf(100.00);
        BankAccount bankAccount = arrangeExistingBankAccount();

        BigDecimal initialBalance = bankAccount.getBalance();
        BigDecimal expectedBalance = initialBalance.add(amount);

        when(bankAccountRepository.findById(id)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        WithdrawDepositResponseDTO actualResponse = bankAccountService.depositMoney(id, amount);

        // Assert
        assertEquals(expectedBalance, actualResponse.getBalance());
    }

    @Test
    void isUserAccountOwner() {
        // Arrange
        Integer accountId = 1;
        String username = "JohnDoe";

        BankAccount bankAccount = new BankAccount();
        User user = new User();
        user.setUsername(username);
        bankAccount.setUser(user);

        when(bankAccountRepository.findById(accountId)).thenReturn(Optional.of(bankAccount));

        // Act
        boolean isOwner = bankAccountService.isUserAccountOwner(username, accountId);

        // Assert
        assertTrue(isOwner);
    }

    private BankAccount arrangeExistingBankAccount(){
        BankAccount existingBankAccount = new BankAccount();
        existingBankAccount.setAccountId(1);
        existingBankAccount.setIban("NL91ABNA0417164305");
        existingBankAccount.setBalance(BigDecimal.valueOf(500.00));
        existingBankAccount.setAccountType(AccountType.CHECKINGS);
        existingBankAccount.setIsActive(true);
        existingBankAccount.setAbsoluteLimit(BigDecimal.valueOf(-150.00));
        existingBankAccount.setPinCode("1111");

        User user = new User();
        user.setUserId(1);
        existingBankAccount.setUser(user);
        return existingBankAccount;
    }

    private BankAccount arrangeUpdatedBankAccount() {
        BankAccount existingBankAccount = new BankAccount();
        existingBankAccount.setAccountId(1);
        existingBankAccount.setIban("NL91ABNA0417164305");
        existingBankAccount.setBalance(BigDecimal.valueOf(500.00));
        existingBankAccount.setAccountType(AccountType.CHECKINGS);
        existingBankAccount.setIsActive(true);
        existingBankAccount.setAbsoluteLimit(BigDecimal.valueOf(-150.00));
        existingBankAccount.setPinCode("1111");

        User user = new User();
        user.setUserId(1);
        existingBankAccount.setUser(user);
        return existingBankAccount;
    }

    private BankAccountATMLoginRequest arrangeBankAccountATMLoginRequest() {
        BankAccountATMLoginRequest bankAccountATMLoginRequest = new BankAccountATMLoginRequest();
        bankAccountATMLoginRequest.setIban("NL91ABNA0417164305");
        bankAccountATMLoginRequest.setFullName("John Doe");
        bankAccountATMLoginRequest.setPinCode(1111);
        return bankAccountATMLoginRequest;
    }

    private BankAccount arrangeExistingBankAccountWithUser() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountId(1);
        bankAccount.setIban("NL91ABNA0417164305");
        String pinCode = passwordEncoder.encode("1111");
        bankAccount.setPinCode(pinCode);

        User user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Doe");
        bankAccount.setUser(user);
        return bankAccount;
    }

    @Test
    void getIbanByUsername() {
        // Arrange
        String username = "JohnDoe";
        String iban = "NL91ABNA0417164305";

        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(iban);
        List<BankAccount> account= List.of(bankAccount);
        User user = new User();
        user.setUsername(username);
        bankAccount.setUser(user);

        when(userService.getUserByUsername(username)).thenReturn(user);
        when(bankAccountRepository.findByUser(user)).thenReturn(account);
        when(bankAccountRepository.findIbansByUser(user, AccountType.CHECKINGS)).thenReturn(List.of(iban));

        // Act
        List<String> ibans = bankAccountService.getIbanByUsername(username);

        // Assert
        assertEquals(ibans.size(),1);
        assertEquals(iban, ibans.get(0));
    }
    @Test
    void Throw_when_user_is_unknown() {
        // Arrange
        String username = "JohnDoe";
        when(userService.getUserByUsername(username)).thenReturn(null);
        // Act Assert
        assertThrows(EntityNotFoundException.class, () -> bankAccountService.getIbanByUsername(username));
    }
    @Test
    void Throw_when_user_has_no_bank_account() {
        // Arrange
        String username = "JohnDoe";
        User user = new User();
        user.setUsername(username);
        List<BankAccount> account= List.of();
        when(userService.getUserByUsername(username)).thenReturn(user);
        when(bankAccountRepository.findByUser(user)).thenReturn(account );
        // Act Assert

        assertThrows(EntityNotFoundException.class, () -> bankAccountService.getIbanByUsername(username));
    }
}