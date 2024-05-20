Feature: Users CRUD Operations

  Scenario: Retrieve all users
    Given the endpoint for "users/all" is available for method "GET"
    When I retrieve all users
    Then I should receive all users

  Scenario: Successfully create a user
    Given the endpoint for "users/createUser" is available for method "POST"
    And the user data is valid
    When I create a new user
    Then the user should be created successfully

  Scenario: Fail to create a user
    Given the endpoint for "users/createUser" is available for method "POST"
    And the user data is invalid
    When I create a new user with invalid data
    Then the creation of the user should fail

  Scenario: Successfully Retrieve a user by ID
    Given the endpoint for "users/{id}" is available for method "GET"
    And the user ID 1 exists
    When I retrieve the user by ID
    Then I should receive the user details

  Scenario: Fail to retrieve a user by ID
    Given the endpoint for "users/{id}" is available for method "GET"
    And the user ID 1 does not exist
    When I retrieve the user by ID
    Then I should receive an error message

  Scenario: Successfully update a user
    Given the endpoint for "users/updateUser/{id}" is available for method "PUT"
    And the user ID 1 exists
    And the user data is valid
    When I update the user with ID 4
    Then the user should be updated successfully

  Scenario: Fail to update a user
    Given the endpoint for "users/updateUser/{id}" is available for method "PUT"
    And the user ID 1 does not exist
    And the user data is valid
    When I update the user with ID 4
    Then the update of the user should fail