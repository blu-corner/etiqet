Feature: Interacting

  Background: 
    Given I open the browser
      And I go to the url "https://www.ultimateqa.com/complicated-page/"

  Scenario: Clicking elements
    Given I select element by tag using value "button"
      And I click on the selected element

  Scenario: Right clicking
    Given I select element by id using value "et_search_icon"
    And I right click the selected element

  Scenario: Moving / hovering over
    Given I select element by id using value "et_search_icon"
      And I move to the selected element

  Scenario: Clicking and holding
    Given I select element by id using value "et_search_icon"
      And I click and hold element