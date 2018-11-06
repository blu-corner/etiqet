Feature: Creation of flows

  Scenario: Create a Checksum flow
    Given create flow "Checksum()" as "checksumFlow"

  Scenario: Create a Checksum flow and check it exists
    Given create flow "Checksum()" as "checksumFlow2"
    Then check flow "checksumFlow2" exists

  Scenario: Erase flow
    Given create flow "Checksum()" as "toBeCleaned"
    Then check flow "toBeCleaned" exists
    Then erase flow "toBeCleaned"
    And check flow "toBeCleaned" does not exist

  Scenario: Erase all flows
    Given create flow "Checksum()" as "toBeCleaned"
      And create flow "Length()" as "toBeCleanedToo"
    Then check flow "toBeCleaned" exists
    Then check flow "toBeCleanedToo" exists
    Then erase all flows
    And check flow "toBeCleaned" does not exist
    And check flow "toBeCleanedToo" does not exist

  Scenario: Check for a flow that does not exist
    When check flow "unexistentFlow" does not exist

  Scenario: Create flow and check on that does not exist
    Given create flow "Checksum()" as "checksumFlow3"
    Then check flow "unexistentFlow" does not exist

  Scenario: Create Length -> Checksum flow
    Given create flow "Length()->Checksum()" as "lengthChecksumFlow"
    Then check flow "lengthChecksumFlow" exists

  Scenario Outline: Merge two flows
    Then create flow "Replacer(<log>)->SeparatorNormaliser(^)->TagValueReplacer()" as "replacerFlow"
    And create flow "TagKeeper(8, 34, 52)" as "tagKeeper"
    And create flow "Merger(${replacerFlow}, ${tagKeeper})->Length()->Checksum()" as "mainFlow"
    And check flow "mainFlow" exists
    Examples:
      | log                                                                                                                                                |
      | 8=FIX.4.4^9=142^35=R^34=347^49=SENDER^52=20180906-14:51:17.371^56=TARGET^57=FXSpot^131=Q1536245389725^146=1^55=USDINR^303=2^537=2^38=1^15=EUR^40=D |
