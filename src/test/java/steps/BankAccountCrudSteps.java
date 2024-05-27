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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
public class BankAccountCrudSteps extends BaseSteps {
    // create a JWT token to use for authentication


    @When("I retrieve all bank accounts")
    public void iRetrieveAllBankAccounts() {
        response = restTemplate
                .exchange("/accounts/all",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all bank accounts")
    public void iShouldReceiveAllBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @And("the bank account data is valid")
    public void theBankAccountDataIsValid() throws JsonProcessingException {
        User user = new User();
        user.setUser_id(2);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@doe.com");
        user.setPhone_number("1234567890");
        user.setBsn_number("123456789");
        user.setPassword("password");

        BankAccount bankAccount = new BankAccount("DE89370400440532013000", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234", user);
            requestBody = mapper.writeValueAsString(bankAccount);
    }

    @When("I create a new bank account")
    public void iCreateANewBankAccount() {
        System.out.println("Request Body: " + requestBody);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/create",
                        HttpMethod.POST,
                        entity,
                        String.class);
        System.out.println("Response Body: " + response.getBody());
    }

    @Then("the bank account should be created successfully")
    public void theBankAccountShouldBeCreatedSuccessfully() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @And("the bank account data is invalid")
    public void theBankAccountDataIsInvalid() throws JsonProcessingException {
        User user = new User();
        user.setUser_id(33);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@doe.com");
        user.setPhone_number("1234567890");
        user.setBsn_number("123456789");
        user.setPassword("password");

        BankAccount bankAccount = new BankAccount("abc", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234", user);
        requestBody = mapper.writeValueAsString(bankAccount);
    }

    @When("I create a new bank account with invalid data")
    public void iCreateANewBankAccountWithInvalidData() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/accounts/create",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the creation of the bank account should fail")
    public void theCreationOfTheBankAccountShouldFail() {
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }
    @When("I retrieve the bank account by ID {int}")
    public void iRetrieveTheBankAccountByID(int id) {
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
    }


    @Then("I should receive a bank account error message")
    public void iShouldReceiveABankAccountErrorMessage() {
        int statusCode = response.getStatusCode().value();
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }

    @When("I update the bank account with ID {int}")
    public void iUpdateTheBankAccountWithID(int id) throws JsonProcessingException {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@doe.com");
        user.setPhone_number("1234567890");
        user.setBsn_number("123456789");
        user.setPassword("password");

        BankAccount bankAccount = new BankAccount("DE89370400440532013000", new BigDecimal(100), AccountType.SAVINGS, true, new BigDecimal(1000), "1234", user);
        String requestBody = mapper.writeValueAsString(bankAccount);

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
    }

    @Then("the update of the bank account should fail")
    public void theUpdateOfTheBankAccountShouldFail() {
        int statusCode = response.getStatusCode().value();
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }
}
