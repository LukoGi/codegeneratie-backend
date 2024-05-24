package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import spring.group.spring.models.User;

public class UserCrudSteps extends BaseSteps {

    @When("I retrieve all users")
    public void iRetrieveAllUsers() {
        response = restTemplate
                .exchange("/users/all",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }


    @And("the user data is valid")
    public void theUserDataIsValid() throws JsonProcessingException {
        User user = new User();
        user.setUser_id(5);
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
                .exchange("/users/createUser",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @And("the user data is invalid")
    public void theUserDataIsInvalid() {
        User user = new User();
        user.setUser_id(2);
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
        Assertions.assertEquals(405, response.getStatusCode().value());
    }


    @When("I retrieve the user by ID {int}")
    public void iRetrieveTheUserByID(int id) {
        response = restTemplate
                .exchange("/users/{id}",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Then("I should receive an error message")
    public void iShouldReceiveAnErrorMessage() {
        int statusCode = response.getStatusCode().value();
        Assertions.assertTrue(statusCode == 400 || statusCode == 404);
    }

    @When("I update the user with ID {int}")
    public void iUpdateTheUserWithID(int id) {
        response = restTemplate
                .exchange("/users/updateUser/{id}",
                        HttpMethod.PUT,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class,
                        id);
        System.out.println("Response Body: " + response.getBody());
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        Assertions.assertEquals(200, response.getStatusCode().value());
        Assertions.assertNotNull(response.getBody());
    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }


}
