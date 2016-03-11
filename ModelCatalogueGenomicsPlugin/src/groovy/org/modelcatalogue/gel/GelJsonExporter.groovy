package org.modelcatalogue.gel

import groovy.json.JsonOutput
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass


class GelJsonExporter {

    private final OutputStream out

    GelJsonExporter(OutputStream out) {
        this.out = out
    }

    private void printPhenotypes(DataClass child){
        out << '           "shallowPhenotypes" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if(index!=0){out << ','}
            printHPOterm(cd)
        }
        out << "           ],\n"
    }

    private void printTests(DataClass child) {
        out << '           "tests" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if (index != 0) {
                out << ','
            }
            printTest(cd)
        }
        out << "           ]\n"
    }

    private void printHPOterm(DataClass child){
        out << '                    {\n'
        out << '                        "name" : ' + JsonOutput.toJson(child.name)  + ',\n'
        out << '                        "id"   : ' + JsonOutput.toJson(child.ext.get("OBO ID")) + '\n'
        out << '                    }\n'
    }

    private void printTest(DataClass child){

        out << '                    {\n'
        out << '                        "name" : ' + JsonOutput.toJson(child.name)  + ',\n'
        out << '                        "id"   : ' + JsonOutput.toJson(getVersionId(child)) + '\n'
        out << '                    }\n'
    }


    private static String getVersionId(CatalogueElement c){
        return (c.latestVersionId)? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
    }

    void printDiseaseOntology(DataClass cls){
        out << '{\"DiseaseGroups\": [\n'
        printRareDiseaseChild(cls, 0)
        out << "]\n}"
    }


    private void printRareDiseaseChild(DataClass child, Integer level){
        Long id = child.latestVersionId ?: child.id
        if(level==0){
            child.parentOf?.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(cd, level + 1)
            }
        }

        if (level == 1) {

            out << '{ \n'
            out << '   "id" : "' + id  + '",\n'
            out << '   "name" : ' + JsonOutput.toJson(child.name) + ','
            out << '   "subGroups" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(cd, level + 1)
            }

            out << "]\n"
            out << '        }'
        }

        if (level == 2) {

            out << '{      \n'
            out << '       "id" : "' + id +'",\n'

            out << '       "name" : ' + JsonOutput.toJson(child.name) + ','
            out << '       "specificDisorders" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(cd, level + 1)
            }

            out << "       ]\n"
            out << "       }\n"
        }

        if (level == 3) {
            println("creating model for " + child.name)

            out << '           { \n'
            out << '           "id" : "' + id  +'",\n'
            out << '            "name" : ' + JsonOutput.toJson(child.name) + ',\n'
            out << '                "eligibilityQuestion": {\n'
            out << '                        "date":"' + child.lastUpdated.format("yyyy-MM-dd") + '",\n'
            out << '                        "version": "' +child.versionNumber +'"\n'
            out << '                   },\n'


            def phenotypeModel, testModel

            child.parentOf.each { DataClass cd ->

                if (cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && !cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                    phenotypeModel = cd
                }

                if(!cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                    testModel = cd
                }
            }

            if(phenotypeModel) printPhenotypes(phenotypeModel) else out << '           "shallowPhenotypes" : [],\n'
            if(testModel) printTests(testModel) else out << '           "tests" : []\n'

            out << "           }\n"
        }
    }

}









