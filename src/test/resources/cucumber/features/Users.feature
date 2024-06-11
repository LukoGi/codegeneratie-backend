Feature: Users CRUD Operations

  Scenario: Retrieve all users as an Admin
    Given the endpoint for "users/" is available for method "GET"
    When I retrieve all users as an Admin
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