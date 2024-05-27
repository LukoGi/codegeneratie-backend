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
    Given the endpoint for "users/1" is available for method "GET"
    When I retrieve the user by ID 1
    Then I should receive the user details

  Scenario: Fail to retrieve a user by ID
    Given the endpoint for "users/52390" is available for method "GET"
    When I retrieve the user by ID 52390
    Then I should receive an error message

  Scenario: Successfully update a user
    Given the endpoint for "users/updateUser/1" is available for method "PUT"
    And the user data is valid
    When I update the user with ID 1
    Then the user should be updated successfully

  Scenario: Fail to update a user
    Given the endpoint for "users/updateUser/52390" is available for method "PUT"
    And the user data is invalid
    When I update the user with ID 52390
    Then the update of the user should fail