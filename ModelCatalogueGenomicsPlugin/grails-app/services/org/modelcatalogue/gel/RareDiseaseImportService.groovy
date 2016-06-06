package org.modelcatalogue.gel

import com.google.common.base.Preconditions
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.modelcatalogue.builder.api.CatalogueBuilder
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.util.builder.DefaultCatalogueBuilder
import org.modelcatalogue.integration.obo.OboLoader

class RareDiseaseImportService {

    LinkGenerator grailsLinkGenerator

    Set<CatalogueElement> importDisorderedCsv(CatalogueBuilder catalogueBuilder, String dataModelName,
                                              String hpoDataModelName, String testDataModelName, InputStream inputStream) {
        Preconditions.checkArgument(dataModelName as Boolean)
        Preconditions.checkArgument(hpoDataModelName as Boolean)
        Preconditions.checkArgument(testDataModelName as Boolean)
        Preconditions.checkArgument(inputStream as Boolean)

        catalogueBuilder.build {
            dataModel(name: dataModelName) {
                copy relationships
                inputStream.toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
                    Preconditions.checkElementIndex(10, tokens.length, "Unexpected number of column in csv export, minimum number is 10")

                    dataClass(id: expandToLocalId(tokens[0]), name: tokens[1]) {
                        dataClass(id: expandToLocalId(tokens[2]), name: tokens[3]) {
                            dataClass(id: expandToLocalId(tokens[4]), name: tokens[5]) {
                                // add hpo relationship if specified in csv
                                if (tokens[8]) {
                                    // create hpo if not present
                                    def hpo = getHpo(hpoDataModelName, tokens[8])
                                    if (hpo) {
                                        rel 'hierarchy' to hpo.dataModel.name, hpo.name
                                    } else {
                                        dataClass name: tokens[7], dataModel: hpoDataModelName, {
                                            ext OboLoader.OBO_ID, tokens[8]
                                        }
                                    }
                                }
                                // add clinical test relationship if specified in csv
                                if (tokens[10]) {
                                    // create clinical test if not present
                                    def clinicalTest = getClinicalTest(testDataModelName, tokens[10])
                                    if (clinicalTest) {
                                        rel 'hierarchy' to clinicalTest.dataModel.name, clinicalTest.name
                                    } else {
                                        dataClass id: expandToLocalId(tokens[10]), name: tokens[9], dataModel: testDataModelName
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return ((DefaultCatalogueBuilder) catalogueBuilder).created
    }

    private DataClass getHpo(String dataModelName, String hpoId) {
        def extensionValues = ExtensionValue.withCriteria {
            eq('name', OboLoader.OBO_ID)
            eq('extensionValue', hpoId)
            element {
                dataModel {
                    eq('name', dataModelName)
                }
            }
        }
        if (extensionValues.size() > 0) {
            def element = extensionValues.get(0).element
            if (element.instanceOf(DataClass)) {
                return element as DataClass
            }
        }
        return null
    }

    private DataClass getClinicalTest(String dataModelName, String clinicalTestId) {
        def dataClasses = DataClass.withCriteria {
            eq('modelCatalogueId', expandToLocalId(clinicalTestId))
            dataModel {
                eq('name', dataModelName)
            }
        }
        if (dataClasses.size() > 0) {
            return dataClasses.get(0)
        }
        return null
    }

    private String expandToLocalId(String shortId) {
        return grailsLinkGenerator.link(absolute: true, uri: "/catalogue/dataClass/$shortId")
    }
}
