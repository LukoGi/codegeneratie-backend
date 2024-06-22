Feature: Users CRUD Operations

  Scenario: Retrieve all users
    Given the endpoint for "users/" is available for method "GET"
    When I send a GET request to "/users/"
    Then I should receive all users

  Scenario: Successfully create a user
    Given the endpoint for "users/" is available for method "POST"
    When I send a POST request to "/users/"
    And the user data is valid
    Then the user should be created successfully

  Scenario: Fail to create a user
    Given the endpoint for "users/" is available for method "POST"
    When I send a POST request to "/users/"
    And the user data is invalid
    Then the creation of the user should fail

  Scenario: Successfully Retrieve a user by ID
    Given the endpoint for "users/1" is available for method "GET"
    When I send a GET request to "/users/1" with ID 1
    Then I should receive the user details of that user

  Scenario: Fail to retrieve a user by ID
    Given the endpoint for "users/52390" is available for method "GET"
    When I send a GET request to "/users/6655" with ID 6655
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
    When I send a POST request to "/users/login" with valid credentials
    Then I successfully login
    And I retrieve my user info

  Scenario: Try to login with invalid credentials
    Given the endpoint for "users/login" is available for method "POST"
    When I send a POST request to "/users/login" with invalid credentials
    Then I should receive an error message for login

  Scenario: Retrieve all unapproved users as admin
    Given the endpoint for "users/unapprovedUsers" is available for method "GET"
    When I send a GET request to "/users/unapprovedUsers" to retrieve all unapproved users
    Then I should receive all unapproved users

  Scenario: Retrieve all approved users without being admin
    Given the endpoint for "users/approvedUsers" is available for method "GET"
    When send a GET request to "/users/unapprovedUsers" to retrieve unapproved users without being a admin
    Then I should receive a user forbidden message

  Scenario: Successfully Approve a user
    Given the endpoint for "users/approve/2" is available for method "PUT"
    When I Send a PUT request to "/users/approve/2" to approve user with ID 2
    Then User should be approved

  Scenario: Try to approve a user without being admin
    Given the endpoint for "users/approve/2" is available for method "PUT"
    When I send a PUT request to "/users/approve/2" to approve user with ID 2 without being an admin
    Then I should receive a user forbidden message

  Scenario: Retrieve the bank accounts of a user
    Given the endpoint for "users/myAccounts" is available for method "GET"
    When I send a GET request to "/users/myAccounts" to retrieve the bank accounts of user
    Then I get a success message for retrieving the bank accounts

  Scenario: Try to retrieve the bank accounts of a user without being logged in
    Given the endpoint for "users/myAccounts" is available for method "GET"
    When I send a GET request to "/users/myAccounts" to retrieve the bank accounts of user without being logged in
    Then I should receive a user forbidden message