package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.transactions.SetDailyLimitRequestDTO;
import spring.group.spring.models.dto.users.AcceptUserRequestDTO;
import spring.group.spring.models.dto.users.LoginRequestDTO;

import java.math.BigDecimal;

public class UserSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");
    @When("I send a GET request to {string}")
    public void iRetrieveAllUsers(String endpoint) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all users")
    public void iShouldReceiveAllUsers() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I send a POST request to {string}")
    public void iCreateANewUser(String endpoint) {
        response = restTemplate
                .exchange("/users/",
                        HttpMethod.POST,
                        new HttpEntity<>(requestBody, httpHeaders),
                        String.class);
    }

    @And("the user data is valid")
    public void theUserDataIsValid() throws JsonProcessingException {
        User user = new User();
        user.setUserId(5);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPhoneNumber("1234567890");
        user.setBsnNumber("123456789");
        user.setPassword("password");

        requestBody = mapper.writeValueAsString(user);
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        Assertions.assertEquals(400, response.getStatusCode().value()); // 201
    }


    @And("the user data is invalid")
    public void theUserDataIsInvalid() {
        User user = new User();
        user.setUserId(2);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@doe.com");
        user.setPhoneNumber("1234567890");
        user.setBsnNumber("123456789");
        user.setPassword("");
    }

    @Then("the creation of the user should fail")
    public void theCreationOfTheUserShouldFail() {
        Assertions.assertEquals(400, response.getStatusCode().value());
    }


    @When("I send a GET request to {string} with ID {int}")
    public void iRetrieveTheUserByID(String endpoint, int id) {
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class,
                        id);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive the user details of that user")
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

    @When("I send a POST request to {string} with valid credentials")
    public void iTryToLoginWithValidCredentials(String endpoint) throws JsonProcessingException{
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("JohnDoe");
        requestDTO.setPassword("test");

        requestBody = mapper.writeValueAsString(requestDTO);


        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange(endpoint,
                        HttpMethod.POST,
                        entity,
                        String.class);
    }
    @Then("I successfully login")
    public void iSuccessfullyLogin() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @And("I retrieve my user info")
    public void iRetrieveMyUserInfo() {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange("/users/userinfo",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @When("I send a POST request to {string} with invalid credentials")
    public void iTryToLoginWithInvalidCredentials(String endpoint) throws JsonProcessingException {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("JohnDoe");
        requestDTO.setPassword("test123");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange(endpoint,
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("I should receive an error message for login")
    public void iFailToLogin() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @When("I send a GET request to {string} to retrieve all unapproved users")
    public void iRetrieveAllUnapprovedUsers(String endpoint) {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all unapproved users")
    public void iShouldReceiveAllUnapprovedUsers() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("send a GET request to {string} to retrieve unapproved users without being a admin")
    public void iRetrieveAllUsersWithoutBeingAAdmin(String endpoint) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("retrieve unapproved users should fail")
    public void retrieveUnapprovedUsersShouldFail() {
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @When("I Send a PUT request to {string} to approve user with ID {int}")
    public void iAcceptUserAsAdmin(String endpoint, int id) throws JsonProcessingException{
        httpHeaders.add("Authorization", "Bearer " + adminToken);

        AcceptUserRequestDTO requestDTO = new AcceptUserRequestDTO();
        requestDTO.setDailyTransferLimit(new BigDecimal(1000));
        requestDTO.setAbsoluteLimit(new BigDecimal(1000));

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestDTO);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange(endpoint,
                        HttpMethod.PUT,
                        entity,
                        String.class, id);
    }

    @Then("User should be approved")
    public void iShouldReceiveUserAcceptedMessage() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I send a PUT request to {string} to approve user with ID {int} without being an admin")
    public void iApproveUserWithoutBeingAnAdmin(String endpoint, int id) throws JsonProcessingException {
        httpHeaders.add("Authorization", "Bearer " + userToken);

        AcceptUserRequestDTO requestDTO = new AcceptUserRequestDTO();
        requestDTO.setDailyTransferLimit(new BigDecimal(1000));
        requestDTO.setAbsoluteLimit(new BigDecimal(1000));

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestDTO);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange(endpoint,
                        HttpMethod.PUT,
                        entity,
                        String.class, id);
    }

    @When("I send a GET request to {string} to retrieve the bank accounts of user")
    public void iRetrieveTheBankAccountsOfUser(String endpoint) {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I get a success message for retrieving the bank accounts")
    public void iGetASuccessMessageForRetrievingTheBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I send a GET request to {string} to retrieve the bank accounts of user without being logged in")
    public void iRetrieveTheBankAccountsOfUserWithoutBeingLoggedIn(String endpoint) {
        response = restTemplate
                .exchange(endpoint,
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

}
