package spring.group.spring.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.BankAccountATMLoginRequest;
import spring.group.spring.models.dto.bankaccounts.BankAccountDTO;
import spring.group.spring.models.dto.bankaccounts.BankAccountRequestDTO;
import spring.group.spring.models.dto.bankaccounts.BankAccountResponseDTO;
import spring.group.spring.repositories.BankAccountRepository;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public BankAccountService(BankAccountRepository bankAccountRepository, UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // TODO Implement the following methods:
    // getBankAccounts()
    // createBankAccount()
    // atmLogin()

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        String encryptedPassword = passwordEncoder.encode(bankAccount.getPincode());
        bankAccount.setPincode(encryptedPassword);
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccount atmLogin(BankAccountATMLoginRequest loginRequest) {
        // login request contains : fullName, iban, pincode
        BankAccount bankAccount = bankAccountRepository.findByIban(loginRequest.getIban());
        if (bankAccount != null) {
            String fullName = bankAccount.getUser().getFirst_name() + " " + bankAccount.getUser().getLast_name();
            if (fullName.equals(loginRequest.getFullName()) && passwordEncoder.matches(loginRequest.getPincode().toString(), bankAccount.getPincode())) {
                return bankAccount;
            }
        }
        return null;
    }

    public BankAccountDTO convertToDTO(BankAccount bankAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();

        bankAccountDTO.setAccount_Id(bankAccount.getAccount_id());
        bankAccountDTO.setUser_id(bankAccount.getUser().getUser_id());
        bankAccountDTO.setIban(bankAccount.getIban());
        bankAccountDTO.setBalance(bankAccount.getBalance());
        bankAccountDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountDTO.setIs_active(bankAccount.getIs_active());
        bankAccountDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());
        bankAccountDTO.setPincode(bankAccount.getPincode());

        return bankAccountDTO;
    }

    public BankAccountRequestDTO convertToRequestDTO(BankAccount bankAccount) {
        BankAccountRequestDTO bankAccountRequestDTO = new BankAccountRequestDTO();

        bankAccountRequestDTO.setUser_id(bankAccount.getUser().getUser_id());
        bankAccountRequestDTO.setIban(bankAccount.getIban());
        bankAccountRequestDTO.setBalance(bankAccount.getBalance());
        bankAccountRequestDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountRequestDTO.setIs_active(bankAccount.getIs_active());
        bankAccountRequestDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());
        bankAccountRequestDTO.setPincode(bankAccount.getPincode());

        return bankAccountRequestDTO;
    }

    public BankAccountResponseDTO convertToResponseDTO(BankAccount bankAccount) {
        BankAccountResponseDTO bankAccountResponseDTO = new BankAccountResponseDTO();
        bankAccountResponseDTO.setAccount_id(bankAccount.getAccount_id());
        bankAccountResponseDTO.setUser(userService.convertToDTO(bankAccount.getUser()));
        bankAccountResponseDTO.setIban(bankAccount.getIban());
        bankAccountResponseDTO.setBalance(bankAccount.getBalance());
        bankAccountResponseDTO.setAccount_type(bankAccount.getAccount_type());
        bankAccountResponseDTO.setIs_active(bankAccount.getIs_active());
        bankAccountResponseDTO.setAbsolute_limit(bankAccount.getAbsolute_limit());
        return bankAccountResponseDTO;
    }

    public BankAccount convertToEntity(BankAccountRequestDTO bankAccountDTO) {
        BankAccount bankAccount = new BankAccount();

        User user = new User();
        user.setUser_id(bankAccountDTO.getUser_id());

        bankAccount.setUser(user);
        bankAccount.setIban(bankAccountDTO.getIban());
        bankAccount.setBalance(bankAccountDTO.getBalance());
        bankAccount.setAccount_type(bankAccountDTO.getAccount_type());
        bankAccount.setIs_active(bankAccountDTO.getIs_active());
        bankAccount.setAbsolute_limit(bankAccountDTO.getAbsolute_limit());
        bankAccount.setPincode(bankAccountDTO.getPincode());

        return bankAccount;
    }

}
