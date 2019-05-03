Feature: Selecting
    
  Background: 
    Given I am waiting for elements to be present with a timeout of 5 seconds when selecting
      And I open the browser
      And go to the url "https://www.ultimateqa.com/complicated-page/"

  Scenario: Selecting via css
    Then I select element by css selector using value "#page-container"
      And I clear the selected element

  Scenario: Selecting via id
    Then I select element by id using value "page-container"
      And I clear the selected element

    Scenario: Selecting via xpath
    Then I select element by xpath using value "//*[@id='page-container']"
      And I clear the selected element

  Scenario: Selecting via link text
    Then I select element by link text using value "About"
      And I clear the selected element

  Scenario: Selecting via partial link text
    Then I select element by partial link text using value "Ab"
      And I clear the selected element

  Scenario: Selecting multiple elements by class name
    Then I select elements by class name using value "et-boc"
      And I clear the selected element

  Scenario: Selecting multiple elements by tag
    Then I select elements by tag using value "div"
      And I clear the selected element

    Scenario: Selecting elements from multiple already selected elements
    Then I select elements by tag using value "div"
      And I select index 3 from selected elements
      And I clear the selected element

  Scenario: Giving selected elements an alias then selecting using aliases
    Given I select element by id using value "page-container" as "my container"
      And I select the named element "my container"
    Then I name the selected element as "copy of my container"
      And I clear the selected element

  Scenario: Selecting elements relative to selected element
    Given I select element by id using value "page-container" as "my container"
    And I select the named element "my container"
    Then I name the selected element as "copy of my container"
    And I clear the selected element

  Scenario: Clearing elements
    Given I select element by id using value "page-container" as "my container"
      And I select the named element "my container"
    Then I clear the named elements
      And I clear the selected element
    Then I select elements by class name using value "et-boc"
      And I clear the named elements
