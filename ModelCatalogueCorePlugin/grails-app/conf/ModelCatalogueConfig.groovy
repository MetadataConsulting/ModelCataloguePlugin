import org.modelcatalogue.core.*

// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}

grails.databinding.dateFormats = ['MMddyyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:sss'Z'"]

grails.mime.types = [
        json:          ['application/json', 'text/json'],
        xml:           ['text/xml', 'application/xml'],
        xlsx:          ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
]

grails.doc.images = new File("src/docs/images")
grails.doc.title = 'Model Catalogue Core Plugin' // The title of the documentation
grails.doc.subtitle = 'Documentation' // The subtitle of the documentation
grails.doc.authors = 'Adam Milward, Vladimír Oraný, David Milward'// The authors of the documentation
grails.doc.license = 'MIT'// The license of the software
grails.doc.copyright = ''// The copyright message to display
grails.doc.footer = ''// The footer to use


grails.assets.minifyJs = false

//configured data types, measurement units, relationship types

modelcatalogue.defaults.datatypes = [
        [name: "String", description: "java.lang.String"],
        [name: "Integer", description: "java.lang.Integer"],
        [name: "Double", description: "java.lang.Double"],
        [name: "Boolean", description: "java.lang.Boolean"],
        [name: "Date", description: "java.util.Date"],
        [name: "Time", description: "java.sql.Time"],
        [name: "Currency", description: "java.util.Currency"],
        [name: "Text", description: "a text field"],
        [name: "xs:string", description: "xml string"],
        [name: "xs:normalizedString", description: "A string that does not contain line feeds, carriage returns, or tabs"],
        [name: "xs:date", description: "Defines a date value"],
        [name: "xs:time", description: "Defines a time interval"],
        [name: "xs:dateTime", description: "Defines a date and time value"],
        [name: "xs:duration", description: "Defines a time interval"],
        [name: "xs:gDay", description: "Defines a part of a date - the day (DD)"],
        [name: "xs:gMonth", description: "Defines a part of a date - the month (MM)"],
        [name: "xs:gMonthDay", description: "Defines a part of a date - the month and day (MM-DD)"],
        [name: "xs:gYear", description: "Defines a part of a date - the year (YYYY)"],
        [name: "xs:gYearMonth", description: "Defines a part of a date - the year and month (YYYY-MM)"],
        [name: "xs:byte" , description: "A signed 8-bit integer"],
        [name: "xs:decimal" , description: "A decimal value"],
        [name: "xs:int", description: "A signed 32-bit integer"],
        [name: "xs:integer", description: "An integer value"],
        [name: "xs:long", description: "A signed 64-bit integer"],
        [name: "xs:boolean", description: "A signed 64-bit integer"],
        [name: "xs:float", description: "A float is single-precision 32-bit floating point value"],
        [name: "xs:double", description: "A double is single-precision 64-bit floating point value"],
        [name: "xs:hexBinary", description: "A hexBinary represents arbitrary hex-encoded binary data"],
        [name: "xs:base64Binary", description: "A base64Binary represents Base64-encoded arbitrary binary data"],
        [name: "xs:anyURI", description: "A anyURI represents a Uniform Resource Identifier Reference (URI) data"],
        [name: "xs:QName", description: "QName represents XML qualified names. The value space of QName is the set of tuples {namespace name, local part}, where namespace name is an anyURI and local part is an NCName."],
        [name: "xs:NOTATION", description: "NOTATION represents the NOTATION attribute type from [XML 1.0 (Second Edition)]. The value space of NOTATION is the set of QNames of notations declared in the current schema."],
        [name: "xs:token", description: " A token is the set of strings that do not contain the carriage return (#xD), line feed (#xA) nor tab (#x9) characters, that have no leading or trailing spaces (#x20) and that have no internal sequences of two or more spaces."],
        [name: "xs:nonPositiveInteger", description: "Defines a nonPositive integer"],
        [name: "xs:negativeInteger", description: "Defines a negative integer"],
        [name: "xs:short", description: "Defines an integer value between minInclusive -32768 to maxInclusive 32767"],
        [name: "xs:nonNegativeInteger", description: "Defines an integer value with minInclusive to 0"],
        [name: "xs:unsignedLong", description: "Defines an nonNegativeInteger value with maxInclusive to 18446744073709551615"],
        [name: "xs:unsignedInt", description: "Defines an unsignedLong value with maxInclusive to 4294967295"],
        [name: "xs:unsignedShort", description: "Defines an unsignedInt value with maxInclusive to 65535"],
        [name: "xs:unsignedByte", description: "Defines an unsignedShort value with maxInclusive to 255"],
        [name: "xs:positiveInteger", description: "Defines a nonNegativeInteger value with minInclusive to 1"]
]


modelcatalogue.defaults.measurementunits = [
        [name: "celsius", description: "degrees celsius", symbol: "°C"],
        [name: "fahrenheit", description: "degrees fahrenheit", symbol: "°F"],
        [name: "newtons", description: "measurement of force", symbol: "N"],
        [name:'meter',description:'length', symbol:'m'],
        [name:'kilogram',description:'mass', symbol:'kg'],
        [name:'second',description:'time', symbol:'s'],
        [name:'ampere',description:'electric current', symbol:'A'],
        [name:'kelvin',description:'thermodynamic temperature', symbol:'K'],
        [name:'mole',description:'amount of substance', symbol:'mol'],
        [name:'candela',description:'luminous intensity', symbol:'cd'],
        [name:'area',description:'square meter', symbol:'m2'],
        [name:'volume',description:'cubic meter', symbol:'m3'],
        [name:'speed, velocity',description:'meter per second', symbol:'m/s'],
        [name:'acceleration',description:'meter per second squared  ', symbol:'m/s2'],
        [name:'wave number',description:'reciprocal meter', symbol:'m-1'],
        [name:'mass density',description:'kilogram per cubic meter', symbol:'kg/m3'],
        [name:'specific volume',description:'cubic meter per kilogram', symbol:'m3/kg'],
        [name:'current density',description:'ampere per square meter', symbol:'A/m2'],
        [name:'magnetic field strength',description:'ampere per meter', symbol:'A/m'],
        [name:'amount-of-substance concentration',description:'mole per cubic meter', symbol:'mol/m3'],
        [name:'luminance',description:'candela per square meter', symbol:'cd/m2'],
        [name:'mass fraction',description:'kilogram per kilogram', symbol:'kg/kg = 1']
]


modelcatalogue.defaults.relationshiptypes =  [
        [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: Model, destinationClass: DataElement, metadataHints: "Min Occurs, Max Occurs", rule: '''

            Integer minOccurs = ext['Min Occurs'] as Integer
            Integer maxOccurs = ext['Max Occurs'] as Integer
            
            if (minOccurs != null) {
                if (minOccurs < 0) {
                    return false
                }
                if (maxOccurs != null && maxOccurs < minOccurs) {
                    return false
                }
            } else {
                if (maxOccurs != null && maxOccurs < 1) {
                    return false
                }
            }
            
            return true
        '''],
        [name: 'base', sourceToDestination: 'based on', destinationToSource: 'is base for', sourceClass: CatalogueElement, destinationClass: CatalogueElement],
        [name: "attachment", sourceToDestination: "has attachment of", destinationToSource: "is attached to", sourceClass: CatalogueElement, destinationClass: Asset],
        [name: "context", sourceToDestination: "provides context for", destinationToSource: "has context of", sourceClass: ConceptualDomain, destinationClass: Model],
        [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model],
        [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: PublishedElement, destinationClass: PublishedElement, rule: "source.class == destination.class", system: true],
        [name: "relatedTo", sourceToDestination: "related to", destinationToSource: "related to", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true],
        [name: "synonym", sourceToDestination: "is synonym for", destinationToSource: "is synonym for", sourceClass: DataElement, destinationClass: DataElement, bidirectional: true],
        [name: "union", sourceToDestination: "is union of", destinationToSource: "is united in", sourceClass: CatalogueElement, destinationClass: CatalogueElement]

]

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */

