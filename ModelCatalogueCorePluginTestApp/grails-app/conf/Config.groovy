import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElement

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
                      atom         : 'application/atom+xml',
                      css          : 'text/css',
                      csv          : 'text/csv',
                      form         : 'application/x-www-form-urlencoded',
                      html         : ['text/html', 'application/xhtml+xml'],
                      js           : 'text/javascript',
                      json         : ['application/json', 'text/json'],
                      multipartForm: 'multipart/form-data',
                      rss          : 'application/rss+xml',
                      text         : 'text/plain',
                      hal          : ['application/hal+json', 'application/hal+xml'],
                      xml          : ['text/xml', 'application/xml'],
                      xlsx         : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
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

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
    }
    test {
        grails.plugin.console.enabled = true
    }
    production {
        grails.logging.jul.usebridge = false
        grails.serverURL = "http://mcc-testapp.metadata.eu.cloudbees.net"
    }
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    debug 'grails.app.services.org.modelcatalogue.core.PublishedElementService'
//    debug 'org.codehaus.groovy.grails.web.mapping'
//    debug 'org.springframework.security'
//    debug 'org.grails.plugins.elasticsearch'

    error 'org.codehaus.groovy.grails.web.servlet',        // controllers
            'org.codehaus.groovy.grails.web.pages',          // GSP
            'org.codehaus.groovy.grails.web.sitemesh',       // layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping',        // URL mapping
            'org.codehaus.groovy.grails.commons',            // core / classloading
            'org.codehaus.groovy.grails.plugins',            // plugins
            'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'
}
grails.views.gsp.encoding = "UTF-8"

elasticSearch.client.mode = 'local'
elasticSearch.index.store.type = 'memory' // store local node in memory and not on disk
elasticSearch.datastoreImpl = 'hibernateDatastore'


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
        [name: 'meter', description: 'length', symbol: 'm'],
        [name: 'kilogram', description: 'mass', symbol: 'kg'],
        [name: 'second', description: 'time', symbol: 's'],
        [name: 'ampere', description: 'electric current', symbol: 'A'],
        [name: 'kelvin', description: 'thermodynamic temperature', symbol: 'K'],
        [name: 'mole', description: 'amount of substance', symbol: 'mol'],
        [name: 'candela', description: 'luminous intensity', symbol: 'cd'],
        [name: 'area', description: 'square meter', symbol: 'm2'],
        [name: 'volume', description: 'cubic meter', symbol: 'm3'],
        [name: 'speed, velocity', description: 'meter per second', symbol: 'm/s'],
        [name: 'acceleration', description: 'meter per second squared  ', symbol: 'm/s2'],
        [name: 'wave number', description: 'reciprocal meter', symbol: 'm-1'],
        [name: 'mass density', description: 'kilogram per cubic meter', symbol: 'kg/m3'],
        [name: 'specific volume', description: 'cubic meter per kilogram', symbol: 'm3/kg'],
        [name: 'current density', description: 'ampere per square meter', symbol: 'A/m2'],
        [name: 'magnetic field strength  ', description: 'ampere per meter', symbol: 'A/m'],
        [name: 'amount-of-substance concentration', description: 'mole per cubic meter', symbol: 'mol/m3'],
        [name: 'luminance', description: 'candela per square meter', symbol: 'cd/m2'],
        [name: 'mass fraction', description: 'kilogram per kilogram', symbol: 'kg/kg = 1']
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
        [name: 'base', sourceToDestination: 'is base for', destinationToSource: 'is based on', sourceClass: CatalogueElement, destinationClass: CatalogueElement],
        [name: "attachment", sourceToDestination: "has attachment of", destinationToSource: "is attached to", sourceClass: CatalogueElement, destinationClass: Asset],
        [name: "context", sourceToDestination: "provides context for", destinationToSource: "has context of", sourceClass: ConceptualDomain, destinationClass: Model],
        [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model],
        [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: PublishedElement, destinationClass: PublishedElement, rule: "source.class == destination.class", system: true],
        [name: "relatedTo", sourceToDestination: "related to", destinationToSource: "related to", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true],
        [name: "synonym", sourceToDestination: "is synonym for", destinationToSource: "is synonym for", sourceClass: DataElement, destinationClass: DataElement, bidirectional: true],
        [name: "union", sourceToDestination: "is union of", destinationToSource: "is united in", sourceClass: CatalogueElement, destinationClass: CatalogueElement]

]

// configure the default storage
modelcatalogue.storage.directory = "/tmp/modelcatalogue/storage"
modelcatalogue.storage.maxSize = 50 * 1024 * 1024
// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.modelcatalogue.core.testapp.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.modelcatalogue.core.testapp.UserRole'
grails.plugin.springsecurity.authority.className = 'org.modelcatalogue.core.testapp.Role'
grails.plugin.springsecurity.requestMap.className = 'org.modelcatalogue.core.testapp.Requestmap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'


grails.assets.excludes = ["bootstrap/**/*.less", "jquery/**/*.js", "angular/**/*.js"]

grails.assets.plugin."model-catalogue-core-plugin".excludes = ["bootstrap/**/*.less", "jquery/**/*.js", "angular/**/*.js"]
grails.assets.plugin."model-catalogue-core-plugin".includes = ["bootstrap.less"]

grails.assets.minifyOptions = [
        strictSemicolons: false,
        mangleOptions: [mangle: false, toplevel: false, defines: null, except: null, no_functions:false],
        genOptions: [indent_start:0, indent_level:4, quote_keys: false, space_colon: false, beautify: false, ascii_only: false, inline_script:false]
]

//grails.assets.bundle=false

grails.assets.minifyJs = true