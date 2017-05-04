import grails.util.Metadata
import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Tag
import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.security.User

/**
 * Grails config file. Will look for mc-config.groovy for production mode in ~/.grails/.
 */

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// will be overriden by specific configuration but needs to exist at least as empty map
oauth.providers = [:]

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

grails.app.context = '/'

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
                      xsl          : 'text/xsl',
                      hal          : ['application/hal+json', 'application/hal+xml'],
//                      xml          : ['text/xml', 'application/xml'],
                      xlsx         : ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'],
                      all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.documentCache.maxsize = 1000

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
grails.exceptionresolver.params.exclude = ['password', 'password1', 'password2', 'client_secret']

// configure auto-caching of queries by default (if false you can documentCache individual queries with 'documentCache: true')
grails.hibernate.cache.queries = false

environments {
    development {
        grails.logging.jul.usebridge = true
        grails.serverURL = "http://localhost:${System.getProperty('server.port') ?: 8080}"
//        discourse {
//            url = "http://192.168.1.123/"
//            api {
//                key = "af9402ba45b8f4aff5a84bcdf6da85fc7548db746026c5095ed652d0f83fcd8b"
//                user = "discourse"
//            }
//            users {
//                fallbackEmail = 'vladimir.orany+:username@gmail.com'
//            }
//            sso {
//                key = System.getenv('METADATA_DISCOURSE_SSO_KEY') ?: "notasecret"
//            }
//        }
        oauth {
            providers {
                google {
                    // this key is limited to localhost only so no need to hide it
                    api = org.modelcatalogue.repack.org.scribe.builder.api.GoogleApi20
                    key = '225917730237-0hg6u55rgnld9cbtm949ab9h9fk5onr3.apps.googleusercontent.com'
                    secret = 'OG0JVVoy4bnGm48bneIS0haB'
                    successUri = '/oauth/google/success'
                    failureUri = '/oauth/google/error'
                    callback = "${grails.serverURL}/oauth/google/callback"
                    scope = 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email'
                }
            }
        }
        mc.allow.signup = true

        grails.plugin.console.enabled = true
        mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/es${System.currentTimeMillis()}"
        mc.css.custom = """
          /* green for dev mode to show it's safe to do any changes */
          .navbar-default {
            background-color: #c8e1c0;
            border-color: #bee2b2;
          }
        """
        mc.preload = [
                [name: "Java Basic Types", url: "https://s3-eu-west-1.amazonaws.com/datamodels.metadata.org.uk/Java.mc.xml"]
        ]
        grails.mail.disabled=true
    }
    local {
        grails.logging.jul.usebridge = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}"
    }
    test {
        // uncomment for debugging failing functional tests on Travis CI
        grails.assets.bundle=false
        grails.assets.minifyJs = false
        oauth {
            providers {
                google {
                    // this key is limited to localhost only so no need to hide it
                    api = org.modelcatalogue.repack.org.scribe.builder.api.GoogleApi20
                    key = '225917730237-0hg6u55rgnld9cbtm949ab9h9fk5onr3.apps.googleusercontent.com'
                    secret = 'OG0JVVoy4bnGm48bneIS0haB'
                    successUri = '/oauth/google/success'
                    failureUri = '/oauth/google/error'
                    callback = "${grails.serverURL}/oauth/google/callback"
                    scope = 'https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email'
                }
            }
        }
        mc.allow.signup = true

        grails.plugin.console.enabled = true
        grails.serverURL =  "http://localhost:${System.getProperty('server.port') ?: 8080}"
        if (System.getenv('DOCKERIZED_TESTS') && System.properties["grails.test.phase"] == 'functional') {
            mc.search.elasticsearch.host="localhost"
            mc.search.elasticsearch.port=49300
            // this must be set to be able to send any mails
            grails.mail.default.from = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.register.emailFrom = 'tester@metadata.org.uk'
            grails.plugin.springsecurity.ui.forgotPassword.emailFrom = 'tester@metadata.org.uk'

            grails {
                mail {
                    host = 'localhost'
                    port = 41025
                }
            }
        } else {
            mc.search.elasticsearch.local="${System.getProperty('java.io.tmpdir')}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/es${System.currentTimeMillis()}"
            grails.mail.disabled=true
        }
    }
    production {
        grails.logging.jul.usebridge = false

        // ---
        // you can overrides in your mc-config.groovy
        mc.sync.relationshipTypes=true
        grails.assets.minifyJs = true
        // configure the default storage
        mc.storage.directory = "/tmp/mc/storage"
        mc.storage.maxSize = 50 * 1024 * 1024
        // ---

        grails.assets.minifyOptions = [
                strictSemicolons: false,
                mangleOptions: [mangle: false, toplevel: false, defines: null, except: null, no_functions:false],
                genOptions: [indent_start:0, indent_level:4, quote_keys: false, space_colon: false, beautify: false, ascii_only: false, inline_script:false]
        ]


        if (System.properties["mc.config.location"]) {
            // for running
            // grails prod run-war -Dmc.config.location=my-conf.groovy
            grails.config.locations = ["file:" + System.properties["mc.config.location"]]
        } else {
            grails.config.locations = [ "classpath:mc-config.properties",
                                        "classpath:mc-config.groovy",
                                        "file:${userHome}/.grails/mc-config.properties",
                                        "file:${userHome}/.grails/mc-config.groovy"]
        }
        if (System.properties['catalina.base']) {
            def tomcatConfDir = new File("${System.properties['catalina.base']}/conf")
            if (tomcatConfDir.isDirectory()) {
                grails.config.locations = ["file:${tomcatConfDir.canonicalPath}/mc-config.groovy"]
            }
        }

    }
}

