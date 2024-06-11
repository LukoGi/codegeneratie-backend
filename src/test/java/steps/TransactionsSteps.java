package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import spring.group.spring.models.User;
import spring.group.spring.models.dto.bankaccounts.SetAbsoluteLimitRequestDTO;
import spring.group.spring.models.dto.transactions.*;
import spring.group.spring.models.dto.users.AcceptUserRequestDTO;
import spring.group.spring.models.dto.users.LoginRequestDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionsSteps extends BaseSteps {

    private final String adminToken = System.getenv("ADMIN_TOKEN");
    private final String userToken = System.getenv("USER_TOKEN");
    private final String janeUserToken = System.getenv("JANE_USER_TOKEN");

    @When("I retrieve all transactions")
    public void iRetrieveAllTransactions() {

    }

    @Then("I should receive all transactions")
    public void iShouldReceiveAllTransactions() {
    }

    @And("the transaction data is valid")
    public void theTransactionDataIsValid() {
    }

    @When("I create a new transaction")
    public void iCreateANewTransaction() {
    }

    @And("the transaction data is invalid")
    public void theTransactionDataIsInvalid() {
    }

    @When("I create a new transaction with invalid data")
    public void iCreateANewTransactionWithInvalidData() {
    }

    @And("the transaction ID {int} exists")
    public void theTransactionIDExists(int arg0) {
    }

    @When("I retrieve the transaction by ID {int}")
    public void iRetrieveTheTransactionByID(int arg0) {
    }

    @Then("I should receive the transaction details")
    public void iShouldReceiveTheTransactionDetails() {
    }

    @And("the transaction ID {int} does not exist")
    public void theTransactionIDDoesNotExist(int arg0) {
    }

    @Then("I should receive a transaction error message")
    public void iShouldReceiveATransactionErrorMessage() {
    }

    @When("I update the transaction with ID {int}")
    public void iUpdateTheTransactionWithID(int arg0) {
    }

    @Then("the transaction should be updated successfully")
    public void theTransactionShouldBeUpdatedSuccessfully() {
    }

    @Then("the update of the transaction should fail")
    public void theUpdateOfTheTransactionShouldFail() {
    }

    // CREATE TRANSACTION WITH IBAN STEPS HERE

    @And("the transaction IBAN data is valid")
    public void theTransactionIbanDataIsValid() throws JsonProcessingException {
        TransactionCreateFromIbanRequestDTO requestDTO = new TransactionCreateFromIbanRequestDTO();
        requestDTO.setInitiator_user_id(1);
        requestDTO.setTo_account_iban("NL91ABNA0417164306");
        requestDTO.setTransfer_amount(new BigDecimal(1));
        requestDTO.setDescription("test");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
    }

    @When("I create a new transaction with IBAN")
    public void iCreateANewTransactionWithIban() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/createWithIban",
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
        assertNotNull(responseDTO.getTransaction_id());
    }

    // TRANSFER FUNDS STEPS HERE

    @And("the transfer data is valid")
    public void theTransferDataIsValid() throws JsonProcessingException {
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setUserId(2);
        requestDTO.setFromAccountType("CHECKINGS");
        requestDTO.setToAccountType("SAVINGS");
        requestDTO.setTransferAmount(new BigDecimal(2));

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + janeUserToken);
    }

    @When("I transfer funds")
    public void iTransferFunds() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/transfer",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    @Then("the funds should be transferred successfully")
    public void theFundsShouldBeTransferredSuccessfully() {
        System.out.println("Response Body: " + response.getBody());
        Assertions.assertEquals(200, response.getStatusCode().value());

        TransactionResponseDTO responseDTO = null;
        try {
            responseDTO = mapper.readValue(response.getBody(), TransactionResponseDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getTransaction_id());
    }

    // EMPLOYEE TRANSFER FUNDS STEPS HERE

    @And("the employee transfer data is valid")
    public void theEmployeeTransferDataIsValid() throws JsonProcessingException {
        EmployeeTransferRequestDTO requestDTO = new EmployeeTransferRequestDTO();
        requestDTO.setEmployeeId(3);
        requestDTO.setFromAccountIban("NL91ABNA0417164305");
        requestDTO.setToAccountIban("NL91ABNA0417164306");
        requestDTO.setTransferAmount(new BigDecimal(1));
        requestDTO.setDescription("testEmployeeTransfer");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + adminToken);
    }

    @When("an employee transfers funds")
    public void anEmployeeTransfersFunds() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        this.response = restTemplate
                .exchange("/transactions/employeeTransfer",
                        HttpMethod.POST,
                        entity,
                        String.class);
    }

    // FAIL TO CREATE WITH INVALID IBAN

    @And("the transaction IBAN data is invalid")
    public void theTransactionIbanDataIsInvalid() throws JsonProcessingException {
        TransactionCreateFromIbanRequestDTO requestDTO = new TransactionCreateFromIbanRequestDTO();
        requestDTO.setInitiator_user_id(1);
        requestDTO.setTo_account_iban("INVALID_IBAN"); // Set an invalid IBAN
        requestDTO.setTransfer_amount(new BigDecimal(1));
        requestDTO.setDescription("test");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + userToken);
    }

    @When("I create a new transaction with invalid IBAN")
    public void iCreateANewTransactionWithInvalidIban() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/createWithIban",
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

    // FAIL TO TRANSFER FUNDS WITH INVALID DATA

    @And("the transfer data is invalid")
    public void theTransferDataIsInvalid() throws JsonProcessingException {
        TransferRequestDTO requestDTO = new TransferRequestDTO();
        requestDTO.setUserId(-1); // Invalid user ID
        requestDTO.setFromAccountType("INVALID"); // Invalid account type
        requestDTO.setToAccountType("INVALID"); // Invalid account type
        requestDTO.setTransferAmount(new BigDecimal(-1)); // Invalid transfer amount

        ObjectMapper mapper = new ObjectMapper();
        requestBody = mapper.writeValueAsString(requestDTO);
    }

    @When("I attempt to transfer funds")
    public void iAttemptToTransferFunds() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/transfer",
                            HttpMethod.POST,
                            entity,
                            String.class);
        } catch (HttpClientErrorException e) {
            this.response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    @Then("the transfer should fail")
    public void theTransferShouldFail() {
        assertThat(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()).isTrue();
    }

    // FAIL TO EMPLOYEE TRANSFER FUNDS

    @And("the employee transfer data is invalid")
    public void theEmployeeTransferDataIsInvalid() throws JsonProcessingException {
        EmployeeTransferRequestDTO requestDTO = new EmployeeTransferRequestDTO();
        requestDTO.setEmployeeId(-1); // Invalid employee ID
        requestDTO.setFromAccountIban("INVALID_IBAN"); // Invalid IBAN
        requestDTO.setToAccountIban("INVALID_IBAN"); // Invalid IBAN
        requestDTO.setTransferAmount(new BigDecimal(-1)); // Invalid transfer amount
        requestDTO.setDescription("testEmployeeTransfer");

        requestBody = mapper.writeValueAsString(requestDTO);

        httpHeaders.add("Authorization", "Bearer " + adminToken);
    }

    @When("an employee attempts to transfer funds")
    public void an_employee_attempts_to_transfer_funds() {
        HttpEntity<String> entity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            this.response = restTemplate
                    .exchange("/transactions/employeeTransfer",
                            HttpMethod.POST,
                            entity,
                            String.class);
        } catch (HttpClientErrorException e) {
            this.response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}
