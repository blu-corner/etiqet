Feature: Explicit and implicit waits

  Scenario: Implicit waits
    Given I set implicit wait to 100 nanoseconds
    Given I set implicit wait to 100 milliseconds
    Given I set implicit wait to 100 seconds

  Scenario: Explicit waits
    Given I am waiting for elements to be present with a timeout of 10 seconds when selecting
    Given I am waiting for elements to be visible with a timeout of 10 seconds when selecting
    Given I am waiting for elements to be clickable with a timeout of 10 seconds when selecting

  Scenario: Clear explicit wait
    Given I am waiting for elements to be present with a timeout of 10 seconds when selecting
    Then explicit wait is disabled