package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator

import static org.modelcatalogue.core.util.test.FileOpener.open

/**
 * Created by rickrees on 18/04/2016.
 */
class RareDiseasePhenotypeChangeLogXlsExporterSpec extends AbstractRareDiseasesExporterSpec {


    def "Export rare disease phenotype changelogs to xls"() {
        when:
        buildTestModel(true)
        findDataIds()
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')
        makeChanges(dataClass)

        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")

        new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false).exportPhenotypes(dataClass, file.newOutputStream())

        open file

        then:
        noExceptionThrown()
    }

//    def "Export full rare disease changelogs to xls"() {
//        when:
//        DataClass dataClass = DataClass.findByName('Rare Disease Conditions, Phenotypes and Eligibility Criteria')
//
//        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
//
//        new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false).exportPhenotypes(dataClass, file.newOutputStream())
//
//        open file
//
//        then:
//        noExceptionThrown()
//    }

    def text = '''{"name":"Max Occurs","relationship":"{archived=false, classification=null, dataModel=null, destination={classifiedName=2nd New Phenotype DataElement (Test Data Model), dataType=null, dateCreated=2016-04-25T16:41:11Z, description=null, elementType=org.modelcatalogue.core.DataElement, ext={type=orderedMap, values=[]}, id=133, internalModelCatalogueId=http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataElement/133@0.0.1, lastUpdated=2016-04-25T16:41:12Z, latestVersionId=133, link=/dataElement/133, minimal=true, modelCatalogueId=null, name=2nd New Phenotype DataElement, status=DRAFT, versionCreated=2016-04-25T16:41:11Z, versionNumber=1}, elementType=org.modelcatalogue.core.Relationship, ext={type=orderedMap, values=[{key=Min Occurs, value=0}, {key=Max Occurs, value=2}]}, id=131, inherited=false, source={classifiedName=Phenotype (9) name 1 1 (Test Data Model), dataModel={classifiedName=Test Data Model, dateCreated=2016-04-25T16:40:59Z, description=This is a data model for testing Eligibility OR Phenotype and Clinicals tests exports, elementType=org.modelcatalogue.core.DataModel, ext={type=orderedMap, values=[{key=http://www.modelcatalogue.org/metadata/#organization, value=The Organisation}, {key=http://www.modelcatalogue.org/metadata/#reviewers, value=Reviewer One, Reviewer Two, Reviewer Three}, {key=http://www.modelcatalogue.org/metadata/#authors, value=Author One, Author Two, Author Three}, {key=http://www.modelcatalogue.org/metadata/#owner, value=The Owner}]}, id=1, internalModelCatalogueId=http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataModel/1@0.0.1, lastUpdated=2016-04-25T16:41:00Z, latestVersionId=1, link=/dataModel/1, minimal=true, modelCatalogueId=null, name=Test Data Model, semanticVersion=0.0.1, status=DRAFT, versionCreated=2016-04-25T16:40:58Z, versionNumber=1}, dateCreated=2016-04-25T16:41:01Z, description=null, elementType=org.modelcatalogue.core.DataClass, ext={type=orderedMap, values=[{key=OBO ID, value=HP:11}]}, id=22, internalModelCatalogueId=http://localhost:8080/ModelCatalogueCorePluginTestApp/catalogue/dataClass/22@0.0.1, lastUpdated=2016-04-25T16:41:12Z, latestVersionId=22, link=/dataClass/22, minimal=true, modelCatalogueId=null, name=Phenotype (9) name 1 1, status=DRAFT, versionCreated=2016-04-25T16:41:01Z, versionNumber=1}, type={id=1, link=/relationshipType/1, name=containment}}","extensionValue":"2"}'''

    def "test extractDeepElement Min Occurs"() {
        given:
        text
        when:
        String value = RareDiseasePhenotypeChangeLogXlsExporter.extractDeepElement(text, 'Min Occurs')

        then:
        '0' == value
    }

    def "test extractDeepElement Max Occurs"() {
        given:
        text
        when:
        String value = RareDiseasePhenotypeChangeLogXlsExporter.extractDeepElement(text, 'Max Occurs')

        then:
        '2' == value
    }

}
