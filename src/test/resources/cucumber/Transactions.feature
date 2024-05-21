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
    Given the endpoint for "transactions/{id}" is available for method "GET"
    When I retrieve the transaction by ID 1
    Then I should receive the transaction details

  Scenario: Fail to retrieve a transaction by ID
    Given the endpoint for "transactions/{id}" is available for method "GET"
    When I retrieve the transaction by ID 1
    Then I should receive a transaction error message

  Scenario: Successfully update a transaction
    Given the endpoint for "transactions/{id}" is available for method "PUT"
    And the transaction data is valid
    When I update the transaction with ID 1
    Then the transaction should be updated successfully

  Scenario: Fail to update a transaction
    Given the endpoint for "transactions/{id}" is available for method "PUT"
    And the transaction data is valid
    When I update the transaction with ID 1
    Then the update of the transaction should fail