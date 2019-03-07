Feature: Validations

  Background:
    Given I open the browser
      And I go to the url "https://www.ultimateqa.com/complicated-page/"
    
  Scenario: Check inner text
    Given I select element by xpath using value "//*[@id='et-boc']/div/div/div[7]/div[2]/div[2]/div[1]/h2"
    Then I check the selected element's inner text is equal to "Login"

  Scenario: Check inner text with explicit wait
    Given I select element by xpath using value "//*[@id='et-boc']/div/div/div[7]/div[2]/div[2]/div[1]/h2"
    Then I check the text "Login" is present in the selected element with a timeout of 5 seconds

  Scenario: Check any element's inner text
    Then I check that any element's inner text is equal to "Login" with a timeout of 5 seconds

  Scenario: Check any no element's inner text is equal to
    Then I check that no element's inner text is equal to "random inner text" with a timeout of 5 seconds

  Scenario: Check inner text contains
    Given I select element by xpath using value "//*[@id='et-boc']/div/div/div[7]/div[2]/div[2]/div[1]/h2"
    Then I check the selected element's inner text contains "Log"

  Scenario: Check page contains
    Then I check the page contains the text "Skills"
    
  Scenario: Check attribute values
    Given I select element by id using value "main-header"
      And I check the element's attribute "id" is equal to "main-header"

  Scenario: Check attribute value contains
    Given I select element by id using value "main-header"
      And I check the selected element's attribute "id" contains "main"

  Scenario: Page does not contain
    Given I select element by id using value "main-header"
      And I check the page does not contain the text "nosuchword" with a timeout of 2 seconds

  Scenario: Check page title
    Then I check the page title is "Complicated Page - Ultimate QA"

  Scenario: Check page title contains
    Then I check the page title contains "Ultimate"

  Scenario: Check current url
    Then I check the current url is "https://www.ultimateqa.com/complicated-page/"

  Scenario: Check current url contains
    Then I check the current url contains "ultimateqa"
    
  Scenario: Check number of elements found
    Given I select elements by class name using value "et_pb_button"
      And I check the number of elements found is 17

  Scenario: Check number of elements found is greater than
    Given I select elements by class name using value "et_pb_button"
      And I check the number of elements found is at least 2