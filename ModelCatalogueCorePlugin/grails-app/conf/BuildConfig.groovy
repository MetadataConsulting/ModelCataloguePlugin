grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: false,
    // test: [maxMemory: 2048, minMemory: 1024, debug: false, maxPerm: 512, daemon:true],
    // configure settings for the run-app JVM
    run: false,
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

// XXX: triggers error in war deployment
//grails.tomcat.nio = true
//grails.tomcat.scan.enabled = true

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
        mavenCentral()
        mavenLocal()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo 'http://jcenter.bintray.com'
		mavenRepo "http://dl.bintray.com/metadata/model-catalogue"
        mavenRepo "http://dl.bintray.com/musketyr/document-builder"
        //mavenRepo "http://dl.dropbox.com/u/326301/repository"
		//mavenRepo "http://www.biojava.org/download/maven/"


    }
    dependencies {

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
        compile 'com.google.guava:guava:19.0'
        compile 'io.reactivex:rxjava:1.1.5'

        // does not work in tests
        // compile 'io.reactivex:rxgroovy:1.0.3'


        String mcToolkitVersion = '2.1.0.2'
        compile "org.modelcatalogue:mc-core-api:$mcToolkitVersion"
        compile "org.modelcatalogue:mc-builder-api:$mcToolkitVersion"
        compile "org.modelcatalogue:mc-datatype-validation:$mcToolkitVersion"

        compile 'com.craigburke.document:word:0.5.0'

        compile 'org.jsoup:jsoup:1.8.3'

        compile 'org.modelcatalogue:spreadsheet-builder-poi:0.3.0-rc4'

        compile 'org.apache.poi:poi:3.13'
        compile 'org.apache.poi:poi-ooxml:3.13'
        compile 'org.apache.poi:ooxml-schemas:1.1'

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

        compile group: 'org.elasticsearch', name: 'elasticsearch', version: '2.3.5'
        compile 'com.vividsolutions:jts:1.13'

        test 'xmlunit:xmlunit:1.6'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"
        test "org.modelcatalogue:mc-builder-xml:$mcToolkitVersion"

    }

    plugins {
        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }

        runtime ':database-migration:1.3.6'

        compile ":asset-pipeline:2.9.1"
        compile ":coffee-asset-pipeline:2.9.1"
        compile ":less-asset-pipeline:2.9.1"
        compile "org.grails.plugins:angular-annotate-asset-pipeline:2.4.0"
        compile "org.grails.plugins:babel-asset-pipeline:1.4.5"

        runtime ":angular-template-asset-pipeline:2.3.0"
//        runtime ":hibernate4:4.3.5.5"
        runtime  ":hibernate:3.6.10.18"

        compile ":excel-export:0.2.1"
        compile ":executor:0.3"

        compile ":karman-aws:0.8.4"

        compile ":csv:0.3.1"

        compile ":spring-websocket:1.3.1"

        test ':build-test-data:2.1.2'
        test ':fixtures:1.3'

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
    }
}


codenarc.reports = {
    MyXmlReport('xml') {                    // The report name "MyXmlReport" is user-defined; Report type is 'xml'
        outputFile = 'CodeNarc-Report.xml'  // Set the 'outputFile' property of the (XML) Report
        title = 'Sample Report'             // Set the 'title' property of the (XML) Report
    }
    MyHtmlReport('html') {                  // Report type is 'html'
        outputFile = 'CodeNarc-Report.html'
        title = 'Sample Report'
    }
}

// release
grails.project.repos.metadataSnapshots.url = "http://repository-metadata.forge.cloudbees.com/snapshot/"

grails.tomcat.nio = true
grails.tomcat.scan.enabled = true

