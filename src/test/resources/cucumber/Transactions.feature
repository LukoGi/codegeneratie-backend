Feature: Transactions CRUD Operations

  Scenario: Retrieve all transactions
    Given the endpoint for "transactions/all" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

  Scenario: Successfully create a transaction
    Given the endpoint for "transactions/create" is available for method "POST"
    And the transaction data is valid
    When I create a new transaction
    Then the transaction should be created successfully

  Scenario: Fail to create a transaction
    Given the endpoint for "transactions/create" is available for method "POST"
    And the transaction data is invalid
    When I create a new transaction with invalid data
    Then the creation of the transaction should fail

  Scenario: Successfully retrieve a transaction by ID
    Given the endpoint for "transactions/1" is available for method "GET"
    When I retrieve the transaction by ID 1
    Then I should receive the transaction details

  Scenario: Fail to retrieve a transaction by ID
    Given the endpoint for "transactions/999" is available for method "GET"
    When I retrieve the transaction by ID 999
    Then I should receive a transaction error message

  Scenario: Successfully update a transaction
    Given the endpoint for "transactions/1" is available for method "PUT"
    And the transaction data is valid
    When I update the transaction with ID 1
    Then the transaction should be updated successfully

  Scenario: Fail to update a transaction
    Given the endpoint for "transactions/999" is available for method "PUT"
    And the transaction data is valid
    When I update the transaction with ID 999
    Then the update of the transaction should fail

  Scenario: Get all transactions as an admin successfully
    Given I am an admin
    When I request to get all transactions
    Then I should receive a list of TransactionHistoryDTO objects

Scenario: Get transactions by account ID successfully
  Given I am a user with account ID 2
  When I request to get transactions by my account ID
  Then I should receive a list of TransactionHistoryDTO objects

  Scenario: Get transactions by user ID as an admin successfully
    Given I am an admin
    And there is a user with ID 1
    When I request to get transactions by the user ID 1
    Then I should receive a list of TransactionHistoryDTO objects

      Scenario: Fail to get transactions by non-existing account ID
        Given I am a user with account ID 9999
        When I request to get transactions by my account ID
        Then I should receive an error message

      Scenario: Fail to get transactions by non-existing user ID as an admin
        Given I am an admin
        And there is a user with ID 9999
        When I request to get transactions by the user ID 9999
        Then I should receive an error message