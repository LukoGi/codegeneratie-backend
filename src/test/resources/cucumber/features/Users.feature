Feature: Users CRUD Operations

  Scenario: Retrieve all users
    Given the endpoint for "users/" is available for method "GET"
    When I retrieve all users
    Then I should receive all users

  Scenario: Successfully create a user
    Given the endpoint for "users/" is available for method "POST"
    And the user data is valid
    When I create a new user
    Then the user should be created successfully

  Scenario: Fail to create a user
    Given the endpoint for "users/" is available for method "POST"
    And the user data is invalid
    When I create a new user with invalid data
    Then the creation of the user should fail

  Scenario: Successfully Retrieve a user by ID
    Given the endpoint for "users/1" is available for method "GET"
    When I retrieve the user by ID 1
    Then I should receive the user details

  Scenario: Fail to retrieve a user by ID
    Given the endpoint for "users/52390" is available for method "GET"
    When I retrieve the user by ID 52390
    Then I should receive an error message

  Scenario: Successfully update a user
    Given the endpoint for "users/1" is available for method "PUT"
    And the user data is valid
    When I update the user with ID 1
    Then the user should be updated successfully

  Scenario: Fail to update a user
    Given the endpoint for "users/52390" is available for method "PUT"
    And the user data is invalid
    When I update the user with ID 52390
    Then the update of the user should fail


  Scenario: Successfully set daily limit to a user
    Given the endpoint for "users/1/setDailyLimit" is available for method "PUT"
    And the setdailylimit data is valid
    When I set daily limit to user "1" as admin
    Then I should receive user success message

  Scenario: Fail to set daily limit to a user as not admin
    Given the endpoint for "users/1/setDailyLimit" is available for method "PUT"
    And the setdailylimit data is valid
    When I set daily limit to user "1" account as John Doe
    Then I should receive a user forbidden message

  Scenario: Fail to set daily limit to users bc of invalid data
    Given the endpoint for "users/1/setDailyLimit" is available for method "PUT"
    And the setdailylimit data is invalid
    When I set daily limit to user "1" as admin
    Then I should receive a user error message

  Scenario: Successfully login
    Given the endpoint for "users/login" is available for method "POST"
    And I retrieve my user info
    When I try to login with valid credentials
    Then I successfully login

  Scenario: Try to login with invalid credentials
    Given the endpoint for "users/login" is available for method "POST"
    When I try to login with invalid credentials
    Then I should receive an error message for login

  Scenario: Retrieve all unapproved users
    Given the endpoint for "users/getUnapprovedUsers" is available for method "GET"
    When I retrieve all unapproved users
    Then I should receive all unapproved users

  Scenario: Approve a user
    Given the endpoint for "users/acceptUser/1" is available for method "PUT"
    When I approve a user with ID "1" And set the limits
    Then I should receive user accepted message

  Scenario: Retrieve users without bank account
    Given the endpoint for "users/getUsersWithoutBankAccount" is available for method "GET"
    When I retrieve all users without bank accounts
    Then I should retrieve a succes message for retrieving all accounts

  Scenario: Retrieve the bank accounts of a user
    Given the endpoint for "users/myAccounts" is available for method "GET"
    When I retrieve the bank accounts of a user
    Then I get a success message for retrieving the bank accounts