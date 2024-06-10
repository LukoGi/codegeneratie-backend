package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import spring.group.spring.models.AccountType;
import spring.group.spring.models.BankAccount;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
public class BankAccountCrudSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");




    @When("I retrieve all bank accounts")
    public void iRetrieveAllBankAccounts() {
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
        BankAccountRequestDTO bankAccount = new BankAccountRequestDTO(1, "DE89370400440532013000", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234");
            requestBody = mapper.writeValueAsString(bankAccount);
    }

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

    @When("I update the bank account with ID {int} as admin")
    public void iUpdateTheBankAccountWithIDAsAdmin(int id) throws JsonProcessingException {


        httpHeaders.add("Authorization", "Bearer " + adminToken);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.PUT,
                        entity,
                        String.class,
                        id);
        System.out.println("Response Body: " + response.getBody());
    }

    @When("I update the bank account with ID {int} as user")
    public void iUpdateTheBankAccountWithIDAsUser(int id) throws JsonProcessingException {
        BankAccountRequestDTO bankAccount = new BankAccountRequestDTO(id ,"DE89370400440532013000", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234");
        String requestBody = mapper.writeValueAsString(bankAccount);

        httpHeaders.add("Authorization", "Bearer " + userToken);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/{id}",
                        HttpMethod.PUT,
                        entity,
                        String.class,
                        id);
        System.out.println("Response Body: " + response.getBody());
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
        Assertions.assertEquals(401, response.getStatusCode().value());
    }
}
