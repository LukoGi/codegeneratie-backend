package spring.group.spring.services;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.exception.exceptions.*;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.TransactionRequestDTO;
import spring.group.spring.models.dto.bankaccounts.*;
import spring.group.spring.repositories.BankAccountRepository;
import spring.group.spring.security.JwtProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        return bankAccountRepository.save(bankAccount);
    }

    public Page<BankAccount> getAllBankAccounts(Pageable pageable){
        return bankAccountRepository.findAll(pageable);
    }

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        String pincode = bankAccountRepository.findById(bankAccount.getAccount_id()).orElseThrow(EntityNotFoundException::new).getPincode();
        if ((!passwordEncoder.matches(bankAccount.getPincode(), pincode)) && !bankAccount.getPincode().equals(pincode)) {
            String newPincode = passwordEncoder.encode(bankAccount.getPincode());
            bankAccount.setPincode(newPincode);
        } else {
            bankAccount.setPincode(pincode);
        }


        if (!isValidIban(bankAccount.getIban())){
            throw new IllegalArgumentException("Invalid IBAN");
        }
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccountATMLoginResponse atmLogin(BankAccountATMLoginRequest loginRequest) {
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());
        if (bankAccount == null) {
            throw new IncorrectIbanException();
        }

        String fullName = bankAccount.getUser().getFirst_name() + " " + bankAccount.getUser().getLast_name();

        if (!fullName.equals(loginRequest.getFullname())) {
            throw new IncorrectFullnameOnCardException();
        }
        if (!passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
            throw new IncorrectPincodeException();
        }

        BankAccountATMLoginResponse response = mapper.map(bankAccount, BankAccountATMLoginResponse.class);
        response.setToken(jwtProvider.createToken(bankAccount.getUser().getUsername(), bankAccount.getUser().getRoles()));

        return response;
    }

    public WithdrawDepositResponseDTO withdrawMoney(Integer id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        TransactionRequestDTO transactionRequestDTO = transactionService.createTransactionRequestDTO(null, id, bankAccount.getUser().getUser_id(), amount, "Withdraw");
        transactionService.createTransaction(transactionRequestDTO);

        bankAccount.setBalance(bankAccount.getBalance().subtract(amount));
        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());
        bankAccountRepository.save(bankAccount);

        return withdrawDepositResponseDTO;
    }

    public WithdrawDepositResponseDTO depositMoney(Integer id, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);

        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        bankAccount.setBalance(bankAccount.getBalance().add(amount));

        WithdrawDepositResponseDTO withdrawDepositResponseDTO = new WithdrawDepositResponseDTO();
        withdrawDepositResponseDTO.setBalance(bankAccount.getBalance());

        TransactionRequestDTO transactionRequestDTO = transactionService.createTransactionRequestDTO(id, null, bankAccount.getUser().getUser_id(), amount, "Deposit");
        transactionService.createTransaction(transactionRequestDTO);
        bankAccountRepository.save(bankAccount);

        return withdrawDepositResponseDTO;
    }

    public boolean isUserAccountOwner(String username, Integer accountId) {
        String bankAccountUsername = bankAccountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new).getUser().getUsername();
        return bankAccountUsername.equals(username);
    }
  
    public boolean isValidIban(String iban) {
        return iban != null && iban.matches("^[A-Z]{2}\\d{2}[A-Z\\d]{4}\\d{7}[A-Z\\d]{0,16}$");
    }

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

    public BankAccount createBankAccountEntity(User user, AccountType accountType, BigDecimal absolute_limit) {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("NL" + String.format("%02d", SECURE_RANDOM.nextInt(10)) + "INHO0" + String.format("%09d", SECURE_RANDOM.nextInt(1000000000)));
        bankAccount.setUser(user);
        bankAccount.setIs_active(true);
        bankAccount.setBalance(BigDecimal.ZERO);
        bankAccount.setPincode(String.format("%04d", SECURE_RANDOM.nextInt(10000)));
        bankAccount.setAccount_type(accountType);
        bankAccount.setAbsolute_limit(absolute_limit);

        return bankAccount;
    }



}
