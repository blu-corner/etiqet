Feature: Selecting

  Background:
    Given I open the browser
      And go to the url "https://www.ultimateqa.com/complicated-page/"

  Scenario: Selecting via css
    Given I select element by css selector using value "#page-container"

  Scenario: Selecting via id
    Given I select element by id using value "page-container"

  Scenario: Selecting via xpath
    Given I select element by xpath using value "//*[@id='page-container']"

  Scenario: Selecting via link text
    Given I select element by link text using value "About"

  Scenario: Selecting via partial link text
    Given I select element by partial link text using value "Ab"

  Scenario: Selecting multiple elements by class name
    Given I select elements by class name using value "et-boc"

  Scenario: Selecting multiple elements by tag
    Given I select elements by tag using value "div"

  Scenario: Selecting elements from multiple already selected elements
    Given I select elements by tag using value "div"
      And I select index 3 from selected elements

  Scenario: Giving selected elements an alias then selecting using aliases
    Given I select element by id using value "page-container" as "my container"
      And I select the named element "my container"
    Then I name the selected element as "copy of my container"

  Scenario: Clearing elements
    Given I select element by id using value "page-container" as "my container"
      And I select the named element "my container"
    Then I clear the named elements
      And I clear the selected element
    Then I select elements by class name using value "et-boc"
      And I clear the named elements
