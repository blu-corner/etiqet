Feature: Selecting elements relative to another

  Background:
    Given I choose the browser "firefox"
    And I go to url "https://www.google.com/"

  Scenario: select element relative to the selected element
      And I select element by id using value "lga"
    Then I select element by id using value "hplogo" relative to the selected element

  Scenario: select element relative to the selected element with an alias
      And I select element by id using value "lga"
    Then I select element by id using value "hplogo" relative to the selected element as "my_element"
