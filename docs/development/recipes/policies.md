# Data Model Policies

Data Model Policies are constraints applied on data model to enforce
certain conventions across elements declared within given data model.

There are several forms how to declare a policy rule:

    check <domain> property <property name> is <checker name> otherwise <constraint violation text>
    check <domain> extension <extension name> is <checker name>
    check <domain> property <property name> apply <checker name>: <constraint configuration>

The domain is camel-cased name of one of the element types supported within data model such as `dataClass`.
You can use `every` as alias to `catalogueElement`.

Currently following checkers are available:

| Constraint Name | Description                                                                            | Required Configuration
|-----------------|----------------------------------------------------------------------------------------|--------------------------------
| `unique`        | There can be only one property or extension within the given value in the data model.  | _None_
| `required`      | The property or extension has to be present in instances of given domain.              | _None_
| `regex`         | The given property or extension as string must match the regular expression            | Regular Expression to match
| `negativeRegex` | The given property or extension as string must not match the regular expression        | Regular Expression not to match

Values can be injected to constraint violation texts:

| Number | Value
|--------|----------------------------------------------------------------------------------------
| `0`    | current data model
| `1`    | domain class
| `2`    | current item
| `3`    | name of the property
| `4`    | checker configuration


## Examples


    check dataElement property 'dataType' is 'required' otherwise 'Data type is missing for {2}'
    check dataElement property 'name' is 'unique' otherwise 'Data element\'s name is not unique for {2}'
    check dataType extension 'http://www.example.com#foobar' is 'unique' otherwise 'Data type\'s extension http://www.example.com#foobar is not unique for {2}'
    check every property 'name' apply regex: /[^_ -]+/ otherwise 'Name of {2} contains illegal characters ("_", "-" or " ")'

