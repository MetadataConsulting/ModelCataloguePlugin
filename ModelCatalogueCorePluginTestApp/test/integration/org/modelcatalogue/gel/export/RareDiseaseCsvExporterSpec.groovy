package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass
import org.modelcatalogue.gel.RareDiseaseCsvExporter

/**
 * FirstTestSpec for RD reports class using test model with similar format to expected data.
 */
class RareDiseaseCsvExporterSpec extends AbstractRareDiseasesExporterSpec {

    static def hpoAndClinicalTestsCSV
    static def eligibilityCSV

    def setup() {

        buildTestModel(true)
        findDataIds()
        generateCSVs()

    }

    private void generateCSVs() {
        def today = new Date().format("yyyy-MM-dd")

        hpoAndClinicalTestsCSV = """id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Phenotype,Phenotype ID,FirstTestSpec,FirstTestSpec ID"""
        for (int i in 1..2) {
            for (int j in 1..2) {
                for (int k in 1..15) {
                    hpoAndClinicalTestsCSV += "\n"
                    hpoAndClinicalTestsCSV += "${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Phenotype ($k) name $i $j,HP:${(i + j + k)},,,"
                }
                for (int l in 1..5) {
                    hpoAndClinicalTestsCSV += "\n"
                    hpoAndClinicalTestsCSV += "${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,,,Clinical tests ($l) name $i $j,${getClinicalTestId(l, i, j)},"
                }
            }
        }

        eligibilityCSV = """id,Level 2 Disease Group,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder,Last Updated,Eligibility Criteria,Criteria ID,Description"""
        for (int i in 1..2) {
            for (int j in 1..2) {
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Inclusion criteria name $i $j,${DataClass.findByName("Inclusion criteria name $i $j").id},"Inclusion criteria description  $i $j",/
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Exclusion criteria name $i $j,${DataClass.findByName("Exclusion criteria name $i $j").id},"Exclusion criteria description  $i $j",/
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Prior Genetic testing name $i $j,${DataClass.findByName("Prior Genetic testing name $i $j").id},"Prior Genetic testing description  $i $j",/
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Prior testing genes name $i $j,${DataClass.findByName("Prior testing genes name $i $j").id},"Prior testing genes description  $i $j",/
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Closing statement name $i $j,${DataClass.findByName("Closing statement name $i $j").id},"Closing statement description  $i $j",/
                eligibilityCSV += "\n"
                eligibilityCSV += /${getLevel2Id(i)},${getLevel2Name(i)},${getLevel3Id(i, j)},${getLevel3Name(i, j)},${getLevel4Id(i, j)},${getLevel4Name(i, j)},$today,Guidance name $i $j,${DataClass.findByName("Guidance name $i $j").id},"Guidance description  $i $j",/
            }
        }

    }

    String getLevel2Id(def pos){
        return DataClass.findByNameIlike("Disorder%$pos%Level2").id
    }

    String getLevel3Id(def level2pos, def level3pos){
        return DataClass.findByNameIlike("Disorder%$level2pos%Level3%$level3pos").id
    }

    String getLevel4Id(def level2pos, def level3pos){
        return DataClass.findByNameIlike("Disorder%$level2pos%Level4%$level3pos").id
    }

    def "export HPO and clinical test data from RD model to csv"() {
        OutputStream out = new ByteArrayOutputStream()

        when:
        DataClass model = DataClass.findByName('Dataclass Top Level 1 Root')
        new RareDiseaseCsvExporter(out, RareDiseaseCsvExporter.HPO_AND_CLINICAL_TESTS).printReport(model)

        String csv = new String(out.toByteArray())

//        csv.eachLine {
//            s -> println(s + '\n')
//        }

        then:

        println("-------------")
        println "expected"
        println(csv.replace('-', '').replace('(', '').replace(')', ''))
        println "actual"
        println hpoAndClinicalTestsCSV.replace('-', '').replace('(', '').replace(')', '')
        println("-------------")

        noExceptionThrown()
        csv.replace('-', '').replace('(', '').replace(')', '') ==~ hpoAndClinicalTestsCSV.replace('-', '').replace('(', '').replace(')', '')
    }

    def "export eligibility data from RD model to csv"() {
        OutputStream out = new ByteArrayOutputStream()

        when:
        DataClass model = DataClass.findByName('Dataclass Top Level 1 Root')
        new RareDiseaseCsvExporter(out, RareDiseaseCsvExporter.ELIGIBILITY).printReport(model)

        String csv = new String(out.toByteArray())

//        csv.eachLine {
//            s -> println(s + '\n')
//        }

        then:
        noExceptionThrown()
        csv ==~ eligibilityCSV
    }

}
