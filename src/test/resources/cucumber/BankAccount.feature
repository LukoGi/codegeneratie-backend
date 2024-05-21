Feature: Bank Accounts CRUD Operations

  Scenario: Retrieve all bank accounts
    Given the endpoint for "accounts/all" is available for method "GET"
    When I retrieve all bank accounts
    Then I should receive all bank accounts

  Scenario: Successfully create a bank account
    Given the endpoint for "accounts/create" is available for method "POST"
    And the bank account data is valid
    When I create a new bank account
    Then the bank account should be created successfully

  Scenario: Fail to create a bank account
    Given the endpoint for "accounts/create" is available for method "POST"
    And the bank account data is invalid
    When I create a new bank account with invalid data
    Then the creation of the bank account should fail

  Scenario: Successfully retrieve a bank account by ID
    Given the endpoint for "accounts/{id}" is available for method "GET" with id 1
    When I retrieve the bank account by ID 1
    Then I should receive the bank account details

  Scenario: Fail to retrieve a bank account by ID
    Given the endpoint for "accounts/{id}" is available for method "GET" with id 999
    When I retrieve the bank account by ID 999
    Then I should receive a bank account error message

  Scenario: Successfully update a bank account
    Given the endpoint for "accounts/{id}" is available for method "PUT" with id 1
    And the bank account data is valid
    When I update the bank account with ID 1
    Then the bank account should be updated successfully

  Scenario: Fail to update a bank account
    Given the endpoint for "accounts/{id}" is available for method "PUT" with id 999
    And the bank account data is valid
    When I update the bank account with ID 999
    Then the update of the bank account should fail