grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.fork = [
    // configure settings for compilation JVM, note that if you alter the Groovy version forked compilation is required
    //  compile: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 256, daemon:true],

    // configure settings for the test-app JVM, uses the daemon by default
    test: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, daemon:true],
    // configure settings for the run-app JVM
    run: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the run-war JVM
    war: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256, forkReserve:false],
    // configure settings for the Console UI JVM
    console: [maxMemory: 768, minMemory: 64, debug: false, maxPerm: 256]
]

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
        mavenLocal()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
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

        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        // runtime 'mysql:mysql-connector-java:5.1.27'
    }

    plugins {
        build(":release:3.0.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }

        compile ':spring-security-core:2.0-RC4'
        compile ":spring-security-oauth:2.1.0-RC4"
        compile ':spring-security-oauth-google:0.1'
        compile ':spring-security-oauth-twitter:0.1'
        compile ':spring-security-oauth-facebook:0.1'
        // compile ':model-catalogue-core:0.9.1'
    }
}

grails.plugin.location.'model-catalogue-core' = "../ModelCatalogueCorePlugin"
