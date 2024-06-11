Feature: Transactions CRUD Operations

  Scenario: Retrieve all transactions
    Given the endpoint for "transactions/all" is available for method "GET"
    When I retrieve all transactions
    Then I should receive all transactions

  Scenario: Successfully retrieve a transaction by ID
    Given the endpoint for "transactions/1" is available for method "GET"
    When I retrieve the transaction by ID 1
    Then I should receive the transaction details

  Scenario: Fail to retrieve a transaction by ID
    Given the endpoint for "transactions/999" is available for method "GET"
    When I retrieve the transaction by ID 999
    Then I should receive a transaction error message

  Scenario: Create transaction with IBAN
    Given the endpoint for "transactions/createWithIban" is available for method "POST"
    And the transaction IBAN data is valid
    When I create a new transaction with IBAN
    Then the transaction should be created successfully

  Scenario: Fail to create a transaction with invalid IBAN
    Given the endpoint for "transactions/createWithIban" is available for method "POST"
    And the transaction IBAN data is invalid
    When I create a new transaction with IBAN
    Then the creation of the transaction should fail

      Scenario: Get all transactions as an admin successfully
        Given the endpoint for "transactions/" is available for method "GET"
        When I request to get transactions by the user admin
        Then I should receive transactions

  Scenario: Get transactions as an user successfully
    Given the endpoint for "transactions/account/1" is available for method "GET"
    When I request to get transactions by the user
    Then I should receive my transactions

  Scenario: Get transactions as an user by id successfully
    Given the endpoint for "transactions/1" is available for method "GET"
    When I request to get transactions as admin of a user
    Then I should receive my transactions by id
    
  Scenario: Transfer funds
    Given the endpoint for "transactions/transfer" is available for method "POST"
    And the transfer data is valid
    When I transfer funds
    Then the funds should be transferred successfully

  Scenario: Fail to transfer funds with invalid data
    Given the endpoint for "transactions/transfer" is available for method "POST"
    And the transfer data is invalid
    When I attempt to transfer funds
    Then the transfer should fail

  Scenario: Employee transfer funds
    Given the endpoint for "transactions/employeeTransfer" is available for method "POST"
    And the employee transfer data is valid
    When an employee transfers funds
    Then the funds should be transferred successfully

  Scenario: Fail to transfer funds by employee with invalid data
    Given the endpoint for "transactions/employeeTransfer" is available for method "POST"
    And the employee transfer data is invalid
    When an employee attempts to transfer funds
    Then the transfer should fail
