package org.modelcatalogue.gel

import com.google.common.base.Preconditions
import grails.gorm.DetachedCriteria
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.util.builder.CatalogueElementProxyRepository
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader

class RareDiseaseImportService {

    LinkGenerator grailsLinkGenerator
    ElementService elementService

    Set<CatalogueElement> importDisorderedCsv(CatalogueBuilder catalogueBuilder, DataModel dataModelToImport,
                                              DataModel hpoDataModel, DataModel testDataModel, InputStream inputStream) {
        Preconditions.checkNotNull(dataModelToImport)
        Preconditions.checkNotNull(hpoDataModel)
        Preconditions.checkNotNull(testDataModel)
        Preconditions.checkNotNull(inputStream)

        catalogueBuilder.build {
            copy relationships
            dataModel(id: dataModelToImport.getDefaultModelCatalogueId(true), name: dataModelToImport.name) {
                inputStream.toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
                    // check given csv
                    Preconditions.checkElementIndex(10, tokens.length, "Unexpected number of column in csv export, minimum number is 10")
                    // check existence of data classes and their belonging to data model
                    [
                        [level: 2, id: tokens[0], dataClass: elementService.findByModelCatalogueId(DataClass, expandToLocalIdWithoutVersion(tokens[0]))],
                        [level: 3, id: tokens[2], dataClass: elementService.findByModelCatalogueId(DataClass, expandToLocalIdWithoutVersion(tokens[2]))],
                        [level: 4, id: tokens[4], dataClass: elementService.findByModelCatalogueId(DataClass, expandToLocalIdWithoutVersion(tokens[4]))],
                    ].each {
                        Preconditions.checkNotNull(it.dataClass, "Cannot find level ${it.level} data class with id ${it.id}")
                        Preconditions.checkArgument(it.dataClass.dataModel != dataModelToImport, "Level ${it.level} " +
                            "data class with id ${it.id} doesn't belong to selected Data Model ${dataModelToImport}")
                    }

                    dataClass(id: expandToLocalIdWithoutVersion(tokens[0]), name: tokens[1]) {
                        dataClass(id: expandToLocalIdWithoutVersion(tokens[2]), name: tokens[3]) {
                            dataClass(id: expandToLocalIdWithoutVersion(tokens[4]), name: tokens[5]) {
                                // add hpo relationship if specified in csv
                                if (tokens[8]) {
                                    // find hpo data model
                                    def hpo = getHpo(hpoDataModel, tokens[8])
                                    // throw exception when there is no hpo data class
                                    Preconditions.checkNotNull(hpo, "Cannot find data class with given phenotype id ${tokens[8]} for Hpo data model ${hpoDataModel}")
                                    rel 'hierarchy' to hpo.dataModel.name, hpo.name
                                }
                                // add clinical test relationship if specified in csv
                                if (tokens[10]) {
                                    // create clinical test if not present
                                    def clinicalTest = getClinicalTest(testDataModel, tokens[10])
                                    // throw exception when there is not clinical test data class
                                    Preconditions.checkNotNull(clinicalTest, "Cannot find data class with given id ${tokens[10]} for Clinical Tests data model ${testDataModel}")
                                    rel 'hierarchy' to clinicalTest.dataModel.name, clinicalTest.name
                                }
                            }
                        }
                    }
                }
            }
        }

        return ((DefaultCatalogueBuilder) catalogueBuilder).created
    }

    static DataClass getHpo(DataModel dataModel, String hpoId) {
        DetachedCriteria<DataClass> criteria = new DetachedCriteria<DataClass>(DataClass).build {
            eq 'modelCatalogueId', hpoId
            eq 'dataModel', dataModel
        }
        ElementService.getLatestFromCriteria(criteria) as DataClass
    }

    private DataClass getClinicalTest(DataModel dataModel, String clinicalTestId) {
        def localId = expandToLocalIdWithoutVersion(clinicalTestId)
        def clinicalTest = elementService.findByModelCatalogueId(DataClass, localId) ?: CatalogueElementProxyRepository.findByMissingReferenceId(localId)

        if (clinicalTest && clinicalTest.dataModel.id == dataModel.id) {
            return clinicalTest
        }
        return null
    }

    private String expandToLocalIdWithoutVersion(String shortId) {
        // get rid of version - version might be after dot or at character
        return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/dataClass/${StringUtils.split(shortId, "@.")[0]}")
    }
}
