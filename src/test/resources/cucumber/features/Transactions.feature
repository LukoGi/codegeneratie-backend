Feature: Transactions CRUD Operations

  Scenario: Get transactions as an user successfully
    Given the endpoint for "transactions/account/1" is available for method "GET"
    When I request to get transactions by the user
    Then I should receive my transactions

  Scenario: Get all transactions as an admin successfully
    Given the endpoint for "transactions/" is available for method "GET"
    When I request to get transactions by the user admin
    Then I should receive transactions

  Scenario: Successfully Create transaction with valid IBAN as customer
    Given the endpoint for "transactions/customer" is available for method "POST"
    And the transaction IBAN data is valid
    When I create a new transaction as customer
    Then the transaction should be created successfully

  Scenario: Fail to create a transaction with invalid IBAN as customer
    Given the endpoint for "transactions/customer" is available for method "POST"
    And the transaction IBAN data is invalid
    When I create a new transaction with invalid IBAN as customer
    Then the creation of the transaction should fail

  Scenario: Create Internal Transaction
    Given the endpoint for "transactions/customer/internal" is available for method "POST"
    And the internal transaction data is valid
    When I create internal transaction
    Then the internal transaction should be created successfully

  Scenario: Fail to create internal transaction with invalid data
    Given the endpoint for "transactions/customer/internal" is available for method "POST"
    And the internal transaction data is invalid
    When I attempt to create internal transaction
    Then the creation of the transaction should fail

  Scenario: Employee create transaction
    Given the endpoint for "transactions/employee" is available for method "POST"
    And the employee transaction data is valid
    When an employee creates transaction
    Then the transaction should be created successfully

  Scenario: Fail to create transaction by employee with invalid data
    Given the endpoint for "transactions/employee" is available for method "POST"
    And the employee transaction data is invalid
    When an employee attempts to create transaction
    Then the creation of the transaction should fail
