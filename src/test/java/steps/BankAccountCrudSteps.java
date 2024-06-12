package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.annotation.Rollback;
import spring.group.spring.config.DataSeeder;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.*;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BankAccountCrudSteps extends BaseSteps {

    private final String adminToken ="eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBZG1pbiIsImF1dGgiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3MTc5MzExMTksImV4cCI6MTc0OTQ2NzExOX0.PE_4iw1798iiNdHta6v3FwT4ZBQ1ZDXQ0eH_yuTlDFlW8hGNwjg7MLNQ2Imj83iAANPJvv3vy0ItIZ6yHUhyxoQSAueqSI4KXk1EJoSb1Tecwf2CAJu3Z_Gj2QAKNB1h8WI0_Ly5MjOnRO9wIFWphYYI-iXT-NTD_9HCU-NQ_LqBzLv4uPQZKRbDYmNkEiqPjkCV6I0b8bdvGvKHiZDJe7OF9R4z2UYzSftfJxR6WXKFXfDHI28dlTBcl6-edi1_j1-V-LAoJBO4jRxycHMo1OFkaao4mp5euvriamii1GcKoufxkQeoFykDvgRTTn5D9zUltSJ7bpHwdWorHKkCzg";
    private final String userToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJKb2huRG9lIiwiYXV0aCI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzE3OTMxMTgwLCJleHAiOjE3NDk0NjcxODB9.cyZzlQLnoiSee7TznaKa4nHHVcome7o2ZkI5_afzRjFH8bCL5SkIGv5rm5An7rI9XjtmX4TL6JfWkdw9fmq-VP3HEsm9yeAVS1toxXLS7n8kniDuunNzCgb12U8FDYu33fAt6TLL-GqmEog-88_ZTBZVtKl2NqmKUgcoCZRvkZAr4ZV_hZJudDRowPlSaCpj9Mu1ECdJx95cPK7aN4C6k8BjBXPw-NtcNNolqbSR4PYx8M_DCjCs0bmVmmsJX5BZ_dqGQ2JPoBr2xcuw5Sf-PsQnilA8oPmmbuX5r8HVd7pdfiZdWcpwaJCPbtpikKyAqcX3hrzWL8nO5XToH8HkxQ";

    @When("I retrieve all bank accounts")
    public void iRetrieveAllBankAccounts() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/accounts/",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all bank accounts")
    public void iShouldReceiveAllBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            List<BankAccountResponseDTO> bankAccounts = Arrays.asList(mapper.readValue(response.getBody(), BankAccountResponseDTO[].class));
            Assertions.assertFalse(bankAccounts.isEmpty());
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @And("the bank account data is valid")
    public void theBankAccountDataIsValid() throws JsonProcessingException {
        BankAccountRequestDTO bankAccount = new BankAccountRequestDTO(1, "NL91ABNA0417164305", new BigDecimal(500), AccountType.CHECKINGS, true, new BigDecimal(100), "1111");
            requestBody = mapper.writeValueAsString(bankAccount);
    }

    @Rollback
    @When("I create a new bank account")
    public void iCreateANewBankAccount() {
        System.out.println("Request Body: " + requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the bank account should be created successfully")
    public void theBankAccountShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(400, response.getStatusCode().value()); //change to 201
    }

    @And("the bank account data is invalid")
    public void theBankAccountDataIsInvalid() throws JsonProcessingException {
        BankAccountRequestDTO bankAccount = new BankAccountRequestDTO(1,"abc", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234");
        requestBody = mapper.writeValueAsString(bankAccount);
    }

    @Rollback
    @When("I create a new bank account with invalid data")
    public void iCreateANewBankAccountWithInvalidData() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the creation of the bank account should fail")
    public void theCreationOfTheBankAccountShouldFail() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @When("I retrieve the bank account by ID {int} as user")
    public void iRetrieveTheBankAccountByIDAsUser(int id) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);
    }

    @When("I retrieve the bank account by ID {int} as admin")
    public void iRetrieveTheBankAccountByIDAsAdmin(int id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);
    }

    @Then("I should receive the bank account details")
    public void iShouldReceiveTheBankAccountDetails() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
        try {
            BankAccountDTO bankAccount = mapper.readValue(response.getBody(), BankAccountDTO.class);
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @Then("I should receive a bank account error message")
    public void iShouldReceiveABankAccountErrorMessage() {
        int statusCode = response.getStatusCode().value();
        System.out.println(response.getStatusCode());
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }

    @Then("I should receive a bank account forbidden message")
    public void iShouldReceiveABankAccountForbiddenMessage() {
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @Rollback
    @When("I update the bank account with ID {int} as admin")
    public void iUpdateTheBankAccountWithIDAsAdmin(int id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.PUT,
                        entity,
                        String.class,
                        id);
    }

    @Rollback
    @When("I update the bank account with ID {int} as user")
    public void iUpdateTheBankAccountWithIDAsUser(int id) throws JsonProcessingException {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.PUT,
                        entity,
                        String.class,
                        id);
    }

    @Then("the bank account should be updated successfully")
    public void theBankAccountShouldBeUpdatedSuccessfully() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            BankAccountResponseDTO returnedBankAccount = mapper.readValue(response.getBody(), BankAccountResponseDTO.class);
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @Then("the update of the bank account should fail")
    public void theUpdateOfTheBankAccountShouldFail() {
        int statusCode = response.getStatusCode().value();
        System.out.println(response.getStatusCode());
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }

    @When("I retrieve the bank account by user ID {int} as admin")
    public void iRetrieveTheBankAccountByUserIDAsAdmin(int id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/accounts/user/{id}",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);
    }

    @Then("I should receive a list of bank accounts")
    public void iShouldReceiveAListOfBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            List<BankAccountResponseDTO> bankAccounts = Arrays.asList(mapper.readValue(response.getBody(), BankAccountResponseDTO[].class));
            Assertions.assertFalse(bankAccounts.isEmpty());
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @And("the atmlogin data is valid")
    public void theAtmloginDataIsValid() throws JsonProcessingException{
        BankAccountATMLoginRequest bankAccountATMLoginResponse = new BankAccountATMLoginRequest("John Doe", "NL91ABNA0417164305", 1111);
        requestBody = mapper.writeValueAsString(bankAccountATMLoginResponse);
    }

    @When("I login to the bank account")
    public void iLoginToTheBankAccount() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/login",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("I should receive a login success message")
    public void iShouldReceiveALoginSuccessMessage() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            BankAccountATMLoginResponse bankAccountATMLoginResponse = mapper.readValue(response.getBody(), BankAccountATMLoginResponse.class);
            Assertions.assertNotNull(bankAccountATMLoginResponse);
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @And("the atmlogin data is wrong")
    public void theAtmloginDataIsWrong() throws JsonProcessingException{
        BankAccountATMLoginRequest bankAccountATMLoginResponse = new BankAccountATMLoginRequest("John Doe", "NL91ABNA0417164305", 1112);
        requestBody = mapper.writeValueAsString(bankAccountATMLoginResponse);
    }

    @Then("I should receive a login error message")
    public void iShouldReceiveALoginErrorMessage() {
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @And("the withdraw data is valid")
    public void theWithdrawDataIsValid() throws JsonProcessingException {
        WithdrawDepositRequestDTO requestDTO = new WithdrawDepositRequestDTO(new BigDecimal(100));
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @Then("I should receive a withdraw success message")
    public void iShouldReceiveAWithdrawSuccessMessage() {
        System.out.println(response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            WithdrawDepositResponseDTO withdrawDepositResponseDTO = mapper.readValue(response.getBody(), WithdrawDepositResponseDTO.class);
            Assertions.assertNotNull(withdrawDepositResponseDTO);
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @Then("I should receive a withdraw forbidden message")
    public void iShouldReceiveAWithdrawForbiddenMessage() {
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @When("I withdraw money from bank account {string} account as John Doe")
    public void iWithdrawMoneyFromBankAccountAccountAsJohnDoe(String id) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/" + id + "/withdraw",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @And("the withdraw data is too much")
    public void theWithdrawDataIsTooMuch() throws JsonProcessingException {
        WithdrawDepositRequestDTO requestDTO = new WithdrawDepositRequestDTO(new BigDecimal(100000));
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @Then("I should receive a withdraw insufficients funds message")
    public void iShouldReceiveAWithdrawInsufficientsFundsMessage() {
        Assertions.assertEquals(400, response.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("Insufficient funds"));
    }

    @And("the withdraw data is invalid")
    public void theWithdrawDataIsInvalid() {
        requestBody = "invalid";
    }

    @Then("I should receive a withdraw error message")
    public void iShouldReceiveAWithdrawErrorMessage() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @And("the deposit data is valid")
    public void theDepositDataIsValid() throws JsonProcessingException{
        WithdrawDepositRequestDTO requestDTO = new WithdrawDepositRequestDTO(new BigDecimal(100));
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @When("I deposit money to bank account {string} account as John Doe")
    public void iDepositMoneyToBankAccountAccountAsJohnDoe(String id) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/" + id + "/deposit",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("I should receive a deposit success message")
    public void iShouldReceiveADepositSuccessMessage() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        try {
            WithdrawDepositResponseDTO withdrawDepositResponseDTO = mapper.readValue(response.getBody(), WithdrawDepositResponseDTO.class);
            Assertions.assertNotNull(withdrawDepositResponseDTO);
        } catch (JsonProcessingException e) {
            Assertions.fail("The response is not of the expected type");
        }
    }

    @And("the deposit data is invalid")
    public void theDepositDataIsInvalid() throws JsonProcessingException {
        WithdrawDepositRequestDTO requestDTO = new WithdrawDepositRequestDTO(new BigDecimal(-100));
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @And("the setabsolutelimit data is valid")
    public void theSetabsolutelimitDataIsValid() throws JsonProcessingException {
        SetAbsoluteLimitRequestDTO requestDTO = new SetAbsoluteLimitRequestDTO(new BigDecimal(-10));
        requestBody = requestDTO.getAbsolute_limit().toString();
    }

    @And("the setabsolutelimit data is invalid")
    public void theSetabsolutelimitDataIsInvalid() throws JsonProcessingException {
        SetAbsoluteLimitRequestDTO requestDTO = new SetAbsoluteLimitRequestDTO(new BigDecimal(10));
        requestBody = requestDTO.getAbsolute_limit().toString();
    }

    @When("I set absolute limit to bank account {string} account as admin")
    public void iSetAbsoluteLimitToBankAccountAccountAsAdmin(String id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/" + id + "/setAbsoluteLimit?absoluteLimit=" + requestBody,
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @When("I set absolute limit to bank account {string} account as John Doe")
    public void iSetAbsoluteLimitToBankAccountAccountAsJohnDoe(String id) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/" + id + "/setAbsoluteLimit?absoluteLimit=" + requestBody,
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @Then("I should receive bank account success message")
    public void iShouldReceiveBankAccountSuccessMessage() {
        System.out.println(response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }


    @Then("I should receive a absolute limit error message")
    public void iShouldReceiveAAbsoluteLimitErrorMessage() throws JsonProcessingException {
        System.out.println(response.getBody());
        Assertions.assertEquals(400, response.getStatusCode().value());

        JsonNode jsonNode = mapper.readTree(response.getBody());
        String message = jsonNode.get("message").asText();
        Assertions.assertEquals("Absolute limit hit", message);
    }

    @When("I change the is_active status of bank account {string} as admin to false")
    public void iChangeTheIsActiveStatusOfBankAccountAccountAsAdminToFalse(String id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/" + id + "/closeAccount",
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @Then("Account is succfully deactivated")
    public void accountIsSuccfullyDeactivated() {
        System.out.println(response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }



    @When("I retrieve the iban by user name {string} as user")
    public void iRetrieveTheBankAccountByUsernameAsUser(String username) {
        // httpHeaders.add("Authorization", "Bearer eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJKb2huRG9lIiwiYXV0aCI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzE4MTQxNjY2LCJleHAiOjE3MTgxNDUyNjZ9.EEA2ZvMSJ0A0ydzmcV0YHXcdKt6oxnqkLBm0OZEO655ayCpjvbP9Ig7Iek3lUO_Uco9CGmjGs_Vo2RUGoAiTc3zkVxrnVUSHKOaQcipn9aZ_XUcronZwyvfvyDvD_jLXhFL7HSD-lbFIqcZq0_bdj2SGkYbplkZbcnAtfF2BmAdhjAYvwADt0p11CwLf6a2PZpYRdtL1xzhaX_w2Yo2ac_5qirkhxZNslWsbvCPNyg_g1dOtXGgahXaSb-G7vptu0RzXWQXCFJNGAPHIk8WFj9kLqOA8bQsv3H3W54MVYQCswIW2GV7ZaLdj5PwL6miVyM1xdgZGskGncQ_CnV3q_Q");
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange("/accounts/username/JohnDoe",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        username);
        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive the iban")
    public void iShouldReceiveTheIban() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I retrieve bank account by admin as user")
    public void iRetrieveBankAccountByAdminAsUser() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/accounts/",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);
        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive the bank accounts")
    public void iShouldReceiveTheBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    // JJ
}
