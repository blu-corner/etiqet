Feature: Working with Grid Structures

    Scenario: Engaging with a table which has the typical html table structure
        Given I open the browser
        Given I go to the url "https://accessibility.umn.edu/web-designers/tables-web-pages"

        #Select the table
        When I select element by xpath using value "//div[@class='field-item even']//table[2]//tbody[1]"

        #Get an array list of the tables rows
        Then I select the children of selected element

        #Select the tables header row by a piece of text contained in it
        Then I select first element from elements by contained text "Season"

        #Break the header row down into it's constituent cells
        Then I select the children of selected element

        #Grab the column index for the column you want and save it to the index variable
        Then I get index for element amongst selected elements containing "Season"
        Then I clear the selected elements

        #Get an array list of the tables rows
        Then I select elements by tag using value "tr"

        #Select the row containing "Sweet Sixteen"
        Then I select first element from elements by contained text "Sweet Sixteen"

        #Get an array list of the cells within the currently selected table row
        Then I select the children of selected element

        #Select from current array list element located in position corresponding to column index
        Then I select element at current index from selected elements

        #Verify we have located the specific cell we wanted
        Then I check the selected element's inner text contains "Mid"
