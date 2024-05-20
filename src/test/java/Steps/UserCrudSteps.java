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

public class UserCrudSteps extends BaseSteps {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    private ResponseEntity<String> response;
    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
        response = restTemplate
                .exchange("/users/all",
                        HttpMethod.GET,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);

    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
        Assertions.assertEquals(200, response.getStatusCodeValue());
        /*        System.out.println("Response Body: " + response.getBody()); // check if it actually returns the thing*/

    }

    @And("the user data is valid")
    public void theUserDataIsValid() {
    }

    @When("I create a new user")
    public void iCreateANewUser() {
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
    }

    @And("the user data is invalid")
    public void theUserDataIsInvalid() {
    }

    @When("I create a new user with invalid data")
    public void iCreateANewUserWithInvalidData() {
    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
    }

    @And("the user ID {int} exists")
    public void theUserIDExists(int arg0) {
    }

    @When("I retrieve the user by ID")
    public void iRetrieveTheUserByID() {
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
    }

    @And("the user ID {int} does not exist")
    public void theUserIDDoesNotExist(int arg0) {
    }

    @Then("I should receive an error message")
    public void iShouldReceiveAnErrorMessage() {
    }

    @When("I update the user with ID {int}")
    public void iUpdateTheUserWithID(int arg0) {
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
    }
}
