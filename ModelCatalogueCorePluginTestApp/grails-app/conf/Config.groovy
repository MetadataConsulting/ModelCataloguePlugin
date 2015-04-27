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

    debug 'grails.app.services.org.modelcatalogue'
    debug 'grails.app.controllers.org.modelcatalogue'

    debug 'org.modelcatalogue.core.dataarchitect.xsd.XSDImporter'

    debug 'org.modelcatalogue.core.util.builder'
    debug 'org.modelcatalogue.core.publishing'
    debug 'org.modelcatalogue.core.util.test'
    debug 'org.modelcatalogue.discourse'

//    debug 'org.codehaus.groovy.grails.web.mapping'
//    debug 'org.springframework.security'
//    debug 'org.grails.plugins.elasticsearch'

//    if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.CUSTOM) {
//        trace 'org.hibernate.type'
//        trace 'org.hibernate.stat'
//        debug 'org.hibernate.SQL'
//    }

    warn 'org.modelcatalogue'

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
        "jquery-ui/**/*.*",
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

grails.assets.plugin.famfamfam.excludes = ['**/*.*']

grails.assets.plugin."model-catalogue-core-plugin".excludes = [
        "bootstrap/**/*.*",
        "jquery-ui/**/*.*",
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
grails.plugin.springsecurity.logout.handlerNames = [
        'rememberMeServices',
        'securityContextLogoutHandler',
        'modelCatalogueSecurityService' // both spring security services implements it
]

discourse {
    url = "http://192.168.1.123/"
    api {
        key = "af9402ba45b8f4aff5a84bcdf6da85fc7548db746026c5095ed652d0f83fcd8b"
        user = "discourse"
    }
    users {
        fallbackEmail = 'vladimir.orany+:username@gmail.com'
    }
    sso {
        key = System.getenv('METADATA_DISCOURSE_SSO_KEY') ?: "notasecret"
    }
}