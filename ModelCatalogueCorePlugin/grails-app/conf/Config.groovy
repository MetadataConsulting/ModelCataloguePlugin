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

grails.mime.types = [
        json:          ['application/json', 'text/json'],
        xml:           ['text/xml', 'application/xml'],
        xlsx:          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
]

grails.doc.images = new File("src/docs/images")
grails.doc.title = 'Model Catalogue Core Plugin' // The title of the documentation
grails.doc.subtitle = 'Documentation' // The subtitle of the documentation
grails.doc.authors = 'Adam Milward, Vladimír Oraný, David Milward'// The authors of the documentation
grails.doc.license = 'MIT'// The license of the software
grails.doc.copyright = ''// The copyright message to display
grails.doc.footer = ''// The footer to use



//configured data types, measurement units, relationship types

modelcatalogue.defaults.datatypes = [
        [name: "String", description: "java.lang.String"],
        [name: "Integer", description: "java.lang.Integer"],
        [name: "Double", description: "java.lang.Double"],
        [name: "Boolean", description: "java.lang.Boolean"],
        [name: "Date", description: "java.util.Date"],
        [name: "Time", description: "java.sql.Time"],
        [name: "Currency", description: "java.util.Currency"]
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
        [name:'magnetic field strength  ',description:'ampere per meter', symbol:'A/m'],
        [name:'amount-of-substance concentration',description:'mole per cubic meter', symbol:'mol/m3'],
        [name:'luminance',description:'candela per square meter', symbol:'cd/m2'],
        [name:'mass fraction',description:'kilogram per kilogram', symbol:'kg/kg = 1']
]


modelcatalogue.defaults.relationshiptypes =  [
        [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: Model, destinationClass: DataElement],
        [name: "context", sourceToDestination: "provides context for", destinationToSource: "has context of", sourceClass: ConceptualDomain, destinationClass: Model],
        [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model],
        [name: "inclusion", sourceToDestination: "includes", destinationToSource: "included in", sourceClass: ConceptualDomain, destinationClass: ValueDomain],
        [name: "instantiation", sourceToDestination: "instantiated by", destinationToSource: "instantiates", sourceClass: DataElement, destinationClass: ValueDomain],
        [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: PublishedElement, destinationClass: PublishedElement, rule: "source.class == destination.class"]
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

