Feature: Bank Accounts CRUD Operations

  Scenario: Retrieve all bank accounts
    Given the endpoint for "accounts/" is available for method "GET"
    When I retrieve all bank accounts
    Then I should receive all bank accounts

  Scenario: Successfully create a bank account
    Given the endpoint for "accounts/" is available for method "POST"
    And the bank account data is valid
    When I create a new bank account
    Then the bank account should be created successfully

  Scenario: Fail to create a bank account
    Given the endpoint for "accounts/" is available for method "POST"
    And the bank account data is invalid
    When I create a new bank account with invalid data
    Then the creation of the bank account should fail

  Scenario: Fail to retrieve a bank account by ID as a user
    Given the endpoint for "accounts/1" is available for method "GET"
    When I retrieve the bank account by ID 1 as user
    Then I should receive a bank account forbidden message

  Scenario: Successfully retrieve a bank account by ID as an admin
    Given the endpoint for "accounts/1" is available for method "GET"
    When I retrieve the bank account by ID 1 as admin
    Then I should receive the bank account details

  Scenario: Fail to retrieve a bank account by ID
    Given the endpoint for "accounts/999" is available for method "GET"
    When I retrieve the bank account by ID 999 as admin
    Then I should receive a bank account error message

  Scenario: Successfully update a bank account as admin
    Given the endpoint for "accounts/1" is available for method "PUT"
    And the bank account data is valid
    When I update the bank account with ID 1 as admin
    Then the bank account should be updated successfully

  Scenario: Fail to update a bank account as user
    Given the endpoint for "accounts/1" is available for method "PUT"
    And the bank account data is valid
    When I update the bank account with ID 1 as user
    Then I should receive a bank account forbidden message

  Scenario: Fail to update a bank account with invalid data
    Given the endpoint for "accounts/1" is available for method "PUT"
    And the bank account data is invalid
    When I update the bank account with ID 1 as admin
    Then the update of the bank account should fail

  Scenario: Fail to update a bank account
    Given the endpoint for "accounts/999" is available for method "PUT"
    And the bank account data is valid
    When I update the bank account with ID 999 as admin
    Then the update of the bank account should fail

  Scenario: Successfully Get Bank Account with the user ID
    Given the endpoint for "accounts/user/1" is available for method "GET"
    When I retrieve the bank account by user ID 1 as admin
    Then I should receive a list of bank accounts

  Scenario: Fail to Get Bank Account with the user ID
    Given the endpoint for "accounts/user/999" is available for method "GET"
    When I retrieve the bank account by user ID 999 as admin
    Then I should receive a bank account error message

  Scenario: Successfully login to an atm bank account
    Given the endpoint for "accounts/login" is available for method "POST"
    And the atmlogin data is valid
    When I login to the bank account
    Then I should receive a login success message

  Scenario: Fail to login to an atm bank account
    Given the endpoint for "accounts/login" is available for method "POST"
    And the atmlogin data is wrong
    When I login to the bank account
    Then I should receive a login error message

    # todo:  withdraw deposit setabsolutelimit