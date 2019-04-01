Feature: Input

  Background:
    Given I open the browser
    And I go to the url "https://www.ultimateqa.com/complicated-page/"

  Scenario: Inputting text
    Given I select element by tag using value "input"
    And I enter the text "my text"