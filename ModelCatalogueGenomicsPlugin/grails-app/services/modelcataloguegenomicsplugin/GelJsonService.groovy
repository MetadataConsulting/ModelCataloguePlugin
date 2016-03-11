package modelcataloguegenomicsplugin


import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass


class GelJsonService {

    void printPhenotypes(OutputStream out, DataClass child){
        out << '           "shallowPhenotypes" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if(index!=0){out << ','}
            printHPOterm(out, cd)
        }
        out << "           ],\n"
    }

    void printTests(OutputStream out, DataClass child) {
        out << '           "tests" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if (index != 0) {
                out << ','
            }
            printTest(out, cd)
        }
        out << "           ]\n"
    }

    void printHPOterm(OutputStream out, DataClass child){
        out << '                    {\n'
        out << '                        "name" : "' + child.name  + '",\n'
        out << '                        "id"   : "' + child.ext.get("OBO ID") + '"\n'
        out << '                    }\n'
    }

    void printTest(OutputStream out, DataClass child){

        out << '                    {\n'
        out << '                        "name" : "' + child.name  + '",\n'
        out << '                        "id"   : "' + getVersionId(child) + '"\n'
        out << '                    }\n'
    }


    String getVersionId(CatalogueElement c){
        return (c.latestVersionId)? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
    }

    void printDiseaseOntology(OutputStream out, DataClass cls){
        out << '{\"DiseaseGroups\": [\n'
        printRareDiseaseChild(out, cls, 0)
        out << "]\n}"
    }


    void printRareDiseaseChild(OutputStream out, DataClass child, Integer level){
        Long id = child.latestVersionId ?: child.id
        if(level==0){
            child.parentOf?.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(out, cd, level + 1)
            }
        }

        if (level == 1) {

            out << '{ \n'
            out << '   "id" : "' + id  + '",\n'
            out << '   "name" : "' + child.name+ '",'
            out << '   "subGroups" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(out, cd, level + 1)
            }

            out << "]\n"
            out << '        }'
        }

        if (level == 2) {

            out << '{      \n'
            out << '       "id" : "' + id +'",\n'

            out << '       "name" : "' + child.name+ '",'
            out << '       "specificDisorders" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){out << ','}
                printRareDiseaseChild(out, cd, level + 1)
            }

            out << "       ]\n"
            out << "       }\n"
        }

        if (level == 3) {
            println("creating model for " + child.name)

            out << '           { \n'
            out << '           "id" : "' + id  +'",\n'
            out << '            "name" : "' + child.name+ '",\n'
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

            if(phenotypeModel) printPhenotypes(out, phenotypeModel) else out << '           "shallowPhenotypes" : [],\n'
            if(testModel) printTests(out, testModel) else out << '           "tests" : []\n'

            out << "           }\n"
        }
    }

}









