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

    //pass the top level rare disease conditions class into the function
    //prefix allows us to pass the disease name hierarchy into the cells
    // i.e. if we are going for phenotypes the prefix could be
    // 30783, Ultra-rare disorders, 38589, Multi-system groups, 42193, Undiagnosed monogenic disorder seen in a specialist genetics clinic
    private void printRareDiseaseChild(DataClass child, String prefix, Integer level) {
        def id = child.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: child.getModelCatalogueId() ?: child.getLatestVersionId() ?: child.getId()

        //level 0 is the top level class
        if (level == 0) {
            //add the headers line
            out << headersLine
            //traverse the hierarchy to the disease
            child.parentOf?.eachWithIndex { DataClass cd, index ->
                //call the same method again on the child classes
                //but increase the level, prefix will be nothing as we haven't written a row yet
                printRareDiseaseChild(cd, '', level + 1)
            }
        }

        //level 1 is the top level disease category
        // this is just creating a prefix for the eligibility line, phenotype line or the test line
        if (level == 1) {
            //create the first part of the prefix
            def nextPrefix = id + "," + child.name.replace(',', ' - ') + ","

            //if there are children then traverse the hierarchy else just pass the prefix and move to the next line
            if (child.parentOf.size() > 0) {
                child.parentOf.eachWithIndex { DataClass cd, index ->
                    printRareDiseaseChild(cd, nextPrefix, level + 1)
                }
            } else {
                currentLine += nextPrefix
                printTrailingCommas()
            }
        }

        //level 2 is the second level disease category
        // this is just creating a prefix for the eligibility line, phenotype line or the test line
        if (level == 2) {

            //add the second part of the prefix
            def nextPrefix = prefix + id + "," + child.name.replace(',', ' - ') + ","

            //if there are children then traverse the hierarchy else just pass the prefix and move to the next line
            if (child.parentOf.size() > 0) {
                child.parentOf.eachWithIndex { DataClass cd, index ->
                    printRareDiseaseChild(cd, nextPrefix, level + 1)
                }
            } else {
                currentLine += nextPrefix
                printTrailingCommas()
            }

        }

        //level 3 is the disease
        // this is just creating a prefix for the eligibility line, phenotype line or the test line
        if (level == 3) {
            log.info("creating model for " + child.name)
            //create the current line i.e. what is going to be printed based on the prefix passed from level 2 and 3
            currentLine += prefix + id + "," + child.name.replace(',', ' - ') + "," + child.lastUpdated.format("yyyy-MM-dd") + ","
            //check that there are children - it isn't an empty disease
            if (child.parentOf.size() > 0) {

                switch (docType) {
                    case HPO_AND_CLINICAL_TESTS:
                        def phenotypeClass, testClass

                        child.parentOf.each { DataClass cd ->

                            if (cd.name ==~ /(?i).*Phenotype.*/ && cd.name.replace('-', '') != ~/(?i).*Eligibility.*/ && cd.name != ~/(?i).*Test.*/ && cd.name != ~/(?i).*Guidance.*/) {
                                phenotypeClass = cd
                            }

                            if (cd.name != ~/(?i).*Phenotype.*/ && cd.name.replace('-', '') != ~/(?i).*Eligibility.*/ && cd.name ==~ /(?i).*Test.*/ && cd.name != ~/(?i).*Guidance.*/) {
                                testClass = cd
                            }
                        }

                        String finalPrefix = new String(currentLine)
                        Boolean printed = false
                        if (phenotypeClass && phenotypeClass.parentOf.size()>0) {
                            printPhenotypes(finalPrefix, phenotypeClass)
                            printed = true
                        }
                        if (testClass && testClass.parentOf.size()>0) {
                            printTests(finalPrefix, testClass)
                            printed = true
                        }
                        if(!printed){
                            out << '\n'
                            printTrailingCommas()
                        }

                        break

                    case ELIGIBILITY:
                        def eligibilityClass

                        child.parentOf.each { DataClass cd ->

                            if (cd.name != ~/(?i).*Phenotype.*/ && cd.name ==~ /(?i).*Eligibility.*/ && cd.name != ~/(?i).*Test.*/ && cd.name != ~/(?i).*Guidance.*/) {
                                eligibilityClass = cd
                            }

                        }

                        String finalPrefix = new String(currentLine)
                        if (eligibilityClass) printEligibility(finalPrefix, eligibilityClass)

                        break

                }
            }
        }
    }

    def printEligibility(String prefix, DataClass child) {

        def children = child.parentOf

        if(children.size()>0) {

            child.parentOf.eachWithIndex { DataClass cd, index ->
                try {
                    if (cd?.name?.trim()) {
                        out << '\n'
                        def description = null
//                    if (cd.description?.trim()) description = new String(StandardCharsets.UTF_8.encode(cd.description).array()).replace('•', '>').replaceAll("(?m)^\\-", '>')
                        if (cd.description?.trim()) description = cd.description.replace('•', '>').replaceAll("(?m)^\\-", '>')
                        if (index != 0) currentLine += prefix
                        currentLine += "${cd.name.replace(',', ' - ')},${cd.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: cd.getModelCatalogueId() ?: cd.getLatestVersionId() ?: cd.getId()},${description ? "\"${description.replace('\"', '\"\"')}\"" : ""}"
                        printTrailingCommas()
                    }
                } catch (Exception e) {
                    log.error e
                }
            }
        }else{
            out << '\n'
            printTrailingCommas()
        }
    }

    private void printPhenotypes(String prefix, DataClass child) {

            def children = child.parentOf

            if(children.size()>0) {

                children.eachWithIndex { DataClass cd, index ->
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
            }else{
                out << '\n'
                printTrailingCommas()
            }
    }

    private void printTests(String prefix, DataClass child) {
        def children = child.parentOf
        if(children.size()>0) {
                children.eachWithIndex { DataClass cd, index ->
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
        }else{
            out << '\n'
            printTrailingCommas()
        }
    }

    public static String getVersionId(CatalogueElement c) {
        return c.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-test-id-versioned")?:c.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: c.getModelCatalogueId() ?: c.getLatestVersionId() ?: c.getId()
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
