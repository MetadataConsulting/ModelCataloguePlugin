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
//                      xml          : ['text/xml', 'application/xml'],
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
        grails.serverURL = "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
    local {
        grails.logging.jul.usebridge = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
    test {
        grails.plugin.console.enabled = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
    production {
        grails.logging.jul.usebridge = false
        grails.serverURL = System.getenv('METADATA_SERVER_URL') ?:  "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
}

hibernate {
    format_sql = true
    use_sql_comments = true
    generate_statistics = true
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    debug 'grails.app.services.org.modelcatalogue.core.ElementService'
    debug 'grails.app.services.org.modelcatalogue.core.dataarchitect.OBOService'
    debug 'grails.app.services.org.modelcatalogue.core.InitCatalogueService'
    debug 'org.modelcatalogue.core.dataarchitect.xsd.XSDImporter'

    debug 'org.modelcatalogue.core.util.builder'
    debug 'org.modelcatalogue.core.publishing'
    debug 'org.modelcatalogue.core.util.test'

//    debug 'org.codehaus.groovy.grails.web.mapping'
//    debug 'org.springframework.security'
//    debug 'org.grails.plugins.elasticsearch'

//    if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.CUSTOM) {
//        trace 'org.hibernate.type'
//        trace 'org.hibernate.stat'
//        debug 'org.hibernate.SQL'
//    }

    warn 'org.modelcatalogue.core'

    error 'org.codehaus.groovy.grails.web.servlet',           // controllers
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

modelcatalogue.defaults.relationshiptypes =  [
        [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: Model, destinationClass: DataElement, metadataHints: "Min Occurs, Max Occurs", rule: '''
            String minOccursString = ext['Min Occurs']
            String maxOccursString = ext['Max Occurs']

            Integer minOccurs = minOccursString in ['unbounded', 'null'] ? 0 : (minOccursString as Integer)
            Integer maxOccurs = maxOccursString in ['unbounded', 'null'] ? Integer.MAX_VALUE : (maxOccursString as Integer)

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
        ''', versionSpecific: true],
        [name: 'base', sourceToDestination: 'is base for', destinationToSource: 'is based on', sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "source.class == destination.class"],
        [name: "attachment", sourceToDestination: "has attachment of", destinationToSource: "is attached to", sourceClass: CatalogueElement, destinationClass: Asset],
        [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model, versionSpecific: true],
        [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "source.class == destination.class", system: true, versionSpecific: true],
        [name: "relatedTo", sourceToDestination: "related to", destinationToSource: "related to", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true],
        [name: "synonym", sourceToDestination: "is synonym for", destinationToSource: "is synonym for", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true, rule: "source.class == destination.class"],
        [name: "favourite", sourceToDestination: "favourites", destinationToSource: "is favourite of", sourceClass: User, destinationClass: CatalogueElement, system: true],
        [name: "classification", sourceToDestination: "classifies", destinationToSource: "classifications", sourceClass: Classification, destinationClass: CatalogueElement, versionSpecific: true],
        [name: "classificationFilter", sourceToDestination: "used as filter by", destinationToSource: "filtered by", sourceClass: Classification, destinationClass: User, system: true],

]

// configure the default storage
modelcatalogue.storage.directory = "/tmp/modelcatalogue/storage"
modelcatalogue.storage.maxSize = 50 * 1024 * 1024
// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.modelcatalogue.core.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.modelcatalogue.core.security.UserRole'
grails.plugin.springsecurity.authority.className = 'org.modelcatalogue.core.security.Role'
grails.plugin.springsecurity.requestMap.className = 'org.modelcatalogue.core.testapp.Requestmap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'


grails.assets.excludes =  [
        "bootstrap/**/*.*",
        "bootstrap/**/*.*",
        "font-awesome/**/*.*",
        "jquery/**/*.*",
        "angular/**/*.*",
        "angular-animate/**/*.*",
        "angular-bootstrap/**/*.*",
        "angular-cookies/**/*.*",
        "angular-i18n/**/*.*",
        "angular-mocks/**/*.*",
        "angular-sanitize/**/*.*",
        "jasmine/**/*.*",
        "**/*/GruntFile",
        "**/*/Gruntfile",
        "**/*/Gruntfile.coffee",
        "**/*/LICENSE",
        "**/*/COPYING",
        "**/*/README",
        "**/*/*.md",
        "**/*/*.json",
        "**/src/*.*",
        "**/test/*.*",
        "**/cpp/*.*",
        "**/csharp/*.*",
        "**/dart/*.*",
        "**/demos/*.*",
        "**/java/*.*",
        "**/lua/*.*",
        "**/maven/*.*",
        "**/objectivec/*.*",
        "**/python2/*.*",
        "**/python3/*.*",
]

grails.assets.plugin."model-catalogue-core-plugin".excludes = [
        "bootstrap/**/*.*",
        "bootstrap/**/*.*",
        "font-awesome/**/*.*",
        "jquery/**/*.*",
        "angular/**/*.*",
        "angular-animate/**/*.*",
        "angular-bootstrap/**/*.*",
        "angular-cookies/**/*.*",
        "angular-i18n/**/*.*",
        "angular-mocks/**/*.*",
        "angular-sanitize/**/*.*",
        "jasmine/**/*.*",
        "**/*/GruntFile",
        "**/*/Gruntfile",
        "**/*/Gruntfile.coffee",
        "**/*/LICENSE",
        "**/*/COPYING",
        "**/*/README",
        "**/*/*.md",
        "**/*/*.json",
        "**/src/*.*",
        "**/test/*.*",
        "**/cpp/*.*",
        "**/csharp/*.*",
        "**/dart/*.*",
        "**/demos/*.*",
        "**/java/*.*",
        "**/lua/*.*",
        "**/maven/*.*",
        "**/objectivec/*.*",
        "**/python2/*.*",
        "**/python3/*.*",
]

grails.assets.minifyOptions = [
        strictSemicolons: false,
        mangleOptions: [mangle: false, toplevel: false, defines: null, except: null, no_functions:false],
        genOptions: [indent_start:0, indent_level:4, quote_keys: false, space_colon: false, beautify: false, ascii_only: false, inline_script:false]
]

//grails.assets.bundle=false

grails.assets.minifyJs = true


grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "Model Catalogue"
grails.plugin.springsecurity.filterChain.chainMap = [
        '/catalogue/upload': 'JOINED_FILTERS,-exceptionTranslationFilter',
        '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
]
