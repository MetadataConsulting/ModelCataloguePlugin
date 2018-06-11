 grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test   : false,
    // configure settings for the run-app JVM
//        run    : false, //[maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    run    : [maxMemory: 1536, minMemory: 128, debug: false, maxPerm: 512, forkReserve: false],
    // configure settings for the run-war JVM
    war    : [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve: false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

// XXX: triggers error on war deployment
//grails.tomcat.nio = true
//grails.tomcat.scan.enabled = true

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

    final String gebVersion = '1.0'
    final String seleniumVersion = '2.51.0'

    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false
    // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"

        mavenRepo "http://dl.bintray.com/metadata/model-catalogue"
        mavenRepo 'http://jcenter.bintray.com'
        mavenRepo "http://dl.bintray.com/musketyr/document-builder"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        compile 'com.google.guava:guava:19.0'
        compile 'io.reactivex:rxjava:1.1.5'

        // does not work in tests
        // compile 'io.reactivex:rxgroovy:1.0.3' test 123


        String mcToolkitVersion = '2.2.0'
        compile "org.modelcatalogue:mc-core-api:$mcToolkitVersion"
        compile "org.modelcatalogue:mc-builder-api:$mcToolkitVersion"
        //compile "org.modelcatalogue:mc-datatype-validation:$mcToolkitVersion"

        compile 'me.xdrop:fuzzywuzzy:1.1.7'

        //yet another pull request
        //another pull request
        //pull request
        //push request2
        //request3

        compile 'com.craigburke.document:word:0.5.0'

        compile 'org.jsoup:jsoup:1.8.3'

        compile 'org.modelcatalogue:spreadsheet-builder-poi:0.3.0-rc4'

        compile 'builders.dsl:spreadsheet-builder-poi:1.0.5'
        // for groovy support
        compile 'builders.dsl:spreadsheet-builder-groovy:1.0.5'

        compile 'org.apache.poi:poi:3.13'
        compile 'org.apache.poi:poi-ooxml:3.13'
        compile 'org.apache.poi:ooxml-schemas:1.1'
        compile 'org.apache.commons:commons-lang3:3.0'

        compile 'net.sourceforge.owlapi:owlapi-oboformat:3.5.1'
        compile 'net.sourceforge.owlapi:owlapi-api:3.5.1'
        compile 'net.sourceforge.owlapi:owlapi-parsers:3.5.1'

        compile 'org.gperfutils:gprof:0.3.1-groovy-2.4'

        String springSecurityVersion = '3.2.3.RELEASE'

        compile "org.springframework.security:spring-security-core:$springSecurityVersion", {
            excludes 'aopalliance', 'aspectjrt', 'cglib-nodep', 'commons-collections', 'commons-logging',
                'ehcache', 'fest-assert', 'hsqldb', 'jcl-over-slf4j', 'jsr250-api', 'junit',
                'logback-classic', 'mockito-core', 'powermock-api-mockito', 'powermock-api-support',
                'powermock-core', 'powermock-module-junit4', 'powermock-module-junit4-common',
                'powermock-reflect', 'spring-aop', 'spring-beans', 'spring-context', 'spring-core',
                'spring-expression', 'spring-jdbc', 'spring-test', 'spring-tx'
        }

        compile "org.springframework.security:spring-security-web:$springSecurityVersion", {
            excludes 'aopalliance', 'commons-codec', 'commons-logging', 'fest-assert', 'groovy', 'hsqldb',
                'jcl-over-slf4j', 'junit', 'logback-classic', 'mockito-core', 'powermock-api-mockito',
                'powermock-api-support', 'powermock-core', 'powermock-module-junit4',
                'powermock-module-junit4-common', 'powermock-reflect', 'spock-core', 'spring-beans',
                'spring-context', 'spring-core', 'spring-expression', 'spring-jdbc',
                'spring-security-core', 'spring-test', 'spring-tx', 'spring-web', 'spring-webmvc',
                'tomcat-servlet-api'
        }

        compile 'net.sf.opencsv:opencsv:2.3'

        compile group: 'org.elasticsearch', name: 'elasticsearch', version: '2.3.5'
        compile 'com.vividsolutions:jts:1.13'

        String crfBuilderVersion = '3.2.0-rc6'
        compile ("org.modelcatalogue:crf-builder-serializer:$crfBuilderVersion")
        compile ("org.modelcatalogue:crf-builder-builder:$crfBuilderVersion")
        compile ("org.modelcatalogue:crf-builder-preview:$crfBuilderVersion")

        test 'xmlunit:xmlunit:1.6'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
        test "org.modelcatalogue:mc-builder-xml:$mcToolkitVersion"



        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        // runtime 'org.postgresql:postgresql:9.3-1100-jdbc41'

        runtime "org.modelcatalogue:spring-security-ajax-aware:0.1.1"
        runtime 'mysql:mysql-connector-java:5.1.24'
        runtime "org.apache.httpcomponents:httpclient:4.3.1"

        // Testing modules
        test "org.gebish:geb-spock:$gebVersion"
        test "org.seleniumhq.selenium:selenium-chrome-driver:${seleniumVersion}"
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

        compile 'com.github.mpkorstanje:simmetrics-core:4.1.1'
    }

    plugins {
        build ':tomcat:8.0.33'

        // plugins for the compile step
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.8'
        compile ":console:1.5.6"
        compile ":spring-security-ui:1.0-RC2"
        compile ":grails-melody:1.59.0"

        // plugins needed at runtime but not for compilation
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"

        test ":geb:$gebVersion"
        test ':build-test-data:2.1.2'
        test ':fixtures:1.3'

        compile ":asset-pipeline:2.9.1"
        compile ":coffee-asset-pipeline:2.9.1"
        compile ":less-asset-pipeline:2.9.1"
        compile "org.grails.plugins:angular-annotate-asset-pipeline:2.4.0"
        compile "org.grails.plugins:babel-asset-pipeline:1.4.5"

        runtime ":angular-template-asset-pipeline:2.3.0"

        runtime ":hibernate4:4.3.10" // or ":hibernate:3.6.10.18"

        compile ":executor:0.3"

        compile ":karman-aws:0.8.4"

        compile ":spring-websocket:1.3.1"

        // codenarc static analysis
        build ":codenarc:0.21"

        // test coverage
        test ":code-coverage:1.2.7"

        build ':tomcat:8.0.33'

        compile ':spring-security-core:2.0.0'
        compile ":spring-security-oauth:2.1.0-RC4"
        compile ':spring-security-oauth-google:0.1'
        compile ':spring-security-oauth-twitter:0.1'
        compile ':spring-security-oauth-facebook:0.1'

        compile ':spring-security-acl:2.0.1'
        compile 'org.grails.plugins:rest-client-builder:2.1.1'
    }
}