hibernate {
    format_sql = true
    use_sql_comments = true
    generate_statistics = true
}

// log4j configuration
log4j.main = {
    info 'grails.app.services.org.modelcatalogue'
    info 'grails.app.controllers.org.modelcatalogue'

    info 'org.modelcatalogue.core.dataarchitect.xsd.XSDImporter'

    // detailed feedback is now visible using the ProgressMonitor API
    info 'org.modelcatalogue.core.util.builder'
    info 'org.modelcatalogue.core.util.HibernateHelper' // for some reason the logging from builder is redirected here

    info 'org.modelcatalogue.core.publishing'
    info 'org.modelcatalogue.core.util.test'
    info 'org.modelcatalogue.core.gel'
    info 'org.modelcatalogue.core.export'
    info 'org.modelcatalogue.core.elasticsearch'
    info 'org.modelcatalogue.discourse'

    info 'grails.app.services.org.grails.plugins.console'
    info 'grails.app.services.org.modelcatalogue.core.elasticsearch'
    info 'org.grails.plugins.console'

    info 'org.modelcatalogue.core.rx.BatchOperator'
    info 'org.modelcatalogue.core.rx.DetachedCriteriaOnSubscribe'

//    debug 'org.codehaus.groovy.grails.web.mapping'
//    debug 'org.springframework.security'
//    debug 'org.grails.plugins.elasticsearch'

//    if (Environment.current == Environment.DEVELOPMENT || Environment.current == Environment.CUSTOM) {
//        trace 'org.hibernate.type'
//        trace 'org.hibernate.stat'
//        debug 'org.hibernate.SQL'
//    }

    info 'org.modelcatalogue'
    info 'grails.app.domain.org.modelcatalogue'

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

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.modelcatalogue.core.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.modelcatalogue.core.security.UserRole'
grails.plugin.springsecurity.authority.className = 'org.modelcatalogue.core.security.Role'
grails.plugin.springsecurity.requestMap.className = 'org.modelcatalogue.core.testapp.Requestmap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'

// this doesn't work properly, only reliable way is to his in setup-frontend.sh script
def assetExcludes = [
        "bootstrap/**/*.*",
        "jquery-ui/**/*.*",
        "font-awesome/**/*.*",
        "core.js/**/*.*",
        "jquery/**/*.*",
        "angular/**/*.*",
        "ace-builds/**/*.*",
        "rxjs/**/*.*",
        "angular-animate/**/*.*",
        "angular-rx/**/*.*",
        "angular-bootstrap/**/*.*",
        "angular-cookies/**/*.*",
        "angular-i18n/**/*.*",
        "angular-i18n/*.js",
        "angular-mocks/**/*.*",
        "angular-sanitize/**/*.*",
        "jasmine/**/*.*",
        "libs/**/*.*",
        "**/*/GruntFile",
        "**/*/GruntFile.js",
        "**/*/gulpfile.babel.js",
        "**/*/karma.conf.js",
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



grails.assets.excludes = assetExcludes

grails.assets.plugin.famfamfam.excludes = ['**/*.*']

grails.assets.babel.enabled = true
grails.assets.less.compiler = 'less4j'

grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "Model Catalogue"
grails.plugin.springsecurity.filterChain.chainMap = [
        '/catalogue/upload':                    'JOINED_FILTERS,-exceptionTranslationFilter',
        '/catalogue/*/*/export':                'JOINED_FILTERS,-exceptionTranslationFilter',
        '/catalogue/*/*/cytoscapeJsonExport':   'JOINED_FILTERS,-exceptionTranslationFilter',
        '/user/current':                        'JOINED_FILTERS,-exceptionTranslationFilter',
        '/api/modelCatalogue/core/feedback/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
        '/**':                                  'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
]
grails.plugin.springsecurity.logout.handlerNames = [
        'rememberMeServices',
        'securityContextLogoutHandler',
        'modelCatalogueSecurityService' // both spring security services implements it
]

// Added by the Spring Security OAuth plugin:
grails.plugin.springsecurity.oauth.domainClass = 'org.modelcatalogue.core.security.OAuthID'

if (!mc.allow.signup) {
    // for safety reasons, override the default class
    grails.plugin.springsecurity.oauth.registration.roleNames = ['ROLE_REGISTERED']
}

grails.plugin.springsecurity.ajaxCheckClosure = { request ->
    request.getHeader('accept')?.startsWith('application/json')
}

//language=HTML
mc.welcome.jumbo = """
<h1>Model Catalogue</h1>
<p class="lead">
    <b><em>Model</em></b> existing business processes and context. <b><em>Design</em></b> and version new datasets <b><em>Generate</em></b> better software components
</p>
"""

mc.welcome.info = """
<div class="col-sm-4">
<h2>Data Quality</h2>
<p>Build up datasets using existing data elements from existing datasets and add them to new data elements to compose new data models.</p>
<p>

</p>
</div>
<div class="col-sm-4">
<h2>Dataset Curation</h2>
<p>Link and compose data-sets to create uniquely identified and versioned "metadata-sets", thus ensuring preservation of data semantics between applications</p>
<p>

</p>
</div>
      <div class="col-sm-4">
<h2>Dataset Comparison</h2>
<p>Discover synonyms, hyponyms and duplicate data elements within datasets, and compare data elements from differing datasets.</p>
<p></p>
</div>
"""

grails.plugin.springsecurity.ui.register.defaultRoleNames = [] // no roles

grails.databinding.dateFormats = ['MMddyyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:sss'Z'"]


grails.doc.images = new File("src/docs/images")
grails.doc.title = 'Model Catalogue Core Plugin' // The title of the documentation
grails.doc.subtitle = 'Documentation' // The subtitle of the documentation
grails.doc.authors = 'Adam Milward, Vladimír Oraný, David Milward'// The authors of the documentation
grails.doc.license = 'MIT'// The license of the software
grails.doc.copyright = ''// The copyright message to display
grails.doc.footer = ''// The footer to use


grails.assets.minifyJs = false

modelcatalogue.defaults.relationshiptypes =  [
    [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: DataClass, destinationClass: DataElement, rule: '''
            String minOccursString = ext['Min Occurs']
            String maxOccursString = ext['Max Occurs']

            Integer minOccurs = minOccursString in ['unbounded', 'null', '*', null, ''] ? 0 : (minOccursString as Integer)
            Integer maxOccurs = maxOccursString in ['unbounded', 'null', '*', null, ''] ? Integer.MAX_VALUE : (maxOccursString as Integer)

            if (minOccurs < 0) {
                return ["relationshipType.containment.min.occurs.less.than.zero", "'Max Occurs' has to be greater than zero"]
            }
            if (maxOccurs < minOccurs) {
                return ["relationshipType.containment.min.occurs.greater.than.max.occurs", "The metadata 'Min Occurs' cannot be greater than 'Min Occurs'"]
            }
            if (maxOccurs < 1) {
                return ["relationshipType.containment.max.occurs.zero", "The metadata 'Max Occurs' must be greater than zero"]
            }

            return true
        ''', versionSpecific: true, sourceToDestinationDescription: "Model can contain multiple data elements. Contained data elements are finalized when the model is finalized.", destinationToSourceDescription: "Data element can be contained in multiple models. When new draft of the data element is created then drafts for all containing models are created as well."],
    [name: 'base', sourceToDestination: 'is based on', destinationToSource: 'is base for', sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "isSameClass()", versionSpecific: true, sourceToDestinationDescription: "Any catalogue element can be based on multiple elements of the same type. Value domains will first use rules of the base value domains and than their owns when validating input values.", destinationToSourceDescription: "Any catalogue element can be base for multiple elements of the same type."],
    [name: "attachment", sourceToDestination: "has attachment of", destinationToSource: "is attached to", sourceClass: CatalogueElement, destinationClass: Asset, sourceToDestinationDescription: "You can attach uploaded assets to any catalogue element.", destinationToSourceDescription: "Any uploaded asset can be attached to multiple catalogue element."],
    [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: DataClass, destinationClass: DataClass, versionSpecific: true, sourceToDestinationDescription: "Model can contain (be parent of) multiple models. Child models are finalized when parent model is finalized,", destinationToSourceDescription: "Model can be contained (be child model) in multiple models. When draft is created for child model drafts for parent models are created as well."],
    [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "isSameClass()", system: true, versionSpecific: true, sourceToDestinationDescription: "Any element can have multiple previous versions which are elements of the same type.", destinationToSourceDescription: "Any element can be previous version (supersede) multiple elements of the same type."],
    [name: "origin", sourceToDestination: "is origin for", destinationToSource: "is cloned from", sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "isSameClass()", system: true, versionSpecific: true, sourceToDestinationDescription: "Any element can be cloned from single element of the same type.", destinationToSourceDescription: "Any element can be origin for multiple cloned elements of the same type in different data models."],
    [name: "relatedTo", sourceToDestination: "related to", destinationToSource: "related to", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true, sourceToDestinationDescription: "Any element can be related to multiple elements. This relationship has no specific meaning."],
    [name: "synonym", sourceToDestination: "is synonym for", destinationToSource: "is synonym for", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true, rule: "isSameClass()", sourceToDestinationDescription: "Any element can be synonym of multiple elements of the same type having similar meaning."],
    [name: "favourite", sourceToDestination: "favourites", destinationToSource: "is favourite of", sourceClass: User, destinationClass: CatalogueElement, system: true, sourceToDestinationDescription: "User can favourite multiple elements which will be displayed at the Favourites page.", destinationToSourceDescription: "Any element can be favourited by multiple users and appear in their Favourites page.", searchable: true],
    [name: "import", sourceToDestination: "imports", destinationToSource: "is imported by", sourceClass: DataModel, destinationClass: DataModel, sourceToDestinationDescription: "Data Model has to import other Data Model if you want to reuse elements declared there.", destinationToSourceDescription: "Data Model can be imported by many other Data Models so they can reuse the catalogue elements defined within it."],
    [name: "declaration", sourceToDestination: "declares", destinationToSource: "declared within", sourceClass: DataModel, destinationClass: CatalogueElement, versionSpecific: true, system: true, sourceToDestinationDescription: "Data models can declare multiple elements. Based on this relationship you can narrow the elements shown in the catalogue using the data model filter in the bottom left corner. When data model is finalized all defined elements are finalized as well.", destinationToSourceDescription: "Any element can be declared within multiple data models. When new draft of the element is created then drafts for data models are created as well."],
    [name: "classificationFilter", sourceToDestination: "used as filter by", destinationToSource: "filtered by", sourceClass: DataModel, destinationClass: User, system: true, sourceToDestinationDescription: "Classification can be used as filter by multiple users. This is done using the classification filter in bottom left corner.", destinationToSourceDescription: "User can filter by multiple classifications. To use exclusion filter instead of inclusion, set metadata \$exclude to any non-null value."],
    [name: "ruleContext", destinationToSource: "provides context for", sourceToDestination: "applied within context", sourceClass: ValidationRule, destinationClass: DataClass, versionSpecific: true, destinationToSourceDescription: "Data class can provide context for multiple validation rules", sourceToDestinationDescription: "Validation rule is applied within context of data class."],
    [name: "involvedness", destinationToSource: "is involved in", sourceToDestination: "involves", sourceClass: ValidationRule, destinationClass: DataElement, versionSpecific: true, destinationToSourceDescription: "Data element can be involved in multiple validation rules", sourceToDestinationDescription: "Validation rule can involve multiple data elements"],
    [name: "tag", sourceToDestination: "tags", destinationToSource: "is tagged by", sourceClass: Tag, destinationClass: DataElement, versionSpecific: false, sourceToDestinationDescription: "Applies Tag on particular Data Element", destinationToSourceDescription: "Data Elements can be tagged by multiple Tags"],
]
