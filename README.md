# Secure Site Rules Engine Library

Secure Site rules engine is a library that implements a business-readable DSL for evaluating data and a set of
conditions that result in some action. Example of a business rule for the secure site is one that makes determination if
a user should be presented with a notification to enroll for text alerts, if they have a phone number that is SMS
enabled.

## Business Rules DSL

Below is a sample business rules JSON file written in Secure Site Rules Engine library's DSL. At root of every business
rules file is a collection of _'features'_ that represent the business decision to be made. Each _feature_ will have a
set of _requirements_ that will be evaluated and an optional set of _actions_ to be taken if the evaluation results in a
positive outcome. _requirements_ contain a set of _conditions_ to be evaluated. An optional set of _preConditions_ can
be defined that will be evaluated before the _conditions_ are evaluated.

```json
{
  "description": "rules for evaluating notifications",
  "features": [
    {
      "name": "sms_enrollment_notification",
      "description": "rules to evaluate of SMS enrollment notification should be displayed",
      "requirements": {
        "preConditions": [
          {
            "type": "data",
            "key": "notification-rules:sms-enrollment-notification.last-execution-date",
            "beyondDays": 7
          },
          {
            "type": "data",
            "key": "notification-rules:sms-enrollment-notification.status",
            "notEquals": "127"
          }
        ],
        "conditions": [
          {
            "type": "data",
            "key": "client-telephones:sms-enrollment-status",
            "notOneOf": [
              "ENROLLED"
            ]
          },
          {
            "type": "data",
            "key": "registration:enrollment.SCS_SITE.action-date",
            "withinDays": 180
          }
        ]
      },
      "actions": [
        {
          "type": "return",
          "key": "sms_enrollment_notification",
          "value": "Y",
          "defaultValue": "N"
        }
      ]
    }
  ]
}
```

The following table describes properties that can be used to define _conditions_ or _preConditions_

| Property | Description |
| --- | --- |
| type | Required. Should always be `data`. Indicates condition is evaluated using data that fetched by the rules engine. |
| key | Required. Identifier or name for the property in a `DataSet` that contains data-value to be used for evaluating the rule. |
| \<matcher\> | Required. An operator used to match/compare the data-value with a given value. | 

The following table describes all `matchers` that are currently implemented:

| Matcher | Data Type | Description |
| --- | --- | --- |
| equals | String | Performs case insensitive _equality_ comparison between the data-value and the given value |
| notEquals | String | Performance case insensitive _inequality_ comparison |
| oneOf | String Array | Checks if data-value is one of the given collection of strings |
| notOneOf | String Array | Inverse of `oneOf` |
| withinDays | Number | Checks if the date in data-value is within +/-N days from current date-time, where N is the number of days provided in the rule |
| beyondDays | Number | Checks if the date in data-value is outside +/-N days from current date-time, where N is the number of days provided in the rule |
| contains | String | Performs a substring match (case-sensitive) of the given value within the data-value|
| notContains | String | Inverse of `contains`|
