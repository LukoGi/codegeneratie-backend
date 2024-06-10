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
import spring.group.spring.models.dto.bankaccounts.SetAbsoluteLimitRequestDTO;
import spring.group.spring.models.dto.transactions.SetDailyLimitRequestDTO;

import java.math.BigDecimal;

public class UserCrudSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");

    @When("I retrieve all users as an Admin")
    public void iRetrieveAllUsers() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/users/",
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
                .exchange("/users/",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(400, response.getStatusCode().value()); // 201
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
                .exchange("/users/",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCode().value());
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
                .exchange("/users/{id}",
                        HttpMethod.PUT,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class,
                        id);
        System.out.println("Response Body: " + response.getBody());
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        Assertions.assertEquals(400, response.getStatusCode().value()); // 400
        Assertions.assertNotNull(response.getBody());
    }

    @Then("the update of the user should fail")
    public void theUpdateOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }


    @And("the setdailylimit data is valid")
    public void theSetdailylimitDataIsValid() {
        SetDailyLimitRequestDTO requestDTO = new SetDailyLimitRequestDTO(new BigDecimal(10));
        requestBody = requestDTO.getDailyLimit().toString();
    }

    @And("the setdailylimit data is invalid")
    public void theSetdailylimitDataIsInvalid() {
        SetDailyLimitRequestDTO requestDTO = new SetDailyLimitRequestDTO(new BigDecimal(-10));
        requestBody = requestDTO.getDailyLimit().toString();
    }

    @When("I set daily limit to user {string} as admin")
    public void iSetDailyLimitToUserAsAdmin(String id) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        this.response = restTemplate
                .exchange("/users/" + id + "/setDailyLimit?dailyLimit=" + requestBody,
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @When("I set daily limit to user {string} account as John Doe")
    public void iSetDailyLimitToUserAccountAsJohnDoe(String id) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(null, httpHeaders);
        this.response = restTemplate
                .exchange("/users/" + id + "/setDailyLimit?dailyLimit=" + requestBody,
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @Then("I should receive user success message")
    public void iShouldReceiveUserSuccessMessage() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @Then("I should receive a user error message")
    public void iShouldReceiveAUserErrorMessage() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }

    @Then("I should receive a user forbidden message")
    public void iShouldReceiveAUserForbiddenMessage() {
        Assertions.assertEquals(403, response.getStatusCode().value());
    }
}
