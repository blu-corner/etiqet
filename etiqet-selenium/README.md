### Table of Contents

- [Introduction](#Introduction)
- [Overview](#Overview)
- [Selection](#Selection)
- [Navigation](#Navigation)
- [Interaction](#Interaction)
- [Validations](#Validations)
- [Explicit and Implicit wait](#Waits)
- [Browser Configuration](#Configuration)
- [Misc](#Misc)

## Introduction

The goal of this project is to make writing automated tests as seamlessly and user-friendly as possible -
With a catalogue of java step definitions, for testing with Cucumber and Selenium.

## Overview

This project uses a number of Cucumber step definitions to leverage Selenium
to make automated testing easier.
There are various step definitions to cover areas such as:
Navigation
Interaction
Validation
Selection
Etc...
A config file also allows the user to set-up the Selenium driver, with custom options before running tests.

## Selection

The 'selectedElement' variable is the foundation of this framework - Any actions that
are performed, are performed on the selected element - There are various ways to
select elements on a web-page to then be stored in 'selectedElement'.

```
  Scenario: Selecting via css
    Given I select element by css selector using value "#page-container"
    
  Scenario: Selecting via xpath
    Given I select element by xpath using value "//*[@id='page-container']"
```

If no element is selected, selection methods will search the entire DOM. If an element
is selected, the search methods will search relevant to the selected element. If you are
finished with the selected element and want to search from the top of the DOM you should
first clear the selected element.

There are also search methods to find multiple elements. The results of these searches
will be stored in the 'selectedElements' list - To interact with any of these elements, you
will need to select the element from the list first.

```
  Scenario: Selecting multiple elements by class name
    Given I select elements by class name using value "et-boc"

  Scenario: Selecting multiple elements by tag
    Given I select elements by tag using value "div"
```

A final container is the 'nameElements' map which stores elements that can be accessed via
an alias - These aliases can be created and added to names elements during selection.

```
  Scenario: Giving selected elements an alias then selecting using aliases
    Given I select element by id using value "page-container" as "my container"
      And I select the named element "my container"
```

They can also be created after the selection process.

```
  Scenario: Giving selected elements an alias then selecting using aliases
    Given I select element by id using value "page-container"
      And I select the named element "my container"
```

## Navigation

There are step definitions to cover navigation. The most common being visiting a url.

```
  Scenario: Opening url
    Given I go to the url "https://www.ultimateqa.com"
```

There are also navigation step definitions to go back a page, forward a page and to
refresh the page.

## Interaction

All interactions will be performed on the 'selectedElement', so it is important to select
the element first using a search method, and then interacting with the element. You can
also assign a selected element from the selected elements list or the named elements map.

There are a number of ways to interact with an element such as:

Clicking

```
  Scenario: Clicking elements
    Given I select element by tag using value "button"
      And I click on the selected element
```

Entering text

```
  Scenario: Inputting text
    Given I select element by tag using value "input"
    And I enter the text "my text"
```

Hovering over

```
  Scenario: Moving / hovering over
    Given I select element by id using value "et_search_icon"
      And I move to the selected element
```

## Validations

There are a number of ways to perform validations as part of your test - You can
check if an element contains specific text.

```
  Scenario: Check inner text
    Given I select element by xpath using value "//*[@id='et-boc']/div/div/div[7]/div[2]/div[2]/div[1]/h2"
    Then I check the selected element's inner text is equal to "Login"
    
  Scenario: Check inner text contains
    Given I select element by xpath using value "//*[@id='et-boc']/div/div/div[7]/div[2]/div[2]/div[1]/h2"
    Then I check the selected element's inner text contains "Log"
```

Check if the number of elements found is what you expect

```
  Scenario: Check number of elements found
    Given I select elements by class name using value "et_pb_button"
      And I check the number of elements found is 17
```

Check the values of attribute

```
  Scenario: Check attribute values
    Given I select element by id using value "main-header"
      And I check the element's attribute "id" is equal to "main-header"

  Scenario: Check attribute value contains
    Given I select element by id using value "main-header"
      And I check the selected element's attribute "id" contains "main"
```

Perform checks on the full web-page

```
  Scenario: Page does not contain
    Given I select element by id using value "main-header"
      And I check the page does not contain the text "nosuchword" with a timeout of 2 seconds
      
  Scenario: Check page contains
    Then I check the page contains the text "Skills"
```

## Waits

Implicit wait can be used simply by setting implicit wait, this will
be used for any step definitions.

```
  Scenario: Implicit waits
    Given I set implicit wait to 100 nanoseconds
    Given I set implicit wait to 100 milliseconds
    Given I set implicit wait to 100 seconds
```

Explicit waits can be set for the presence, visibility, or click-ability of
elements. 

```
  Scenario: Explicit waits
    Given I am waiting for elements to be present with a timeout of 10 seconds when selecting
    Given I am waiting for elements to be visible with a timeout of 10 seconds when selecting
    Given I am waiting for elements to be clickable with a timeout of 10 seconds when selecting
```

This will be set for all selection methods until you specify no explicit wait.

```
  Scenario: Clear explicit wait
    Given I am waiting for elements to be present with a timeout of 10 seconds when selecting
    Then no explicit wait
```

If explicit wait has been activated and multiple elements are being selected, it
will then attempt to wait for all elements (where possible).

Setting the explicit wait, as above, will apply only to selections methods - Waits that
return a boolean rather than 'WebElements' have their own step definitions that usually end with "a timeout of...".

```
  Scenario: Page does not contain
    Given I select element by id using value "main-header"
      And I check the page does not contain the text "nosuchword" with a timeout of 2 seconds
```

## Configuration

Some of the driver settings can be set using step definitions, however others
can be set using the config file browsers.xml. Unless a browser is specified when launching the browser,
a default browser will be selected, which has the following config:

```
    <Firefox name="default" driver_path="src/test/resources/drivers/geckodriver.exe">
        <headless>false</headless>
        <screenshot_on_exit>true</screenshot_on_exit>
    </Firefox>
```

Notice the name attribute. The names of all browsers defined in xml will be available
at runtime, to be selected in the following step definition:

```
  Scenario: Opening a specific browser
    Given the "default" browser is open
      And the window is maximized
```

The name "default" references the browser config you would like to use. Other customising
options that can be set are startup arguments, auto-close after scenarios, browser extensions
and more.

## Misc

Example '.feature' files can be found within resources/features.

The config defines whether a screenshot is taken before the tests finish - This may be useful
if auto-close is being used and you want a snapshot of what the web-page looked
like when the test had finished - You can also call the method directly to take
a screenshot at any step of the test.

```
  Background:
    Given I open the browser
      And go to the url "https://www.ultimateqa.com/complicated-page/"
```
