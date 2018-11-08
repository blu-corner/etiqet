Feature: Creation of flows

  Scenario Outline: Send message through flow
    Given a "fix" client
      And "BME" order book is purged
      And filter out "Logon" message
    When client is logged on
    Then create flow "Replacer(<log>)->SeparatorNormaliser(^)->TagValueReplacer()" as "replacerFlow"
      And create flow "TagKeeper(8, 34, 52)" as "tagKeeper"
      And create flow "Merger(${replacerFlow}, ${tagKeeper})->Length()->Checksum()" as "mainFlow"
    Then send raw message through "mainFlow"
      And wait for a "Quote" message
    Examples:
      | log                                                                                                                                                |
      | 8=FIX.4.4^9=142^35=R^34=347^49=SENDER^52=20180906-14:51:17.371^56=TARGET^57=FXSpot^131=Q1536245389725^146=1^55=USDINR^303=2^537=2^38=1^15=EUR^40=D |
