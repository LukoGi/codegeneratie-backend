package Steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import spring.group.spring.Application;
import spring.group.spring.models.BankAccount;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
public class BankAccountCrudSteps extends BaseSteps {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    private HttpHeaders httpHeaders;
    private String endpoint;
    private String requestBody;
    private ResponseEntity<String> response;


    @Given("the endpoint for {string} is available for method {string}")
    public void theEndpointForIsAvailableForMethod(String endpoint, String method) {
        response = restTemplate
                .exchange("/" + endpoint,
                        HttpMethod.OPTIONS,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);
        List<String> options = Arrays.stream((response.getHeaders()
                        .get("Allow")
                        .get(0)
                        .split(",")))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));

        this.endpoint = endpoint;
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.add("Content-Type", "application/json");

    }

    @When("I retrieve all bank accounts")
    public void iRetrieveAllBankAccounts() {
        response = restTemplate
                .exchange("/accounts/all",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);
    }

    @Then("I should receive all bank accounts")
    public void iShouldReceiveAllBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @And("the bank account data is valid")
    public void theBankAccountDataIsValid() throws JsonProcessingException {
        BankAccount bankAccount = new BankAccount("RO123456789", new BigDecimal(100), "SAVINGS", true, new BigDecimal(1000), "1234", null);
            requestBody = mapper.writeValueAsString(bankAccount);
    }

    @When("I create a new bank account")
    public void iCreateANewBankAccount() {
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
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @And("the bank account data is invalid")
    public void theBankAccountDataIsInvalid() {
    }

    @When("I create a new bank account with invalid data")
    public void iCreateANewBankAccountWithInvalidData() {
    }

    @Then("the creation of the bank account should fail")
    public void theCreationOfTheBankAccountShouldFail() {
    }

    @And("the bank account ID {int} exists")
    public void theBankAccountIDExists(int arg0) {
    }

    @When("I retrieve the bank account by ID {int}")
    public void iRetrieveTheBankAccountByID(int arg0) {
    }

    @Then("I should receive the bank account details")
    public void iShouldReceiveTheBankAccountDetails() {
    }

    @And("the bank account ID {int} does not exist")
    public void theBankAccountIDDoesNotExist(int arg0) {
    }

    @Then("I should receive a bank account error message")
    public void iShouldReceiveABankAccountErrorMessage() {
    }

    @When("I update the bank account with ID {int}")
    public void iUpdateTheBankAccountWithID(int arg0) {
    }

    @Then("the bank account should be updated successfully")
    public void theBankAccountShouldBeUpdatedSuccessfully() {
    }

    @Then("the update of the bank account should fail")
    public void theUpdateOfTheBankAccountShouldFail() {
    }
}
