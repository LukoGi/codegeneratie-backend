package spring.group.spring.services;

import org.springframework.stereotype.Service;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.dto.BankAccountDTO;
import spring.group.spring.models.dto.BankAccountRequestDTO;
import spring.group.spring.models.dto.BankAccountResponseDTO;
import spring.group.spring.repositories.BankAccountRepository;

import java.util.List;

@Service
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final UserService userService;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userService = new UserService();
    }

    public BankAccount createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }

    public BankAccount getBankAccountById(Integer id) {
        return bankAccountRepository.findById(id).orElse(null);
    }

    public BankAccount updateBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    public BankAccountDTO convertToDTO(BankAccount bankAccount) {
        BankAccountDTO bankAccountDTO = new BankAccountDTO();

        bankAccountDTO.setAccount_Id(bankAccount.getAccount_id());
        bankAccountDTO.setUser(userService.convertToDTO(bankAccount.getUser()));
        bankAccountDTO.setIban(bankAccount.getIban());
        bankAccountDTO.setBalance(bankAccount.getBalance());
        bankAccountDTO.setAccount_Type(bankAccount.getAccount_type());
        bankAccountDTO.setIs_Active(bankAccount.getIs_active());
        bankAccountDTO.setAbsolute_Limit(bankAccount.getAbsolute_limit());
        bankAccountDTO.setPincode(bankAccount.getPincode());

        return bankAccountDTO;
    }

    public BankAccountRequestDTO convertToRequestDTO(BankAccount bankAccount) {
        BankAccountRequestDTO bankAccountRequestDTO = new BankAccountRequestDTO();

        bankAccountRequestDTO.setUser(userService.convertToDTO(bankAccount.getUser()));
        bankAccountRequestDTO.setIban(bankAccount.getIban());
        bankAccountRequestDTO.setBalance(bankAccount.getBalance());
        bankAccountRequestDTO.setAccount_Type(bankAccount.getAccount_type());
        bankAccountRequestDTO.setIs_Active(bankAccount.getIs_active());
        bankAccountRequestDTO.setAbsolute_Limit(bankAccount.getAbsolute_limit());
        bankAccountRequestDTO.setPincode(bankAccount.getPincode());

        return bankAccountRequestDTO;
    }

    public BankAccountResponseDTO convertToResponseDTO(BankAccount bankAccount) {
        BankAccountResponseDTO bankAccountResponseDTO = new BankAccountResponseDTO();
        bankAccountResponseDTO.setAccount_id(bankAccount.getAccount_id());
        bankAccountResponseDTO.setUser(userService.convertToDTO(bankAccount.getUser()));
        bankAccountResponseDTO.setIban(bankAccount.getIban());
        bankAccountResponseDTO.setBalance(bankAccount.getBalance());
        bankAccountResponseDTO.setAccount_Type(bankAccount.getAccount_type());
        bankAccountResponseDTO.setIs_Active(bankAccount.getIs_active());
        bankAccountResponseDTO.setAbsolute_Limit(bankAccount.getAbsolute_limit());
        return bankAccountResponseDTO;
    }


}
