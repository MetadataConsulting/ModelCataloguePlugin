package org.modelcatalogue.gel

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.util.Metadata

import java.nio.charset.StandardCharsets

/**
 * CSV Exporter that traverses each level of the RD Data Class (currently named 'Rare Disease Conditions, Phenotypes and Eligibility Criteria')
 * to output a CSV file containing one row per lowest level item (e.g. one row for each HPO item, one row for each clinical test, one row for each
 * criteria item).
 *
 * The business requirement for these csv files is to be human readable (for human consumption), so they are probably looking at the generated
 * report in Excel. Currently, the file is UTF-8 encoded and may require treatment in Excel after being imported.
 */
@Log4j
class RareDiseaseCsvExporter {

    private def docType
    private String headersLine
    private final OutputStream out

    private String currentLine

    private final String HPO_AND_CLINICAL_TESTS_HEADERS_LINE =
        "id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Phenotype,Phenotype ID,Test,Test ID"

    private final String ELIGIBILITY_HEADERS_LINE =
        "id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Eligibility Criteria,Criteria ID,Description"

    static final def ELIGIBILITY = 0
    static final def HPO_AND_CLINICAL_TESTS = 1

    RareDiseaseCsvExporter(OutputStream out, def docType) {
        this.out = out
        this.docType = docType
        headersLine = docType == HPO_AND_CLINICAL_TESTS ? HPO_AND_CLINICAL_TESTS_HEADERS_LINE : ELIGIBILITY_HEADERS_LINE
        currentLine = ""
    }

    void printReport(DataClass cls) {
        printRareDiseaseChild(cls, '', 0)
    }

    private void printRareDiseaseChild(DataClass child, String prefix, Integer level) {
        def id = child.combinedVersion

        if (level == 0) {
            out << headersLine
            child.parentOf?.eachWithIndex { DataClass cd, index ->
//                out << '\n'
                printRareDiseaseChild(cd, '', level + 1)
            }
        }

        if (level == 1) {

            def nextPrefix = id + "," + child.name.replace(',', ' - ') + ","

            if (child.parentOf.size() > 0) {

                child.parentOf.eachWithIndex { DataClass cd, index ->
                    if (index != 0) {
//                        out << '\n'
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
//                        out << '\n'
                    }
                    printRareDiseaseChild(cd, nextPrefix, level + 1)
                }

            } else {
                currentLine += nextPrefix
                printTrailingCommas()
            }

        }

        if (level == 3) {
            log.info("creating model for " + child.name)

            currentLine += prefix + id + "," + child.name.replace(',', ' - ') + "," + child.lastUpdated.format("yyyy-MM-dd") + ","

            if (child.parentOf.size() > 0) {

                switch (docType) {
                    case HPO_AND_CLINICAL_TESTS:
                        def phenotypeClass, testClass

                        child.parentOf.each { DataClass cd ->

                            if (cd.name ==~ /(?i).*Phenotypes.*/ && cd.name.replace('-', '') != ~/.*Eligibility.*/ && cd.name != ~/.*Test.*/ && cd.name != ~/.*Guidance.*/) {
                                phenotypeClass = cd
                            }

                            if (cd.name != ~/.*Phenotypes.*/ && cd.name.replace('-', '') != ~/.*Eligibility.*/ && cd.name ==~ /(?i).*Test.*/ && cd.name != ~/.*Guidance.*/) {
                                testClass = cd
                            }
                        }

                        String finalPrefix = new String(currentLine)
                        if (phenotypeClass) printPhenotypes(finalPrefix, phenotypeClass)
                        if (testClass) printTests(finalPrefix, testClass)

                        break

                    case ELIGIBILITY:
                        def eligibilityClass

                        child.parentOf.each { DataClass cd ->

                            if (cd.name != ~/.*Phenotypes.*/ && cd.name ==~ /(?i).*Eligibility.*/ && cd.name != ~/.*Test.*/ && cd.name != ~/.*Guidance.*/) {
                                eligibilityClass = cd
                            }

                        }

                        String finalPrefix = new String(currentLine)
                        if (eligibilityClass) printEligibility(finalPrefix, eligibilityClass)

                        break

                }
            }
//            printTrailingCommas()
        }
    }

    def printEligibility(String prefix, DataClass child) {
        child.parentOf.eachWithIndex { DataClass cd, index ->
            try {
                if (cd?.name?.trim()) {
                    out << '\n'
                    def description = null
//                    if (cd.description?.trim()) description = new String(StandardCharsets.UTF_8.encode(cd.description).array()).replace('•', '>').replaceAll("(?m)^\\-", '>')
                    if (cd.description?.trim()) description = cd.description.replace('•', '>').replaceAll("(?m)^\\-", '>')
                    if (index != 0) currentLine += prefix
                    currentLine += "${cd.name.replace(',', ' - ')},$cd.combinedVersion,${description ? "\"${description.replace('\"', '\"\"')}\"" : ""}"
                    printTrailingCommas()
                }
            } catch (Exception e) {
                log.error e
            }
        }
    }

    private void printPhenotypes(String prefix, DataClass child) {
        child.parentOf.eachWithIndex { DataClass cd, index ->
            try {
                if (cd?.name?.trim()) {
                    out << '\n'
                    if (index != 0) currentLine += prefix
                    currentLine += "${cd.name.replace(',', ' - ')},${cd.ext.get(Metadata.OBO_ID)}"
                    printTrailingCommas()
                }
            } catch (Exception e) {
                log.error e
            }
        }
    }

    private void printTests(String prefix, DataClass child) {
        child.parentOf.eachWithIndex { DataClass cd, index ->
            try {
                if (cd?.name?.trim()) {
                    out << '\n'
                    if (index != 0 || currentLine.isEmpty()) currentLine += prefix
                    currentLine += ",," // not a phenotype
                    currentLine += "${cd.name.replace(',', ' - ')},${getVersionId(cd)}"
                    printTrailingCommas()
                }
            } catch (Exception e) {
                log.error e
            }
        }
    }

    public static String getVersionId(CatalogueElement c) {
        return (c.latestVersionId) ? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
    }

    private void printTrailingCommas() {

        def commasInQuotesMatcher = currentLine =~ /(?ms)".*?"/
        int commasInQuotes = 0
        commasInQuotesMatcher.each { String it ->
            commasInQuotes += (it.size() - it.replace(',', '').size())
        }

        int columnsLeftUnfilled = headersLine.count(',') - (currentLine.count(',') - commasInQuotes)

        try {
            0.upto(columnsLeftUnfilled, {
                currentLine += ','
            })
        } catch (GroovyRuntimeException e) {
            log.error "Too many commas on line: $currentLine"
            e.printStackTrace()
        }

        out << currentLine

        currentLine = ""

    }

}
