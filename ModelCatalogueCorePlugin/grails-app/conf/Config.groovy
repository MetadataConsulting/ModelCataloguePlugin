environments {
    development {
        grails.serverURL = "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
    test {
        grails.serverURL = "http://localhost:${System.getProperty('server.port') ?: 8080}/ModelCatalogueCorePluginTestApp"
    }
}


log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    debug 'grails.app.services.org.modelcatalogue.core.ElementService'
    debug 'grails.app.services.org.modelcatalogue.core.dataarchitect.OBOService'
    debug 'grails.app.services.org.modelcatalogue.core.InitCatalogueService'
    debug 'org.modelcatalogue.core.dataarchitect.xsd.XSDImporter'

    debug 'org.modelcatalogue.core.util.test'
    debug 'org.modelcatalogue.core.util.docx'

    // less verbose builder and publishing chains for tests as well
    info 'org.modelcatalogue.core.util.builder'
    info 'org.modelcatalogue.core.util.builder.DefaultCatalogueElementProxy'
    info 'org.modelcatalogue.core.publishing'

    warn 'org.modelcatalogue.core.xml'
    info 'org.modelcatalogue.core.RelationshipType'

//    trace 'org.hibernate.type'
//    trace 'org.hibernate.stat'
//    debug 'org.hibernate.SQL'

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
