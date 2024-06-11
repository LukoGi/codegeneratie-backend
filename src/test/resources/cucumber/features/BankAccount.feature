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

  Scenario: Successfully withdraw money from a bank account
    Given the endpoint for "accounts/1/withdraw" is available for method "POST"
    And the withdraw data is valid
    When I withdraw money from bank account "1" account as John Doe
    Then I should receive a withdraw success message

  Scenario: Fail to withdraw money from a bank account as not owner of the account
    Given the endpoint for "accounts/2/withdraw" is available for method "POST"
    And the withdraw data is valid
    When I withdraw money from bank account "2" account as John Doe
    Then I should receive a bank account forbidden message

  Scenario: Fail to withdraw money from the bank account bc of absolute limit
    Given the endpoint for "accounts/1/withdraw" is available for method "POST"
    And the withdraw data is too much
    When I withdraw money from bank account "1" account as John Doe
    Then I should receive a absolute limit error message

  Scenario: Fail to withdraw money from the bank account bc of invalid data
    Given the endpoint for "accounts/1/withdraw" is available for method "POST"
    And the withdraw data is invalid
    When I withdraw money from bank account "1" account as John Doe
    Then I should receive a bank account error message

  Scenario: Successfully deposit money to a bank account
    Given the endpoint for "accounts/1/deposit" is available for method "POST"
    And the deposit data is valid
    When I deposit money to bank account "1" account as John Doe
    Then I should receive a deposit success message

  Scenario: Fail to deposit money to a bank account as not owner of the account
    Given the endpoint for "accounts/2/deposit" is available for method "POST"
    And the deposit data is valid
    When I deposit money to bank account "2" account as John Doe
    Then I should receive a bank account forbidden message

  Scenario: Fail to deposit money to the bank account bc of invalid data
    Given the endpoint for "accounts/1/deposit" is available for method "POST"
    And the deposit data is invalid
    When I deposit money to bank account "1" account as John Doe
    Then I should receive a bank account error message

  Scenario: Successfully set absolute limit to a bank account
    Given the endpoint for "accounts/1/setAbsoluteLimit" is available for method "PUT"
    And the setabsolutelimit data is valid
    When I set absolute limit to bank account "1" account as admin
    Then I should receive bank account success message

  Scenario: Fail to set absolute limit to a bank account as not admin
    Given the endpoint for "accounts/1/setAbsoluteLimit" is available for method "PUT"
    And the setabsolutelimit data is valid
    When I set absolute limit to bank account "1" account as John Doe
    Then I should receive a bank account forbidden message

  Scenario: Fail to set absolute limit to the bank account bc of invalid data
    Given the endpoint for "accounts/1/setAbsoluteLimit" is available for method "PUT"
    And the setabsolutelimit data is invalid
    When I set absolute limit to bank account "1" account as admin
    Then I should receive a bank account error message

    # Fix these scenarios
  Scenario: Successfully Get Ibans with a username
    Given the endpoint for "accounts/username/JohnDoe" is available for method "GET"
    When I retrieve the bank account by username "JohnDoe" as user
    #Then I should receive a list of bank accounts

  Scenario: Fail Get Ibans with a username
    Given the endpoint for "accounts/username/dsa" is available for method "GET"
    When I retrieve the bank account by username "dsa" as user
    #Then I should receive an error message

  Scenario: Deactivate a bank account
    Given the endpoint for "accounts/1/closeAccount" is available for method "PUT"
    When I change the is_active status of bank account "1" as admin to false
    Then I should receive bank account success message

