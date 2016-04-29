package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.publishing.changelog.ChangeLogDocxGenerator

import static junit.framework.Assert.assertEquals
import static org.modelcatalogue.core.util.test.FileOpener.open

/**
 * Created by rickrees on 18/04/2016.
 */
class RareDiseasePhenotypeChangeLogXlsExporterSpec extends AbstractRareDiseasesExporterSpec {


    // content & file generation test combined as second one fails if run separately
    def "Export rare disease phenotype changelogs to xls"() {
        when:
        buildTestModel(true)
        findDataIds()
        DataClass dataClass = DataClass.findByName('Dataclass Top Level 1 Root')
        makeChanges(dataClass)

        //test content
        List<String> rows = new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false).buildContentRows(dataClass)

        //need to prime with header as we're not running the excel part of the exporter here
        String allRows = 'Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Phenotype /Clinical Tests/Guidance,Affected Data Item,Change Type,Current version details,New version details\n'

        rows.each{ row ->
            allRows+= "${row.join(',')}\n"
        }

//        println "EXPECT=$fullCSV"
//        println "GOT=$allRows"

        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.xlsx")
        //test excel generation
        new RareDiseasePhenotypeChangeLogXlsExporter(auditService, dataClassService, 5, false).exportPhenotypes(dataClass, file.newOutputStream())

        open file

