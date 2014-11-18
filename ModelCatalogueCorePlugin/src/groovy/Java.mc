classification name: 'Java', {
    // save some time finalizing the elements as they all are finalized by default
    status finalized

    // if not set explicitly, the data type with the same name and description is created for value domains
    automatic dataType

    // data types are missing the link to the XMLSchema, but the all have xs prefix so no name clash should happen
    globalSearchFor dataType

    valueDomain name: "String", description: "java.lang.String"
    valueDomain name: "Integer", description: "java.lang.Integer"
    valueDomain name: "Double", description: "java.lang.Double"
    valueDomain name: "Boolean", description: "java.lang.Boolean"
    valueDomain name: "Date", description: "java.util.Date"
    valueDomain name: "Time", description: "java.sql.Time"
    valueDomain name: "Currency", description: "java.util.Currency"
    valueDomain name: "Text", description: "a text field"
}
