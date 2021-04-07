Feature: Selecting elements relative to another

  Background:
    Given I choose the browser "firefox"
    And I go to url "https://www.ultimateqa.com/complicated-page/"

  Scenario: select element relative to the selected element
    And I select element by class name using value "lwptoc_i"
    Then I select element by class name using value "lwptoc_header" relative to the selected element

  Scenario: select element relative to the selected element with an alias
    And I select element by class name using value "lwptoc_i"
    Then I select element by class name using value "lwptoc_header" relative to the selected element as "my_element"
