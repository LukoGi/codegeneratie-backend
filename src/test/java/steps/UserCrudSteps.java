package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
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

public class UserCrudSteps extends BaseSteps {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper mapper;
    private HttpHeaders httpHeaders;
    private String requestBody;
    private ResponseEntity<String> response;

    public UserCrudSteps() {
        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.add("Content-Type", "application/json");
    }

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
    public void theUserDataIsValid() throws JsonProcessingException {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@doe.com");
        user.setPhone_number("1234567890");
        user.setBsn_number("123456789");
        user.setPassword("password");

        requestBody = mapper.writeValueAsString(user);
    }

    @When("I create a new user")
    public void iCreateANewUser() {
        response = restTemplate
                .exchange("/users/create",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(201, response.getStatusCodeValue());
    }

    @And("the user data is invalid")
    public void theUserDataIsInvalid() {
        User user = new User();
        user.setUser_id(1);
        user.setFirst_name("John");
        user.setLast_name("Doe");
        user.setEmail("john@doe.com");
        user.setPhone_number("1234567890");
        user.setBsn_number("123456789");
        user.setPassword("");
    }

    @When("I create a new user with invalid data")
    public void iCreateANewUserWithInvalidData() {
        response = restTemplate
                .exchange("/users/create",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }


    @When("I retrieve the user by ID {int}")
    public void iRetrieveTheUserByID(int id) {
        response = restTemplate
                .exchange("/users/{id}",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Then("I should receive an error message")
    public void iShouldReceiveAnErrorMessage() {
        int statusCode = response.getStatusCodeValue();
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }

    @When("I update the user with ID {int}")
    public void iUpdateTheUserWithID(int id) {
        // fix this
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        Assertions.assertEquals(200, response.getStatusCodeValue());

    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCodeValue());
    }


}
