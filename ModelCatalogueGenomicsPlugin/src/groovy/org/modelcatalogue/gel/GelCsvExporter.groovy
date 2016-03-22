package org.modelcatalogue.gel

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass

/**
 * Created by dexterawoyemi on 11/03/2016.
 */
@Log4j
class GelCsvExporter {

    private final OutputStream out

    private String currentLine

    private final def headersLine = "id,name,subGroup_id,subGroup_name,subGroup_specificDisorder_id,subGroup_specificDisorder_name,subGroup_specificDisorder_eligibilityQuestion_date," +
        "subGroup_specificDisorder_eligibilityQuestion_version,subGroup_specificDisorder_shallowPhenotype_name," +
        "subGroup_specificDisorder_shallowPhenotype_id,subGroup_specificDisorder_test_name,subGroup_specificDisorder_test_id"

    GelCsvExporter(OutputStream out) {
        this.out = out
        currentLine = ""
    }

    void printDiseaseOntology(DataClass cls) {
        printRareDiseaseChild(cls, '', 0)
    }

    private void printRareDiseaseChild(DataClass child, String prefix, Integer level) {
        Long id = child.latestVersionId ?: child.id

        if (level == 0) {
            out << headersLine
            child.parentOf?.eachWithIndex { DataClass cd, index ->
                out << '\n'
                printRareDiseaseChild(cd, '', level + 1)
            }
        }

        if (level == 1) {

            def nextPrefix = id + "," + child.name.replace(',', ' - ') + ","

            if (child.parentOf.size() > 0) {

                child.parentOf.eachWithIndex { DataClass cd, index ->
                    if (index != 0) {
                        out << '\n'
                    }
                    printRareDiseaseChild(cd, nextPrefix, level + 1)
                }

            } else {
                currentLine += nextPrefix
                printTrailingCommas()
            }

        }

        if (level == 2) {

            def nextPrefix = prefix + id + "," + child.name.replace(',', ' - ') + ","

            if (child.parentOf.size() > 0) {

                child.parentOf.eachWithIndex { DataClass cd, index ->
                    if (index != 0) {
                        out << '\n'
                    }
                    printRareDiseaseChild(cd, nextPrefix, level + 1)
                }

            } else {
                currentLine += nextPrefix
                printTrailingCommas()
            }

        }

        if (level == 3) {
            println("creating model for " + child.name)

            currentLine += prefix + id + "," + child.name.replace(',', ' - ') + "," + child.lastUpdated.format("yyyy-MM-dd") + "," + child.versionNumber + ","

            if (child.parentOf.size() > 0) {

                def phenotypeModel, testModel

                child.parentOf.each { DataClass cd ->

                    if (cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && !cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                        phenotypeModel = cd
                    }

                    if (!cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                        testModel = cd
                    }
                }

                String finalPrefix = new String(currentLine)
                if (phenotypeModel) printPhenotypes(finalPrefix, phenotypeModel)
                if (testModel) printTests(finalPrefix, testModel)
            }
            printTrailingCommas()
        }
    }

    private void printPhenotypes(String prefix, DataClass child) {
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if (index != 0) {
                out << '\n'
                currentLine += prefix + cd.name.replace(',', ' - ') + "," + cd.ext.get("OBO ID")
            }
            printTrailingCommas()
        }
    }

    private void printTests(String prefix, DataClass child) {
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if (index != 0) {
                out << '\n'
                currentLine += prefix
                currentLine += ",," // not a phenotype
                currentLine += cd.name.replace(',', ' - ') + "," + getVersionId(cd)
            }
            printTrailingCommas()
        }
    }

    private static String getVersionId(CatalogueElement c) {
        return (c.latestVersionId) ? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
    }

    private void printTrailingCommas() {
        int columnsLeftUnfilled = headersLine.split(',').length - currentLine.count(',')

        try {
            1.upto(columnsLeftUnfilled, {
                currentLine += ','
            })
        } catch (GroovyRuntimeException e) {
            log.error 'Too many commas on line: ' + currentLine
            e.printStackTrace()
        }

        out << currentLine

        currentLine = ""

    }

}
