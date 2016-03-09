package modelcataloguegenomicsplugin


import grails.transaction.Transactional
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass


//@Transactional
class GelJsonService {

    def printPhenotypes(DataClass child){
        def jsonString = ''
        jsonString<<='           "shallowPhenotypes" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if(index!=0){jsonString<<=','}
            jsonString<<=printHPOterm(cd)
        }
        jsonString<<="           ],\n"
        return jsonString
    }

    def printTests(DataClass child) {
        def jsonString = ''
        jsonString<<='           "tests" : [\n'
        child.parentOf.eachWithIndex { DataClass cd, index ->
            if (index != 0) {
                jsonString <<= ','
            }
            jsonString <<= printTest(cd)
        }
        jsonString<<="           ]\n"
        return jsonString
    }

    def printHPOterm(DataClass child){
        def jsonString = ''
        jsonString<<='                    {\n'
        jsonString<<='                        "name" : "' + child.name  + '",\n'
        jsonString<<='                        "id"   : "' + child.ext.get("OBO ID") + '"\n'
        jsonString<<='                    }\n'
        return jsonString.toString()

    }

    def printTest(DataClass child){
        def jsonString = ''
        jsonString<<='                    {\n'
        jsonString<<='                        "name" : "' + child.name  + '",\n'
        jsonString<<='                        "id"   : "' + getVersionId(child) + '"\n'
        jsonString<<='                    }\n'
        return jsonString.toString()
    }


    def getVersionId(CatalogueElement c){
        String id = (c.latestVersionId)? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
        return id
    }

    def printDiseaseOntology(DataClass cls){

        def jsonStringBuffer = new StringBuffer(10*1024);

        jsonStringBuffer<<='{\"DiseaseGroups\": [\n'
        jsonStringBuffer<<=printRareDiseaseChild(cls, 0)
        jsonStringBuffer<<="]\n}"
        return jsonStringBuffer.toString();
    }


    def printRareDiseaseChild(DataClass child, Integer level){
        def jsonString = ''
        def id = child.latestVersionId ?: child.id
        if(level==0){
            child.parentOf?.eachWithIndex { DataClass cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }
        }

        if (level == 1) {

            jsonString<<='{ \n'
            jsonString<<='   "id" : "' + id  + '",\n'
            jsonString<<='   "name" : "' + child.name+ '",'
            jsonString<<='   "subGroups" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }

            jsonString<<="]\n"
            jsonString<<='        }'
        }

        if (level == 2) {

            jsonString<<='{      \n'
            jsonString<<='       "id" : "' + id +'",\n'

            jsonString<<='       "name" : "' + child.name+ '",'
            jsonString<<='       "specificDisorders" : ['

            child.parentOf.eachWithIndex { DataClass cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }

            jsonString<<="       ]\n"
            jsonString<<="       }\n"
        }

        if (level == 3) {
            println("creating model for " + child.name)

            jsonString<<='           { \n'
            jsonString<<='           "id" : "' + id  +'",\n'
            jsonString<<='            "name" : "' + child.name+ '",\n'
            jsonString<<='                "eligibilityQuestion": {\n'
            jsonString<<='                        "date":"' + child.lastUpdated.format("yyyy-MM-dd") + '",\n'
            jsonString<<='                        "version": "' +child.versionNumber +'"\n'
            jsonString<<='                   },\n'


            def phenotypeModel, testModel

            child.parentOf.each { DataClass cd ->

                if (cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && !cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                    phenotypeModel = cd
                }

                if(!cd.name.matches("(?i:.*Phenotypes.*)") && !cd.name.matches("(?i:.*Eligibility.*)") && cd.name.matches("(?i:.*Test.*)") && !cd.name.matches("(?i:.*Guidance.*)")) {
                    testModel = cd
                }
            }

            if(phenotypeModel)jsonString<<= printPhenotypes(phenotypeModel) else jsonString<<='           "shallowPhenotypes" : [],\n'
            if(testModel)jsonString<<= printTests(testModel) else jsonString<<='           "tests" : []\n'

            jsonString<<="           }\n"
        }


        return jsonString.toString()

    }

}









