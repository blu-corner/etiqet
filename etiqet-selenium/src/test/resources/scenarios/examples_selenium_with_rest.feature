Feature: Demonstrating combined scenarios with selenium and rest

    Scenario Outline: PUT http request with rest validated with selenium
        Given a "rest" client
        When client is started
        Then "PUT" a "rest-example-message" with payload "id=1,title=<title>" from "posts/<id>" as "response"
        And check that "response" has status code "200"
        And check that "response" has "id=1"
        # Validation of the put request using selenium
        Then I open the browser
        And I go to the url "https://jsonplaceholder.typicode.com/posts/<id>"
        And I check the page contains the text "<title>" with a timeout of 5 seconds

        Examples:
            | id | title                                                                      |
            | 1  | sunt aut facere repellat provident occaecati excepturi optio reprehenderit |
