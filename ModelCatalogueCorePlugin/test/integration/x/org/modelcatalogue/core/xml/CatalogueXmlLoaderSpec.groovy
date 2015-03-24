package x.org.modelcatalogue.core.xml

import grails.test.spock.IntegrationSpec
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.builder.CatalogueBuilder
import org.modelcatalogue.core.xml.CatalogueXmlLoader
import spock.lang.Shared

class CatalogueXmlLoaderSpec extends IntegrationSpec {

    CatalogueXmlLoader loader

    @Shared def initCatalogueService

    def classificationService
    def elementService

    def setupSpec() {
        initCatalogueService.initCatalogue(true)

        if (!RelationshipType.readByName('derivedFrom')) {
            new RelationshipType(
                    name: 'derivedFrom',
                    sourceClass: MeasurementUnit,
                    sourceToDestination: 'is derived from',
                    destinationClass: MeasurementUnit,
                    destinationToSource: 'derives'
            ).save(failOnError: true)
        }
    }

    def setup() {
        loader = new CatalogueXmlLoader(new CatalogueBuilder(classificationService, elementService))
    }

    def "read simple measurement unit"() {
        when:
        Set<CatalogueElement> loaded = load 'newton.catalogue.xml'

        then:
        loaded.size() == 1
        loaded.first() instanceof MeasurementUnit

        when:
        MeasurementUnit unit = loaded.first() as MeasurementUnit

        then:
        unit.name == 'Newton'
        unit.modelCatalogueId == 'http://www.example.com/units/Newton'
        unit.status == ElementStatus.DRAFT
        unit.symbol == 'N'
        unit.description == 'The newton (symbol: N) is the International System of Units (SI) derived unit of force.'
        unit.ext.From == 'SI'
        unit.countRelatedTo() == 1
        unit.relatedTo.first().modelCatalogueId == 'http://www.bipm.org/en/publications/si-brochure/kilogram.html'
        unit.outgoingRelationships.any { it.destination.modelCatalogueId == 'http://www.bipm.org/en/publications/si-brochure/metre.html' && it.relationshipType.name == 'derivedFrom' }
    }

    def "read simple data type"(){
        DataType decimal = new DataType(name: 'Decimal', modelCatalogueId: 'http://www.example.com/types/Decimal').save(failOnError: true)

        when:
        Set<CatalogueElement> loaded = load 'integer.catalogue.xml'

        then:
        loaded.size() == 1
        loaded.first() instanceof DataType

        when:
        DataType type = loaded.first() as DataType

        then:
        type.name == 'Integer'
        type.modelCatalogueId == 'http://www.example.com/types/Integer'
        type.status == ElementStatus.DRAFT
        type.description == 'A number with no fractional part.'
        type.countIsBasedOn() == 1
        type.isBasedOn.contains(decimal)
    }

    def "read enumerated data type"(){
        when:
        Set<CatalogueElement> loaded = load 'gender.catalogue.xml'

        then:
        loaded.size() == 1
        loaded.first() instanceof EnumeratedType

        when:
        EnumeratedType type = loaded.first() as EnumeratedType

        then:
        type.name == 'Gender'
        type.modelCatalogueId == 'http://www.example.com/types/Gender'
        type.status == ElementStatus.DRAFT
        type.description == 'The state of being male or female (typically used with reference to social and cultural differences rather than biological ones)'
        type.enumerations.size() == 2
        type.enumerations.F == 'Female'
        type.enumerations.M == 'Male'
    }

    def "read simple value domain"(){
        ValueDomain pressure = new ValueDomain(name: 'Pressure', modelCatalogueId: "http://www.example.com/domains/Pressure").save(failOnError: true)

        when:
        Set<CatalogueElement> loaded = load 'force.catalogue.xml'

        then:
        loaded.size() == 3
        loaded*.getClass() as Set == [ValueDomain, DataType, MeasurementUnit] as Set

        when:
        ValueDomain domain = loaded.find { it instanceof ValueDomain } as ValueDomain

        then:
        domain.name == 'Force'
        domain.modelCatalogueId == 'http://www.example.com/domains/Force'
        domain.status == ElementStatus.DRAFT
        domain.description == 'A force is a push or pull upon an object resulting from the object\'s interaction with another object.'
        domain.unitOfMeasure
        domain.unitOfMeasure.name == 'Newton'
        domain.dataType
        domain.dataType.name == 'Decimal'
        domain.regexDef == /\d+/
        domain.countRelatedTo() == 1
        domain.relatedTo.contains(pressure)
        domain.relatedToRelationships.find { it.destination.name == 'Pressure' }?.ext == [Relation: 'Derived From']
    }

    def "read simple data element"(){
        new ValueDomain(name: 'Pressure', modelCatalogueId: "http://www.example.com/domains/Pressure").save(failOnError: true)

        when:
        Set<CatalogueElement> loaded = load 'adhesion.catalogue.xml'

        then:
        loaded.size() == 4
        loaded*.getClass() as Set == [DataElement, ValueDomain, DataType, MeasurementUnit] as Set

        when:
        DataElement element = loaded.find { it instanceof DataElement } as DataElement

        then:
        element.name == 'Factor of Adhesion'
        element.modelCatalogueId == 'http://www.example.com/elements/Adhesion'
        element.status == ElementStatus.DRAFT
        element.description == null
        element.valueDomain
        element.valueDomain.name == 'Force'
    }

    def "read simple model"(){
        new ValueDomain(name: 'Pressure', modelCatalogueId: "http://www.example.com/domains/Pressure").save(failOnError: true)

        when:
        Set<CatalogueElement> loaded = load 'locomotive.catalogue.xml'

        then:
        loaded.size() == 6
        loaded*.getClass() as Set == [Model, DataElement, ValueDomain, DataType, MeasurementUnit] as Set

        when:
        Model model = loaded.find { it.name == 'Locomotive' } as Model

        then:
        model
        model.modelCatalogueId == 'http://www.example.com/models/Locomotive'
        model.status == ElementStatus.DRAFT
        model.description == null
        model.countContains() == 1
        model.contains.first().name == 'Factor of Adhesion'
        model.countParentOf() == 1
        model.parentOf.first().name == 'Engine'

        when:
        Model engine = loaded.find { it.name == 'Engine' } as Model

        then:
        engine.containsRelationships.find { Relationship rel -> rel.destination.name == 'Factor of Adhesion'}?.ext == ['Min. Occurs': '0']

    }

    def "read simple classification"(){
        new ValueDomain(name: 'Pressure', modelCatalogueId: "http://www.example.com/domains/Pressure").save(failOnError: true)

        when:
        Set<CatalogueElement> loaded = load 'transportation.catalogue.xml'

        then:
        loaded.size() == 7
        loaded*.getClass() as Set == [Classification, Model, DataElement, ValueDomain, DataType, MeasurementUnit] as Set

        when:
        Classification classification = loaded.find { it instanceof Classification } as Classification

        then:
        classification.name == 'Transportation'
        classification.modelCatalogueId == 'http://www.example.com/datasets/Transportation'
        classification.status == ElementStatus.DRAFT
        classification.description == null
        classification.countClassifies() == 6
    }




    Set<CatalogueElement> load(String sampleFile) {
        String location = "resources/xml/$sampleFile"
        InputStream input = getClass().classLoader.getResourceAsStream(location)
        if (!input) {
            throw new IllegalArgumentException("File ${getClass().getResource(location)} does not exist!")
        }
        loader.load(input)
    }

}
