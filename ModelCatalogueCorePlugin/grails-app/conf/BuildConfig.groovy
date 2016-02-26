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
        compile 'com.google.guava:guava:18.0'
        compile 'io.reactivex:rxjava:1.0.15'

        // does not work in tests
        // compile 'io.reactivex:rxgroovy:1.0.3'


        String mcToolkitVersion = '2.0.0-alpha-04'
        compile "org.modelcatalogue:mc-core-api:$mcToolkitVersion"
        compile "org.modelcatalogue:mc-builder-api:$mcToolkitVersion"
        //compile "org.modelcatalogue:mc-integration-excel:$mcToolkitVersion"
        //compile "org.modelcatalogue:mc-integration-obo:$mcToolkitVersion"
        //compile "org.modelcatalogue:mc-integration-xml:$mcToolkitVersion"
        // compile "org.modelcatalogue:mc-integration-mc:$mcToolkitVersion"
        test "org.modelcatalogue:mc-builder-xml:$mcToolkitVersion"

        compile 'com.craigburke.document:word:0.4.10-fix31'

        compile 'org.jsoup:jsoup:1.8.3'

        compile 'org.modelcatalogue:spreadsheet-builder-poi:0.1.9'

        compile 'org.apache.poi:poi:3.13'
        compile 'org.apache.poi:poi-ooxml:3.13'
        compile 'org.apache.poi:ooxml-schemas:1.1'

        compile 'net.sourceforge.owlapi:owlapi-oboformat:3.5.1'
        compile 'net.sourceforge.owlapi:owlapi-api:3.5.1'
        compile 'net.sourceforge.owlapi:owlapi-parsers:3.5.1'

        test 'xmlunit:xmlunit:1.6'
        test "org.grails:grails-datastore-test-support:1.0.2-grails-2.4"

    }

    plugins {
        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }

        runtime ':database-migration:1.3.6'

        compile ":asset-pipeline:2.4.3"
        compile ":coffee-asset-pipeline:2.0.7"
        compile ":less-asset-pipeline:2.3.0"
        compile "org.grails.plugins:angular-annotate-asset-pipeline:2.4.0"
        runtime ":angular-template-asset-pipeline:2.2.6"
//        runtime ":hibernate4:4.3.5.5"
        runtime  ":hibernate:3.6.10.18"

        compile ":excel-export:0.2.1"
        compile ":executor:0.3"

        compile ":karman-aws:0.8.4"

        compile ":csv:0.3.1"

        compile ":spring-websocket:1.2.0"

        test ':build-test-data:2.1.2'
        test ':fixtures:1.3'

        // codenarc static analysis
        build ":codenarc:0.21"

        // test coverage
        test ":code-coverage:1.2.7"

        build ':tomcat:8.0.30'
		//jasper report generator see in dependencies

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

