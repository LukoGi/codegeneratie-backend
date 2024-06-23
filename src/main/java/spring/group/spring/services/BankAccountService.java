package spring.group.spring.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.UserNotFoundException;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.TransactionRequestDTO;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.security.JwtProvider;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@AllArgsConstructor
@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TransactionService transactionService;
    private final ModelMapper mapper;
    private final JwtProvider jwtProvider;
    private final UserService userService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public BankAccount createBankAccount(BankAccount bankAccount) {
        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);
        validateData(bankAccount);
        return bankAccountRepository.save(bankAccount);
    }

    // Julian
    public Page<BankAccount> getAllBankAccounts(Pageable pageable){
        return bankAccountRepository.findAll(pageable);
    }

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        validateAndEncodePincode(bankAccount);
        validateData(bankAccount);
        return bankAccountRepository.save(bankAccount);
    }

    // K - atmLogin
    public BankAccountATMLoginResponse atmLogin(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = validateLoginRequest(loginRequest);
        return createLoginResponse(bankAccount);
    }

    // K - withdrawMoney
    public WithdrawDepositResponseDTO withdrawMoney(Integer id, BigDecimal amount) {
        validateAmount(amount);
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        TransactionRequestDTO transactionRequestDTO = transactionService.createTransactionRequestDTO(null, id, bankAccount.getUser().getUserId(), amount, "Withdraw");
        transactionService.createTransaction(transactionRequestDTO);
        updateBalance(bankAccount, amount.negate());
        return createWithdrawDepositResponseDTO(bankAccount);
    }

    // K - depositMoney
    public WithdrawDepositResponseDTO depositMoney(Integer id, BigDecimal amount) {
        validateAmount(amount);
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        TransactionRequestDTO transactionRequestDTO = transactionService.createTransactionRequestDTO(id, null, bankAccount.getUser().getUserId(), amount, "Deposit");
        transactionService.createTransaction(transactionRequestDTO);
        updateBalance(bankAccount, amount);
        return createWithdrawDepositResponseDTO(bankAccount);
    }

    public boolean isUserAccountOwner(String username, Integer accountId) {
        String bankAccountUsername = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new).getUser().getUsername();
        return bankAccountUsername.equals(username);
    }
  
    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}\\d{2}[A-Z\\d]{4}\\d{7}[A-Z\\d]{0,16}$");
    }

    // Julian

    public boolean getBankAccountByIban(String iban) {
        return bankAccountRepository.findByIban(iban) != null;
    }

    public List<BankAccount> getBankAccountsByUserId(Integer userId) {
        User user = userService.getUserById(userId);
        return bankAccountRepository.findByUser(user);
    }

    public boolean checkIban(String iban) {
        return isValidIban(iban) && !getBankAccountByIban(iban);
    }

    public BankAccount closeBankAccount(int accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);
        bankAccount.setIsActive(false);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount createBankAccountEntity(User user, AccountType accountType, BigDecimal absoluteLimit) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("NL" + String.format("%02d", SECURE_RANDOM.nextInt(10)) + "INHO0" + String.format("%09d", SECURE_RANDOM.nextInt(1000000000)));
        bankAccount.setUser(user);
        bankAccount.setIsActive(true);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setPincode("1111");
        bankAccount.setAccountType(accountType);
        bankAccount.setAbsoluteLimit(absoluteLimit);

        return bankAccount;
    }

    // Julian

    public List<BankAccount> getIbanByUsername(String username)  {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        List<BankAccount> bankAccounts = bankAccountRepository.findByUser(user);
        if (bankAccounts.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        return bankAccounts;
    }
    private void validateAndEncodePincode(BankAccount bankAccount) {
        String pincode = bankAccountRepository.findById(bankAccount.getAccountId())
                .orElseThrow(EntityNotFoundException::new)
                .getPincode();

        if ((!passwordEncoder.matches(bankAccount.getPincode(), pincode)) && !bankAccount.getPincode().equals(pincode)) {
            String newPincode = passwordEncoder.encode(bankAccount.getPincode());
            bankAccount.setPincode(newPincode);
        } else {
            bankAccount.setPincode(pincode);
        }
    }

    private void validateData(BankAccount bankAccount) {
        if (!isValidIban(bankAccount.getIban())) {
            throw new IllegalArgumentException("Invalid IBAN");
        }
        if (bankAccount.getAccountType() == AccountType.SAVINGS && !bankAccount.getAbsoluteLimit().equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("Absolute limit must be 0 for savings account");
        }
    }

    private BankAccount validateLoginRequest(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());
        if (bankAccount == null) {
            throw new IncorrectIbanException();
        }

        String fullName = bankAccount.getUser().getFirstName() + " " + bankAccount.getUser().getLastName();
        if (!fullName.equals(loginRequest.getFullname())) {
            throw new IncorrectFullnameOnCardException();
        }
        if (!passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
            throw new IncorrectPincodeException();
        }

        return bankAccount;
    }

    private BankAccountATMLoginResponse createLoginResponse(BankAccount bankAccount) {
        BankAccountATMLoginResponse response = mapper.map(bankAccount, BankAccountATMLoginResponse.class);
        response.setToken(jwtProvider.createToken(bankAccount.getUser().getUsername(), bankAccount.getUser().getRoles()));
        return response;
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
    }

    private void updateBalance(BankAccount bankAccount, BigDecimal amount) {
        bankAccount.setBalance(bankAccount.getBalance().add(amount));
        bankAccountRepository.save(bankAccount);
    }

    private WithdrawDepositResponseDTO createWithdrawDepositResponseDTO(BankAccount bankAccount) {
        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());
        return withdrawDepositResponseDTO;
    }
}
