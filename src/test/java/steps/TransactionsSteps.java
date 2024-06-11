package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public class TransactionsSteps extends BaseSteps {

    private final String adminToken ="eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJBZG1pbiIsImF1dGgiOlsiUk9MRV9BRE1JTiJdLCJpYXQiOjE3MTc5MzExMTksImV4cCI6MTc0OTQ2NzExOX0.PE_4iw1798iiNdHta6v3FwT4ZBQ1ZDXQ0eH_yuTlDFlW8hGNwjg7MLNQ2Imj83iAANPJvv3vy0ItIZ6yHUhyxoQSAueqSI4KXk1EJoSb1Tecwf2CAJu3Z_Gj2QAKNB1h8WI0_Ly5MjOnRO9wIFWphYYI-iXT-NTD_9HCU-NQ_LqBzLv4uPQZKRbDYmNkEiqPjkCV6I0b8bdvGvKHiZDJe7OF9R4z2UYzSftfJxR6WXKFXfDHI28dlTBcl6-edi1_j1-V-LAoJBO4jRxycHMo1OFkaao4mp5euvriamii1GcKoufxkQeoFykDvgRTTn5D9zUltSJ7bpHwdWorHKkCzg";
    private final String userToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJKb2huRG9lIiwiYXV0aCI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNzE3OTMxMTgwLCJleHAiOjE3NDk0NjcxODB9.cyZzlQLnoiSee7TznaKa4nHHVcome7o2ZkI5_afzRjFH8bCL5SkIGv5rm5An7rI9XjtmX4TL6JfWkdw9fmq-VP3HEsm9yeAVS1toxXLS7n8kniDuunNzCgb12U8FDYu33fAt6TLL-GqmEog-88_ZTBZVtKl2NqmKUgcoCZRvkZAr4ZV_hZJudDRowPlSaCpj9Mu1ECdJx95cPK7aN4C6k8BjBXPw-NtcNNolqbSR4PYx8M_DCjCs0bmVmmsJX5BZ_dqGQ2JPoBr2xcuw5Sf-PsQnilA8oPmmbuX5r8HVd7pdfiZdWcpwaJCPbtpikKyAqcX3hrzWL8nO5XToH8HkxQ";

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

    @Then("the transaction should be created successfully")
    public void theTransactionShouldBeCreatedSuccessfully() {
    }

    @And("the transaction data is invalid")
    public void theTransactionDataIsInvalid() {
    }

    @When("I create a new transaction with invalid data")
    public void iCreateANewTransactionWithInvalidData() {
    }

    @Then("the creation of the transaction should fail")
    public void theCreationOfTheTransactionShouldFail() {
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
    public void iUpdateTheTransactionWithID(int transactionId) {
    }

    @Then("the transaction should be updated successfully")
    public void theTransactionShouldBeUpdatedSuccessfully() {
    }

    @Then("the update of the transaction should fail")
    public void theUpdateOfTheTransactionShouldFail() {
    }

    @Given("I am an admin")
    public void iAmAnAdmin() {
        
    }

    @When("I request to get all transactions")
    public void iRequestToGetAllTransactions() {
        
    }

    @Then("I should receive a list of TransactionHistoryDTO objects")
    public void iShouldReceiveAListOfTransactionHistoryDTOObjects() {
    }

    @Given("I am a user with account ID {int}")
    public void iAmAUserWithAccountID(int arg0) {
    }

    @When("I request to get transactions by my account ID")
    public void iRequestToGetTransactionsByMyAccountID() {
    }

    @And("there is a user with ID {int}")
    public void thereIsAUserWithID(int arg0) {
    }

    @When("I request to get transactions by the user ID {int}")
    public void iRequestToGetTransactionsByTheUserID(int arg0) {
    }

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
    public void iShouldReceiveTransactions() {
        Assertions.assertEquals(200, response.getStatusCode().value());
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
    public void iShouldReceiveMyTransactions() {
        Assertions.assertEquals(200, response.getStatusCode().value());
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
    public void iShouldReceiveMyTransactionsById() {
        Assertions.assertEquals(200, response.getStatusCode().value());
    }
    // new stuff

}
