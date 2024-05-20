/*
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
@CucumberContextConfiguration
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserCrudStepsTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
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


    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
*/
/*        response = restTemplate
                .exchange("/users/all",
                        HttpMethod.GET,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);*//*

    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
*/
/*        // check if the response is 200
        Assertions.assertEquals(200, response.getStatusCodeValue());*//*

    }

    @And("the user data is valid")
    public void theUserDataIsValid() throws JsonProcessingException, io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
*/
/*        // create an actual user here
        User user = new User();
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setPhone_number("123456789");
        user.setBsn_number("BSN123456");

        userJson = mapper.writeValueAsString(user);*//*

    }

    @When("I create a new user")
    public void iCreateANewUser() {
*/
/*        // Finish the User model so we can create a full user object
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.postForEntity("/users/createUser", request, String.class);
        httpHeaders.clear();*//*

    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() throws JsonProcessingException {
*/
/*        Assertions.assertEquals(201, response.getStatusCodeValue());

        User createdUser = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(createdUser);*//*

    }

    @And("the user data is invalid")
    public void theUserDataIsInvalid() throws JsonProcessingException {
*/
/*       // Same with here
        User user = new User();
        user.setFirst_name(null);
        user.setLast_name(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setPhone_number(null);
        user.setBsn_number(null);
            userJson = mapper.writeValueAsString(user);*//*

    }

    @When("I create a new user with invalid data")
    public void iCreateANewUserWithInvalidData() {
*/
/*        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.postForEntity("/users/createUser", request, String.class);
        httpHeaders.clear();*//*

    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
*/
/*        Assertions.assertEquals(400, response.getStatusCodeValue());*//*

    }

    @And("the user ID {int} exists")
    public void theUserIDExists(int id) {
*/
/*        response = restTemplate
                .getForEntity("/users/1", String.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());*//*

    }

    @When("I retrieve the user by ID")
    public void iRetrieveTheUserByID() {
*/
/*        response = restTemplate
                .getForEntity("/users/1", String.class);*//*

    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() throws JsonProcessingException, io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
*/
/*        User user = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(user);*//*

    }


    @And("the user ID {int} does not exist")
    public void theUserIDDoesNotExist(int id) {
*/
/*        response = restTemplate
                .getForEntity("/users/9999999", String.class);
        Assertions.assertEquals(404, response.getStatusCodeValue());*//*

    }

    @Then("I should receive an error message")
    public void iShouldReceiveAnErrorMessage() {
*/
/*        Assertions.assertEquals(404, response.getStatusCodeValue());*//*

    }

    @When("I update the user with ID {int}")
    public void iUpdateTheUser() {
*/
/*        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.exchange("/users/1",
                HttpMethod.PUT,
                request,
                String.class);
        httpHeaders.clear();*//*

    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() throws JsonProcessingException {
*/
/*        Assertions.assertEquals(200, response.getStatusCodeValue());
        User updatedUser = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(updatedUser);*//*

    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
*/
/*        Assertions.assertEquals(400, response.getStatusCodeValue());*//*

    }
}

    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
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
*/
