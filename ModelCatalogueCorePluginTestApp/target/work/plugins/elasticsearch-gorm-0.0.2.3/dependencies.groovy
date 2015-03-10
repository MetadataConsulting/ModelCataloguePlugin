grails.project.work.dir = 'target'
grails.project.docs.output.dir = 'docs' // for the gh-pages branch

grails.project.dependency.distribution = {
    remoteRepository(id: 'snapshots-repo', url: 'http://noams.artifactoryonline.com/noams/grails-elasticsearch-plugin-snapshots/') {
        authentication username: System.getProperty('DEPLOYER_USERNAME'), password: System.getProperty('DEPLOYER_PASSWORD')
    }
    remoteRepository(id: 'rc-repo', url: 'http://noams.artifactoryonline.com/noams/grails-elasticsearch-plugin-rc/') {
        authentication username: System.getProperty('DEPLOYER_USERNAME'), password: System.getProperty('DEPLOYER_PASSWORD')
    }
}
grails.project.dependency.resolution = {

    pom true
    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        def excludes = {
            excludes 'slf4j-simple', 'persistence-api', 'commons-logging', 'jcl-over-slf4j', 'slf4j-api', 'jta'
            excludes 'spring-core', 'spring-beans', 'spring-aop', 'spring-asm', 'spring-webmvc', 'spring-tx', 'spring-context', 'spring-web', 'log4j', 'slf4j-log4j12'
            excludes group: 'org.grails', name: 'grails-core'
            excludes group: 'org.grails', name: 'grails-gorm'
            excludes group: 'org.grails', name: 'grails-test'
            excludes group: 'xml-apis', name: 'xml-apis'
            excludes 'ehcache-core'
            transitive = false
        }

        def datastoreVersion = '1.1.9.RELEASE'

        provided("org.grails:grails-datastore-gorm-plugin-support:$datastoreVersion",
                 "org.grails:grails-datastore-gorm:$datastoreVersion",
                 "org.grails:grails-datastore-core:$datastoreVersion",
                 "org.grails:grails-datastore-web:$datastoreVersion", excludes)

        runtime 'org.elasticsearch:elasticsearch:0.90.5'
        runtime('org.elasticsearch:elasticsearch-lang-groovy:1.5.0') {
            excludes 'junit'
            excludes 'elasticsearch'
            excludes 'groovy-all'
            excludes 'log4j'
        }
        runtime 'com.spatial4j:spatial4j:0.3'
    }

    plugins {
        build ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }

        test(':spock:0.7') {
            export = false
        }
    }
}
