import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import spring.group.spring.models.User;

import java.util.Arrays;
import java.util.List;

// UserController still needs to be created

public class UserCrudStepsDefinitions extends BaseStepsDefinition {

@Autowired
    private TestRestTemplate restTemplate;
    private ObjectMapper mapper;
    private ResponseEntity<String> response;
    private HttpHeaders httpHeaders = new HttpHeaders();
    private String userJson;

    // add this to the base class maybe?
    @Given("the endpoint for {string} is available for method {string}")
    public void theEndpointForIsAvailableForMethod(String endpoint, String method) {
/*        response = restTemplate
                .exchange("/" + endpoint,
                        HttpMethod.OPTIONS,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);
        List<String> options = Arrays.stream(response.getHeaders()
                        .get("Allow")
                        .get(0)
                        .split(","))
                .toList();
        Assertions.assertTrue(options.contains(method.toUpperCase()));*/
    }

    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
/*        response = restTemplate
                .exchange("/users",
                        HttpMethod.GET,
                        new HttpEntity<>(null, new HttpHeaders()),
                        String.class);*/
    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
        /*Assertions.assertEquals(200, response.getStatusCodeValue());*/
    }

    @And("the user data is valid")
    public void theUserDataIsValid() throws JsonProcessingException, io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
/*        // create an actual user here
        User user = new User();
        user.setFirst_name("John");
        user.setAccounts(null);
        userJson = mapper.writeValueAsString(user);*/
    }

    @When("I create a new user")
    public void iCreateANewUser() {
/*        // Finish the User model so we can create a full user object
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.postForEntity("/Users", request, String.class);
        httpHeaders.clear();*/
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
/*        Assertions.assertEquals(201, response.getStatusCodeValue());

        User createdUser = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(createdUser.getId());*/
    }

    @And("the user data is invalid")
    public void theUserDataIsInvalid() throws JsonProcessingException, io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
/*       // Same with here
        User user = new User();
        user.setFirst_name(null);
        user.setAccounts(null);
            userJson = mapper.writeValueAsString(user);*/
    }

    @When("I create a new user with invalid data")
    public void iCreateANewUserWithInvalidData() {
/*        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.postForEntity("/Users", request, String.class);
        httpHeaders.clear();*/
    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
/*        Assertions.assertEquals(400, response.getStatusCodeValue());*/
    }

    @And("the user ID {int} exists")
    public void theUserIDExists(int id) {
/*        response = restTemplate
                .getForEntity("/users/1", String.class);
        Assertions.assertEquals(200, response.getStatusCodeValue());*/
    }

    @When("I retrieve the user by ID")
    public void iRetrieveTheUserByID() {
/*        response = restTemplate
                .getForEntity("/users/1", String.class);*/
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() throws JsonProcessingException, io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
/*        User user = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(user);*/
    }


    @And("the user ID {int} does not exist")
    public void theUserIDDoesNotExist(int id) {
/*        response = restTemplate
                .getForEntity("/users/9999999", String.class);
        Assertions.assertEquals(404, response.getStatusCodeValue());*/
    }

    @Then("I should receive an error message")
    public void iShouldReceiveAnErrorMessage() {
/*        Assertions.assertEquals(404, response.getStatusCodeValue());*/
    }

    @When("I update the user")
    public void iUpdateTheUser() {
/*        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(userJson, httpHeaders);
        response = restTemplate.exchange("/users/1",
                HttpMethod.PUT,
                request,
                String.class);
        httpHeaders.clear();*/
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() throws io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException {
/*        Assertions.assertEquals(200, response.getStatusCodeValue());
        User updatedUser = mapper.readValue(response.getBody(), User.class);
        Assertions.assertNotNull(updatedUser);*/

    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
/*        Assertions.assertEquals(400, response.getStatusCodeValue());*/
    }
}
