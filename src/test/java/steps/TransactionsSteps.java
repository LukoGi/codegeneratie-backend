package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import spring.group.spring.models.dto.transactions.*;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionsSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");
    private final String janeUserToken = System.getenv("JANE_USER_TOKEN");

    @When("I request to get transactions by the user admin")
    public void iRequestToGetTransactionsByTheUserAdmin() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/transactions/",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);
    }

    @Then("I should receive transactions")
    public void iShouldReceiveTransactions() throws JsonProcessingException {
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO[] transactions = mapper.readValue(response.getBody(), TransactionResponseDTO[].class);

        Assertions.assertNotNull(transactions, "Transactions should not be null");
        Assertions.assertTrue(transactions.length > 0, "Transactions list should not be empty");
    }

    @When("I request to get transactions by the user")
    public void iRequestToGetTransactionsByTheUser() {
        httpHeaders.add("Authorization", "Bearer " + userToken);
        response = restTemplate
                .exchange("/transactions/account/1",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);
    }

    @Then("I should receive my transactions")
    public void iShouldReceiveMyTransactions() throws JsonProcessingException {
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO[] transactions = mapper.readValue(response.getBody(), TransactionResponseDTO[].class);

        Assertions.assertNotNull(transactions, "Transactions should not be null");
        Assertions.assertTrue(transactions.length > 0, "Transactions list should not be empty");
    }

    @When("I request to get transactions as admin of a user")
    public void iRequestToGetTransactionsAsAdminOfAUser() {
        httpHeaders.add("Authorization", "Bearer " + adminToken);
        response = restTemplate
                .exchange("/transactions/users/1",
                        HttpMethod.GET,
                        new HttpEntity<>(null, httpHeaders),
                        String.class);
    }

    @Then("I should receive my transactions by id")
    public void iShouldReceiveMyTransactionsById() throws JsonProcessingException {
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO transaction = mapper.readValue(response.getBody(), TransactionResponseDTO.class);

        Assertions.assertNotNull(transaction.getTransactionId(), "Transaction ID should not be null");
    }

    // Successfully Create transaction with valid IBAN as customer HERE

    @And("the transaction IBAN data is valid")
    public void theTransactionIbanDataIsValid() throws JsonProcessingException {
        CustomerTransactionRequestDTO requestDTO = new CustomerTransactionRequestDTO();
        requestDTO.setInitiatorUserId(1);
        requestDTO.setToAccountIban("NL91ABNA0417164306");
        requestDTO.setTransferAmount(new BigDecimal(1));
        requestDTO.setDescription("test");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
    }

    @When("I create a new transaction as customer")
    public void iCreateANewTransactionAsCustomer() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/customer",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the transaction should be created successfully")
    public void theTransactionShouldBeCreatedSuccessfully() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO responseDTO = null;
        try {
            responseDTO = mapper.readValue(response.getBody(), TransactionResponseDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getTransactionId());
    }

    // FAIL TO CREATE WITH INVALID IBAN

    @And("the transaction IBAN data is invalid")
    public void theTransactionIbanDataIsInvalid() throws JsonProcessingException {
        CustomerTransactionRequestDTO requestDTO = new CustomerTransactionRequestDTO();
        requestDTO.setInitiatorUserId(1);
        requestDTO.setToAccountIban("INVALID_IBAN"); // Set an invalid IBAN
        requestDTO.setTransferAmount(new BigDecimal(1));
        requestDTO.setDescription("test");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
    }

    @When("I create a new transaction with invalid IBAN as customer")
    public void iCreateANewTransactionWithInvalidIbanAsCustomer() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/customer",
                            HttpMethod.POST,
                            entity,
                            String.class);
        } catch (HttpClientErrorException e) {
            this.response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @Then("the creation of the transaction should fail")
    public void theCreationOfTheTransactionShouldFail() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertNotEquals(200, response.getStatusCode().value());
    }

    // Create Internal Transaction STEPS HERE

    @And("the internal transaction data is valid")
    public void theInternalTransactionDataIsValid() throws JsonProcessingException {
        InternalTransactionRequestDTO requestDTO = new InternalTransactionRequestDTO();
        requestDTO.setInitiatorUserId(2);
        requestDTO.setFromAccountType("CHECKINGS");
        requestDTO.setToAccountType("SAVINGS");
        requestDTO.setTransferAmount(new BigDecimal(2));

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + janeUserToken);
    }

    @When("I create internal transaction")
    public void iCreateInternalTransaction() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/customer/internal",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the internal transaction should be created successfully")
    public void theInternalTransactionShouldBeCreatedSuccessfully() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO responseDTO = null;
        try {
            responseDTO = mapper.readValue(response.getBody(), TransactionResponseDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getTransactionId());
    }

    // FAIL TO CREATE INTERNAL TRANSACTION WITH INVALID DATA

    @And("the internal transaction data is invalid")
    public void theInternalTransactionDataIsInvalid() throws JsonProcessingException {
        InternalTransactionRequestDTO requestDTO = new InternalTransactionRequestDTO();
        requestDTO.setInitiatorUserId(-1); // Invalid user ID
        requestDTO.setFromAccountType("INVALID"); // Invalid account type
        requestDTO.setToAccountType("INVALID"); // Invalid account type
        requestDTO.setTransferAmount(new BigDecimal(-1)); // Invalid transfer amount

        ObjectMapper mapper = new ObjectMapper();
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @When("I attempt to create internal transaction")
    public void iAttemptToCreateInternalTransaction() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/customer/internal",
                            HttpMethod.POST,
                            entity,
                            String.class);
        } catch (HttpClientErrorException e) {
            this.response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @Then("the creation of the internal transaction should fail")
    public void theCreationOfTheInternalTransactionShouldFail() {
        assertThat(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()).isTrue();
    }

    // EMPLOYEE CREATE TRANSACTION STEPS HERE

    @And("the employee transaction data is valid")
    public void theEmployeeTransactionDataIsValid() throws JsonProcessingException {
        EmployeeTransactionRequestDTO requestDTO = new EmployeeTransactionRequestDTO();
        requestDTO.setInitiatorUserId(3);
        requestDTO.setFromAccountIban("NL91ABNA0417164305");
        requestDTO.setToAccountIban("NL91ABNA0417164306");
        requestDTO.setTransferAmount(new BigDecimal(1));
        requestDTO.setDescription("testEmployeeTransfer");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + adminToken);
    }

    @When("an employee creates transaction")
    public void anEmployeeCreatesTransaction() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/employee",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    // FAIL TO EMPLOYEE CREATE TRANSACTION

    @And("the employee transaction data is invalid")
    public void theEmployeeTransactionDataIsInvalid() throws JsonProcessingException {
        EmployeeTransactionRequestDTO requestDTO = new EmployeeTransactionRequestDTO();
        requestDTO.setInitiatorUserId(-1); // Invalid employee ID
        requestDTO.setFromAccountIban("INVALID_IBAN"); // Invalid IBAN
        requestDTO.setToAccountIban("INVALID_IBAN"); // Invalid IBAN
        requestDTO.setTransferAmount(new BigDecimal(-1)); // Invalid transfer amount
        requestDTO.setDescription("testEmployeeTransfer");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + adminToken);
    }

    @When("an employee attempts to create transaction")
    public void anEmployeeAttemptsToCreateTransaction() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/employee",
                            HttpMethod.POST,
                            entity,
                            String.class);
        } catch (HttpClientErrorException e) {
            this.response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
