@Events
Feature: Events
  As a user
  I want to interact with events
  So that I might know what is going on

  Scenario Outline: Create a new event
    Given I am an authenticated user
    And I have an event
    And The event has a title with <number> characters
    When I submit the event
    Then I should receive a success response

    Examples:
      | number |
      | 255    |
      | 1      |
      | 25     |
      | 52     |
      | 250    |
      | 205    |

  Scenario Outline: Create an event without required data
    Given I am an authenticated user
    And I have an event
    And The event has a title with <number> characters
    When I submit the event
    Then I should receive an InvalidPayloadException

    Examples:
      | number |
      | 0      |
      | 256    |

  Scenario Outline: Create an event as an unauthenticated user
    Given I have an event
    And The event has a title with <number> characters
    When I submit the event
    Then I should receive an unauthenticated response

    Examples:
      | number |
      | 15     |

  Scenario: Get an event
    Given I am an authenticated user
    And An event exists
    When I get the event
    Then I should receive a success response
    And An event should be received

  Scenario: Get an event as an unauthenticated user
    Given An event exists
    When I get the event
    Then I should receive an unauthenticated response

  Scenario Outline: Update an existing event
    Given I am an authenticated user
    And An event exists
    And The event has a title with <number> characters
    When I submit the event for update
    Then I should receive a success response

    Examples:
    | number |
    | 10     |

  Scenario Outline: Update an existing event as an unauthenticated user
    Given An event exists
    And The event has a title with <number> characters
    When I submit the event for update
    Then I should receive an unauthenticated response

    Examples:
    | number |
    | 10     |

  Scenario: Delete an event
    Given I am an authenticated user
    And An event exists
    When I delete the event
    Then I should receive a success response
    And The event should be removed

  Scenario: Delete an event as an unauthenticated user
    Given An event exists
    When I delete the event
    Then I should receive an unauthenticated response

  Scenario: Get all events
    Given I am an authenticated user
    And An event exists
    When I get all events
    Then I should receive a success response

  Scenario: Get all events as an unauthenticated user
    Given An event exists
    When I get all events
    Then I should receive an unauthenticated response

  Scenario: Get the list of supporting instructors for an event
    Given I am an authenticated user
    And An event exists
    When I get the list of supporting instructors
    Then I should receive a success response

  Scenario: Get the list of supporting instructors for an event as an unauthenticated user
    Given An event exists
    When I get the list of supporting instructors
    Then I should receive an unauthenticated response

  Scenario: Get whether or not a user is a member for an event
    Given I am an authenticated user
    And An event exists
    When I get whether or not a user is a member of an event
    Then I should receive a success response

  Scenario: Get whether or not a user is a member for an event as an unauthenticated user
    Given An event exists
    When I get whether or not a user is a member of an event
    Then I should receive a success response

  Scenario: Get the list of checked in participants for an event
    Given I am an authenticated user
    And An event exists
    When I get the list of checked in participants for an event
    Then I should receive a success response

  Scenario: Get the list of checked in participants for an event as an unauthenticated user
    Given An event exists
    When I get the list of checked in participants for an event
    Then I should receive a success response

  Scenario: Get upcoming events
    Given I am an authenticated user
    And An event exists
    When I get the list of upcoming events
    Then I should receive a success response

  Scenario: Get upcoming events as an unauthenticated user
    Given An event exists
    When I get the list of upcoming events
    Then I should receive a success response

  Scenario: RSVP a user for an event
    Given I am an authenticated user
    And An event exists
    When I RSVP for an event
    Then I should receive a success response

  Scenario: RSVP a user for an event as an unauthenticated user
    Given An event exists
    When I RSVP for an event
    Then I should receive a success response

  Scenario: Register a user for an event
    Given I am an authenticated user
    And An event exists
    When I register for an event
    Then I should receive a success response

  Scenario: Register a user for an event as an unauthenticated user
    Given An event exists
    When I register for an event
    Then I should receive a success response

  Scenario: Unregister a user for an event
    Given I am an authenticated user
    And An event exists
    When I unregister for an event
    Then I should receive a success response

  Scenario: Unregister a user for an event as an unauthenticated user
    Given An event exists
    When I unregister for an event
    Then I should receive a success response

  Scenario: Get an event's checkin code
    Given I am an authenticated user
    And An event exists
    When I get an event's checkin code
    Then I should receive a success response

  Scenario: Get an event's checkin code as an unauthenticated user
    Given An event exists
    When I get an event's checkin code
    Then I should receive a success response

  Scenario: Checkin a user to an event
    Given I am an authenticated user
    And An event exists
    When I checkin a user to an event
    Then I should receive a success response

  Scenario: Checkin a user to an event as an unauthenticated user
    Given An event exists
    When I checkin a user to an event
    Then I should receive a success response

  Scenario: Start an event
    Given I am an authenticated user
    And An event exists
    When I start an event
    Then I should receive a success response

  Scenario: Start an event as an unauthenticated user
    Given An event exists
    When I start an event
    Then I should receive a success response

  Scenario: Complete an event
    Given I am an authenticated user
    And An event exists
    When I complete an event
    Then I should receive a success response

  Scenario: Complete an event as an unauthenticated user
    Given An event exists
    When I complete an event
    Then I should receive a success response