        then:
        noExceptionThrown()
        assertEquals(fullCSV, allRows) //need to use junit assert as Spock blows up badly on failure (OutOfMem)
    }


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

    def fullCSV = '''Change reference,Level 2 Disease Group (ID),Level 3 Disease Subtype (ID),Level 4 Specific Disorder (ID),Phenotype /Clinical Tests/Guidance,Affected Data Item,Change Type,Current version details,New version details
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Disorder >1< Phenotypes Level5 Model 1 Data Element 1,Remove Data Item,Phenotype (2) name 1 1 (Test Data Model) (0.0.1),
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Disorder >1< Phenotypes Level5 Model 1 Data Element 1,New Data Item,,Disorder >1< Phenotypes Level5 Model 1 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (1) name 1 1,New Data Item,,Phenotype (1) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (1) name 1 1,New Metadata,,OBO ID: HP:3
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (3) name 1 1,New Data Item,,Phenotype (3) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (3) name 1 1,New Metadata,,OBO ID: HP:5
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (4) name 1 1,New Data Item,,Phenotype (4) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (4) name 1 1,New Metadata,,OBO ID: HP:6
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (5) changed name,New Data Item,,Phenotype (5) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (5) changed name,New Metadata,,OBO ID: HP:7
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (5) changed name,Name Change,Name: Phenotype (5) name 1 1,Name: Phenotype (5) changed name
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (6) name 1 1,New Data Item,,Phenotype (6) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (6) name 1 1,Change Metadata,OBO ID: HP:8,OBO ID: modified OBO ID
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (6) name 1 1,New Metadata,,OBO ID: HP:8
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (7) name 1 1,New Data Item,,Phenotype (7) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (7) name 1 1,New Metadata,,OBO ID: HP:9
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (7) name 1 1,Remove Metadata,OBO ID: HP:9,
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (8) name 1 1,New Data Item,,Phenotype (8) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (8) name 1 1,New Data Item,,New Phenotype DataElement (Test Data Model) ([, ])
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (8) name 1 1,New Metadata,,OBO ID: HP:10
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Data Item,,Phenotype (9) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Data Item,,2nd New Phenotype DataElement (Test Data Model) ([, ])
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Metadata,,Max Occurs: 3
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Metadata,,Min Occurs: 1
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Metadata,,OBO ID: HP:11
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Metadata,,Max Occurs: 2
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (9) name 1 1,New Metadata,,Min Occurs: 0
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (10) name 1 1,New Data Item,,Phenotype (10) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (10) name 1 1,New Metadata,,OBO ID: HP:12
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (11) name 1 1,New Data Item,,Phenotype (11) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (11) name 1 1,New Metadata,,OBO ID: HP:13
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (12) name 1 1,New Data Item,,Phenotype (12) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (12) name 1 1,New Metadata,,OBO ID: HP:14
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (13) name 1 1,New Data Item,,Phenotype (13) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (13) name 1 1,New Metadata,,OBO ID: HP:15
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (14) name 1 1,New Data Item,,Phenotype (14) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (14) name 1 1,New Metadata,,OBO ID: HP:16
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (15) name 1 1,New Data Item,,Phenotype (15) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Phenotype,Phenotype (15) name 1 1,New Metadata,,OBO ID: HP:17
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Disorder >1< Clinical tests Level5 Model 1 Data Element 1,New Data Item,,Disorder >1< Clinical tests Level5 Model 1 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Clinical tests (1) name 1 1,New Data Item,,Clinical tests (1) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Clinical tests (2) name 1 1,New Data Item,,Clinical tests (2) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Clinical tests (3) name 1 1,New Data Item,,Clinical tests (3) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Clinical tests (4) name 1 1,New Data Item,,Clinical tests (4) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Clinical tests,Clinical tests (5) name 1 1,New Data Item,,Clinical tests (5) name 1 1 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 1 (4@0.0.1),Disorder >>1<< heading Level4 Model Data Element 1 (5@0.0.1),Guidance,Disorder >1< Guidance name 1 1,Text change,Description: Guidance description  1 1,Description: new textual description replaces old
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Disorder >1< Phenotypes Level5 Model 1 Data Element 2,New Data Item,,Disorder >1< Phenotypes Level5 Model 1 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (1) name 1 2,New Data Item,,Phenotype (1) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (1) name 1 2,New Metadata,,OBO ID: HP:4
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (2) name 1 2,New Data Item,,Phenotype (2) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (2) name 1 2,New Metadata,,OBO ID: HP:5
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (3) name 1 2,New Data Item,,Phenotype (3) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (3) name 1 2,New Metadata,,OBO ID: HP:6
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (4) name 1 2,New Data Item,,Phenotype (4) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (4) name 1 2,New Metadata,,OBO ID: HP:7
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (5) name 1 2,New Data Item,,Phenotype (5) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (5) name 1 2,New Metadata,,OBO ID: HP:8
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (6) name 1 2,New Data Item,,Phenotype (6) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (6) name 1 2,New Metadata,,OBO ID: HP:9
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (7) name 1 2,New Data Item,,Phenotype (7) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (7) name 1 2,New Metadata,,OBO ID: HP:10
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (8) name 1 2,New Data Item,,Phenotype (8) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (8) name 1 2,New Metadata,,OBO ID: HP:11
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (9) name 1 2,New Data Item,,Phenotype (9) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (9) name 1 2,New Metadata,,OBO ID: HP:12
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (10) name 1 2,New Data Item,,Phenotype (10) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (10) name 1 2,New Metadata,,OBO ID: HP:13
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (11) name 1 2,New Data Item,,Phenotype (11) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (11) name 1 2,New Metadata,,OBO ID: HP:14
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (12) name 1 2,New Data Item,,Phenotype (12) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (12) name 1 2,New Metadata,,OBO ID: HP:15
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (13) name 1 2,New Data Item,,Phenotype (13) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (13) name 1 2,New Metadata,,OBO ID: HP:16
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (14) name 1 2,New Data Item,,Phenotype (14) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (14) name 1 2,New Metadata,,OBO ID: HP:17
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (15) name 1 2,New Data Item,,Phenotype (15) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Phenotype,Phenotype (15) name 1 2,New Metadata,,OBO ID: HP:18
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Disorder >1< Clinical tests Level5 Model 1 Data Element 2,New Data Item,,Disorder >1< Clinical tests Level5 Model 1 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (1) name 1 2,New Data Item,,Clinical tests (1) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (2) name 1 2,New Data Item,,Clinical tests (2) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (3) name 1 2,New Data Item,,Clinical tests (3) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (4) name 1 2,New Data Item,,Clinical tests (4) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (5) name 1 2,New Data Item,,Clinical tests (5) name 1 2 (Test Data Model) (0.0.1)
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Clinical tests,Clinical tests (5) name 1 2,Change Description,,Description: description for Clinical tests (5) name 1 2  has been changed
,Disorder >>>1<<< Level2 (3@0.0.1),Disorder >>>1<<< SubCondition Level3 Model Data Element 2 (36@0.0.1),Disorder >>1<< heading Level4 Model Data Element 2 (37@0.0.1),Guidance,New Guidance class,Text change,,Description: brand new description
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Disorder >2< Phenotypes Level5 Model 2 Data Element 1,New Data Item,,Disorder >2< Phenotypes Level5 Model 2 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (1) name 2 1,New Data Item,,Phenotype (1) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (1) name 2 1,New Metadata,,OBO ID: HP:4
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (2) name 2 1,New Data Item,,Phenotype (2) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (2) name 2 1,New Metadata,,OBO ID: HP:5
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (3) name 2 1,New Data Item,,Phenotype (3) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (3) name 2 1,New Metadata,,OBO ID: HP:6
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (4) name 2 1,New Data Item,,Phenotype (4) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (4) name 2 1,New Metadata,,OBO ID: HP:7
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (5) name 2 1,New Data Item,,Phenotype (5) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (5) name 2 1,New Metadata,,OBO ID: HP:8
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (6) name 2 1,New Data Item,,Phenotype (6) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (6) name 2 1,New Metadata,,OBO ID: HP:9
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (7) name 2 1,New Data Item,,Phenotype (7) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (7) name 2 1,New Metadata,,OBO ID: HP:10
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (8) name 2 1,New Data Item,,Phenotype (8) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (8) name 2 1,New Metadata,,OBO ID: HP:11
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (9) name 2 1,New Data Item,,Phenotype (9) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (9) name 2 1,New Metadata,,OBO ID: HP:12
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (10) name 2 1,New Data Item,,Phenotype (10) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (10) name 2 1,New Metadata,,OBO ID: HP:13
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (11) name 2 1,New Data Item,,Phenotype (11) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (11) name 2 1,New Metadata,,OBO ID: HP:14
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (12) name 2 1,New Data Item,,Phenotype (12) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (12) name 2 1,New Metadata,,OBO ID: HP:15
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (13) name 2 1,New Data Item,,Phenotype (13) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (13) name 2 1,New Metadata,,OBO ID: HP:16
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (14) name 2 1,New Data Item,,Phenotype (14) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (14) name 2 1,New Metadata,,OBO ID: HP:17
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (15) name 2 1,New Data Item,,Phenotype (15) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Phenotype,Phenotype (15) name 2 1,New Metadata,,OBO ID: HP:18
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Disorder >2< Clinical tests Level5 Model 2 Data Element 1,New Data Item,,Disorder >2< Clinical tests Level5 Model 2 Data Element 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Clinical tests (1) name 2 1,New Data Item,,Clinical tests (1) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Clinical tests (2) name 2 1,New Data Item,,Clinical tests (2) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Clinical tests (3) name 2 1,New Data Item,,Clinical tests (3) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Clinical tests (4) name 2 1,New Data Item,,Clinical tests (4) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 1 (69@0.0.1),Disorder >>2<< heading Level4 Model Data Element 1 (70@0.0.1),Clinical tests,Clinical tests (5) name 2 1,New Data Item,,Clinical tests (5) name 2 1 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Disorder >2< Phenotypes Level5 Model 2 Data Element 2,New Data Item,,Disorder >2< Phenotypes Level5 Model 2 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (1) name 2 2,New Data Item,,Phenotype (1) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (1) name 2 2,New Metadata,,OBO ID: HP:5
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (2) name 2 2,New Data Item,,Phenotype (2) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (2) name 2 2,New Metadata,,OBO ID: HP:6
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (2) name 2 2,Change Description,,Description: description for Phenotype (2) name 2 2 has been changed also
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (3) name 2 2,New Data Item,,Phenotype (3) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (3) name 2 2,New Metadata,,OBO ID: HP:7
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (4) name 2 2,New Data Item,,Phenotype (4) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (4) name 2 2,New Metadata,,OBO ID: HP:8
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (5) name 2 2,New Data Item,,Phenotype (5) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (5) name 2 2,New Metadata,,OBO ID: HP:9
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (6) name 2 2,New Data Item,,Phenotype (6) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (6) name 2 2,New Metadata,,OBO ID: HP:10
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (7) name 2 2,New Data Item,,Phenotype (7) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (7) name 2 2,New Metadata,,OBO ID: HP:11
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (8) name 2 2,New Data Item,,Phenotype (8) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (8) name 2 2,New Metadata,,OBO ID: HP:12
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (9) name 2 2,New Data Item,,Phenotype (9) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (9) name 2 2,New Metadata,,OBO ID: HP:13
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (10) name 2 2,New Data Item,,Phenotype (10) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (10) name 2 2,New Metadata,,OBO ID: HP:14
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (11) name 2 2,New Data Item,,Phenotype (11) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (11) name 2 2,New Metadata,,OBO ID: HP:15
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (12) name 2 2,New Data Item,,Phenotype (12) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (12) name 2 2,New Metadata,,OBO ID: HP:16
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (13) name 2 2,New Data Item,,Phenotype (13) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (13) name 2 2,New Metadata,,OBO ID: HP:17
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (14) name 2 2,New Data Item,,Phenotype (14) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (14) name 2 2,New Metadata,,OBO ID: HP:18
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (15) name 2 2,New Data Item,,Phenotype (15) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Phenotype,Phenotype (15) name 2 2,New Metadata,,OBO ID: HP:19
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Disorder >2< Clinical tests Level5 Model 2 Data Element 2,New Data Item,,Disorder >2< Clinical tests Level5 Model 2 Data Element 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Clinical tests (1) name 2 2,New Data Item,,Clinical tests (1) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Clinical tests (2) name 2 2,New Data Item,,Clinical tests (2) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Clinical tests (3) name 2 2,New Data Item,,Clinical tests (3) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Clinical tests (4) name 2 2,New Data Item,,Clinical tests (4) name 2 2 (Test Data Model) (0.0.1)
,Disorder >>>2<<< Level2 (68@0.0.1),Disorder >>>2<<< SubCondition Level3 Model Data Element 2 (101@0.0.1),Disorder >>2<< heading Level4 Model Data Element 2 (102@0.0.1),Clinical tests,Clinical tests (5) name 2 2,New Data Item,,Clinical tests (5) name 2 2 (Test Data Model) (0.0.1)
'''

}
