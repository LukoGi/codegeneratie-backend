package Steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TransactionsSteps {
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
    public void iUpdateTheTransactionWithID(int arg0) {
    }

    @Then("the transaction should be updated successfully")
    public void theTransactionShouldBeUpdatedSuccessfully() {
    }

    @Then("the update of the transaction should fail")
    public void theUpdateOfTheTransactionShouldFail() {
    }
}