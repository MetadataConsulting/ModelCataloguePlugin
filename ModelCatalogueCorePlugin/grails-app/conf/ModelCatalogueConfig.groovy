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
        [name: 'base', sourceToDestination: 'is base for', destinationToSource: 'is based on', sourceClass: CatalogueElement, destinationClass: CatalogueElement, rule: "isSameClass()", sourceToDestinationDescription: "Any catalogue element can be base for multiple elements of the same type.", destinationToSourceDescription: "Any catalogue element can be based on multiple elements of the same type. Value domains will first use rules of the base value domains and than their owns when validating input values."],
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
        [name: "ruleContext", sourceToDestination: "provides context for", destinationToSource: "applied within context", sourceClass: DataClass, destinationClass: ValidationRule, versionSpecific: true, sourceToDestinationDescription: "Data class can provide context for multiple validation rules", destinationToSourceDescription: "Validation rule is applied within context of data class."],
        [name: "involvedness", sourceToDestination: "is involved in", destinationToSource: "involves", sourceClass: DataElement, destinationClass: ValidationRule, versionSpecific: true, sourceToDestinationDescription: "Data element can be involved in multiple validation rules", destinationToSourceDescription: "Validation rule can involve multiple data elements"],
]

