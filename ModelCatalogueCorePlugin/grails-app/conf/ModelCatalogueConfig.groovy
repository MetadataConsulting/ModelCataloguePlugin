import org.modelcatalogue.core.*
import org.modelcatalogue.core.security.User

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
    debug 'org.modelcatalogue.core.util.builder'
    debug 'org.modelcatalogue.core.publishing'

    warn 'org.modelcatalogue.core.xml'

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

grails.databinding.dateFormats = ['MMddyyyy', 'yyyy-MM-dd HH:mm:ss.S', "yyyy-MM-dd'T'hh:mm:sss'Z'"]

grails.mime.types = [
        json:          ['application/json', 'text/json'],
        xlsx:          ['application/vnd.ms-excel', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet']
]

grails.doc.images = new File("src/docs/images")
grails.doc.title = 'Model Catalogue Core Plugin' // The title of the documentation
grails.doc.subtitle = 'Documentation' // The subtitle of the documentation
grails.doc.authors = 'Adam Milward, Vladimír Oraný, David Milward'// The authors of the documentation
grails.doc.license = 'MIT'// The license of the software
grails.doc.copyright = ''// The copyright message to display
grails.doc.footer = ''// The footer to use


grails.assets.minifyJs = false

modelcatalogue.defaults.relationshiptypes =  [
        [name: "containment", sourceToDestination: "contains", destinationToSource: "contained in", sourceClass: Model, destinationClass: DataElement, metadataHints: "Min Occurs, Max Occurs", rule: '''
            String minOccursString = ext['Min Occurs']
            String maxOccursString = ext['Max Occurs']

            Integer minOccurs = minOccursString in ['unbounded', 'null', '*', null] ? 0 : (minOccursString as Integer)
            Integer maxOccurs = maxOccursString in ['unbounded', 'null', '*', null] ? Integer.MAX_VALUE : (maxOccursString as Integer)

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
        ''', versionSpecific: true],
        [name: 'base', sourceToDestination: 'is base for', destinationToSource: 'is based on', sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "source.class == destination.class"],
        [name: "attachment", sourceToDestination: "has attachment of", destinationToSource: "is attached to", sourceClass: CatalogueElement, destinationClass: Asset],
        [name: "hierarchy", sourceToDestination: "parent of", destinationToSource: "child of", sourceClass: Model, destinationClass: Model, versionSpecific: true],
        [name: "supersession", sourceToDestination: "superseded by", destinationToSource: "supersedes", sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "source.class == destination.class", system: true, versionSpecific: true],
        [name: "relatedTo", sourceToDestination: "related to", destinationToSource: "related to", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true],
        [name: "synonym", sourceToDestination: "is synonym for", destinationToSource: "is synonym for", sourceClass: CatalogueElement, destinationClass: CatalogueElement, bidirectional: true, rule: "source.class == destination.class"],
        [name: "favourite", sourceToDestination: "favourites", destinationToSource: "is favourite of", sourceClass: User, destinationClass: CatalogueElement, system: true],
        [name: "classification", sourceToDestination: "classifies", destinationToSource: "is classified by", sourceClass: Classification, destinationClass: CatalogueElement, versionSpecific: true],
        [name: "classificationFilter", sourceToDestination: "used as filter by", destinationToSource: "filtered by", sourceClass: Classification, destinationClass: User, system: true],
]

