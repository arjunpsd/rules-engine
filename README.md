# A simple Java based Rules Engine library

This is demo library that implements a business-readable DSL for evaluating data and a set of
conditions that result in some action. Example of a business rule is one that makes determination if
a user should be presented with a notification to enroll for mailing lists, if they happen to be a frequent visitor to a site.

## Business Rules DSL

Below is a sample business rules JSON file written in Rules Engine library's DSL. At root of every business
rules file is a collection of _'features'_ that represent the business decision to be made. Each _feature_ will have a
set of _requirements_ that will be evaluated and an optional set of _actions_ to be taken if the evaluation results in a
positive outcome. _requirements_ contain a set of _conditions_ to be evaluated. An optional set of _preConditions_ can
be defined that will be evaluated before the _conditions_ are evaluated.

```json
{
"description": "rules for evaluating notifications",
"features": [
	{
	"name": "mailing_list_notification",
	"description": "rules to evaluate if mailing signup notification should be displayed",
	"requirements": {
		"preConditions": [
		{
			"type": "data",
			"key": "notification-rules:mailing-list-notification.last-evaluation-date",
			"beyondDays": 7
		},
		{
			"type": "data",
			"key": "notification-rules:mailing-list-notification.status",
			"notEquals": "DISMISSED"
		}
		],
		"conditions": [
		{
			"type": "data",
			"key": "user-profile:user-profile.badge-level",
			"notOneOf": ["GOLD", "SILVER"]
		},
		{
			"type": "data",
			"key": "user-profile:registration-date",
			"withinDays": 180
		}
		]
	},
	"actions": [
		{
		"type": "return",
		"key": "mailing_list_enrollment_notification",
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
| key | Required. Identifier or name for the property in a `DataSet` that contains data-value to be used for evaluating the rule. Key is a _tuple_ that contains a data source name and a property name separated by ':'|
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
| after | Date | Checks if the fetched date is after the given date. Both dates must be in ISO8601 date-time format. |
| before | Date | Checks if the fetched date is before the given date. Both dates must be in ISO8601 date-time format. |

## Rules Engine API

### Executing Rules

The entry point for executing the rules engine is the `executeRules` method of `RulesEngine` class. The signature of the
method is as follows:

```java
public class RulesEngine {
	public CompletableFuture<List<RuleEvaluationResult>> executeRules(
			String ruleSetName, Map<?, ?> userData) {
	}
}
```

The first parameter is the name of the rule-set file containing the business rules (JSON), while the second parameter is
a map containing contextual data that may be used by `DataAdaptors` when fetching data for evaluating the rules.

Currently, rule-set file can only be loaded from classpath. The `ruleSetName` parameter should contain the name of the
rule-set json file (without .json extension) at the root of classpath. For example, the following statement would load
and execute rules in a file named `/user-notification-rules.json` at the root of current classpath

```java
rulesEngine.executeRules("user-notification-rules",userData)
		.thenAccept(evaluationResult->{
		//do something with the result
		});
```

### Data Source Adaptors

Evaluating rules generally requires external data to be fetched at runtime. Data Source Adaptors are java classes (
annotated as spring-beans) that are responsible for fetching the data from external sources and shaping them
into `DataSets` that can be easily consumed by the rules engine for evaluation of the rule. All Data Source Adaptors
must implement `DataSourceAdaptor` interface and its `fetch` method.

```java
public interface DataSourceAdaptor {
	CompletableFuture<DataSet> fetch(DataFetchingContext dfe);
}
```

A couple of sample data source adaptors are provided in the rules engine library source for reference. Additionally, you
may also use a utility class (`JsonPathDataSetMapper`) that can easily convert JSON data into `DataSet` objects using
_JsonPath_. See `UserProfileDataSourceAdaptor` class for usage.

### Mapping Parameters/Keys in Rules to Data Source adaptors

For every parameter/key referenced within a condition or pre-condition in the business rules file, there must be at
least one `DataSourceAdaptor` class annotated with the data source name (first part of the key separated by ':''). In
the following example for a key in business rule condition, 'user-profile' is the name of the data source adaptor
bean, while 'registration-date' is the data attribute that is fetched by the adaptor and returned within a `DataSet`

```json
{
"key": "user-profile:registration-date"
}
```

### Interpreting Results of Rule Evaluation

The `executeRules` method returns a `CompletableFuture`, which on completion, returns a collection
of `RuleEvaluationResult` for each rule that is evaluated. Note - A rule will not be evaluated if its pre-conditions are
not met, and therefore will not return a corresponding `RuleEvaluationResult`. In other words, the count of evaluation
result objects may not always match the count of rules in the rule-set file.

RuleEvaluationResult contains the following properties

| Property Name | Data Type | Description |
| --- | --- | --- |
| feature | String |Name of business feature that was evaluated. |
| returnValue | String | By default, contains `true` if the rule evaluation resulted in positive outcome and `false` if the result evaluation resulted in a negative outcome. The value can be overriden using an `Action` in the rules engine DSL|
| matched | List | Collection of evaluated conditions and the data-values that resulted in positive outcome (aka match). Will be empty if evaluation resulted in negative outcome. |

### Validating Business Rules

Business rules can be validated using tests that are also written using the same DSL. To run the tests, call
the `validateRules` method of `RulesValidationRunner` class, passing in the name of the business rules file to validate.
By convention, test file should also have the same name as the business rules file, but with suffix `-tests`

```java
	testRunner.validateRules("user-notification-rules");
```

Test files are also loaded using the same process as the business rules file under test (ie from classpath). For
reference, see sample test file - `notification-business-rules-tests.json`
