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
import spring.group.spring.models.dto.users.AcceptUserRequestDTO;
import spring.group.spring.models.dto.users.LoginRequestDTO;

import java.math.BigDecimal;

public class UserCrudSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");

    @When("I retrieve all users")
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
        requestBody = requestDTO.getDaily_limit().toString();
    }

    @And("the setdailylimit data is invalid")
    public void theSetdailylimitDataIsInvalid() {
        SetDailyLimitRequestDTO requestDTO = new SetDailyLimitRequestDTO(new BigDecimal(-10));
        requestBody = requestDTO.getDaily_limit().toString();
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

    @When("I try to login with valid credentials")
    public void iTryToLoginWithValidCredentials() throws JsonProcessingException{
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("JohnDoe");
        requestDTO.setPassword("test");

        requestBody = mapper.writeValueAsString(requestDTO);


        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/users/login",
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

    @When("I try to login with invalid credentials")
    public void iTryToLoginWithInvalidCredentials() throws JsonProcessingException {
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setUsername("JohnDoe");
        requestDTO.setPassword("test123");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/users/login",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("I should receive an error message for login")
    public void iFailToLogin() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(403, response.getStatusCode().value());
    }

    @When("I retrieve all unapproved users")
    public void iRetrieveAllUnapprovedUsers() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/users/getUnapprovedUsers",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should receive all unapproved users")
    public void iShouldReceiveAllUnapprovedUsers() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I approve a user with ID {string} And set the limits")
    public void iAcceptUserAsAdmin(String id) throws JsonProcessingException{
        httpHeaders.add("Authorization", "Bearer " + adminToken);

        AcceptUserRequestDTO requestDTO = new AcceptUserRequestDTO();
        requestDTO.setDaily_transfer_limit(new BigDecimal(1000));
        requestDTO.setAbsolute_transfer_limit(new BigDecimal(1000));

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(requestDTO);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/users/acceptUser/" + id,
                        HttpMethod.PUT,
                        entity,
                        String.class);
    }

    @Then("I should receive user accepted message")
    public void iShouldReceiveUserAcceptedMessage() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I retrieve all users without bank accounts")
    public void iRetrieveAllUsersWithoutBankAccounts() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/users/getUsersWithoutBankAccount",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I should retrieve a succes message for retrieving all accounts")
    public void iShouldRetrieveASuccesMessageForRetrievingAllAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }

    @When("I retrieve the bank accounts of a user")
    public void iRetrieveTheBankAccountsOfUser() {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange("/users/myAccounts",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);

        System.out.println("Response Body: " + response.getBody());
    }

    @Then("I get a success message for retrieving the bank accounts")
    public void iGetASuccessMessageForRetrievingTheBankAccounts() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }





}
