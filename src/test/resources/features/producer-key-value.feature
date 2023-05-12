Feature: producer-key-value

  Background:
    Given input topic
      | topic        | alias    | key_type | value_type |
      | topic-string | topic_in | string   | string     |
    And output topic
      | topic        | alias     | key_type | value_type | readTimeoutInSecond |
      | topic-string | topic_out | string   | string     | 5                   |
    And var uuid = call function: uuid

  Scenario: Produce a record
    When records with key and value are sent
      | topic_alias | key              | value     |
      | topic_in    | aTestKey_${uuid} | someValue |
    Then expected records
      | topic_alias | key              | value  |
      | topic_out   | aTestKey_${uuid} | aValue |

    And assert aValue $ == "someValue"
