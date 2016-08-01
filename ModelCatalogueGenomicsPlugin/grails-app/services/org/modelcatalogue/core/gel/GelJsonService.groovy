package org.modelcatalogue.core.gel

import java.util.Collection;
import java.util.List;

import grails.transaction.Transactional
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import org.apache.commons.lang.math.NumberUtils;
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain


@Transactional
class GelJsonService {

    def jsonString = new StringBuffer()

    def printDiseaseOntology(Model model){

        def jsonStringBuffer = new StringBuffer(10*1024);

        jsonStringBuffer<<='{\"DiseaseGroups\": [\n'
        jsonStringBuffer<<=printRareDiseaseChild(model, 0)
        jsonStringBuffer<<="]\n}"
        return jsonStringBuffer.toString();
    }


    def printRareDiseaseChild(Model child, Integer level){
        def jsonString=''
        def id = child.latestVersionId ?: child.id



        if(level==0){
            child.parentOf?.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }
        }

        if (level == 1) {

            jsonString<<='{ \n'
            jsonString<<='   "id" : "' + id  + '",\n'
            jsonString<<='   "name" : "' + child.name+ '",'
            jsonString<<='   "subGroups" : ['

            child.parentOf.eachWithIndex { Model cd, index ->
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

            child.parentOf.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }

            jsonString<<="       ]\n"
            jsonString<<="       }\n"
        }

        if (level == 3) {

            jsonString<<='           { \n'
            jsonString<<='           "id" : "' + id  +'",\n'
            jsonString<<='            "name" : "' + child.name+ '",\n'
            jsonString<<='                "eligibilityQuestion": {\n'
            jsonString<<='                        "date":"' + child.lastUpdated.format("yyyy-mm-dd") + '",\n'
            jsonString<<='                        "version": "' +child.versionNumber +'"\n'
            jsonString<<='                   },\n'

            jsonString<<='           "shallowPhenotypes" : [\n'

            child.parentOf.each { Model cd ->
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }

            jsonString<<="           ]\n"
            jsonString<<="           }\n"
        }

        if (level == 4 && child.name.matches("(?i:.*Phenotypes.*)") && !child.name.matches("(?i:.*Eligibility.*)") && !child.name.matches("(?i:.*Test.*)") && !child.name.matches("(?i:.*Guidance.*)")) {
            child.parentOf.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printRareDiseaseChild(cd, level + 1)
            }
        }

        if (level == 5) {

            jsonString<<='                    {\n'
            jsonString<<='                        "name" : "' + child.name  + '",\n'
            jsonString<<='                        "id"   : "' + child.ext.get("OBO ID") + '"\n'
            jsonString<<='                    }\n'
        }

        return jsonString.toString()
    }


    def printCancerTypeList(Model model){

        def jsonStringBuffer = new StringBuffer(10*1024);

        jsonStringBuffer<<='{\"CancerTypes\": [\n'
        jsonStringBuffer<<=printCancerChild(model)
        jsonStringBuffer<<="]\n}"
        return jsonStringBuffer.toString();
    }

    def printCancerChild(Model model){
        def jsonString=''
        model.parentOf.eachWithIndex{ Model child, index ->
                if(index!=0){jsonString<<=','}
                def id = getVersionId(child)
                jsonString<<='{ \n'
                jsonString<<='   "id" : "' + id  + '",\n'
                jsonString<<='   "type" : "' + child.name+ '",'
                jsonString<<='   "subTypes" : ['

                def de = child.contains[0]
                de.valueDomain.dataType.enumerations.eachWithIndex { en, enIndex  ->
                    if(enIndex!=0){jsonString<<=','}
                    jsonString<<='{ \n'
                    jsonString<<='   "subType" : "' + en.key+ '",'
                    jsonString<<='   "description" : "' + en.value+ '"'
                    jsonString<<='        }'
                }

                jsonString<<="]\n"
                jsonString<<='        }'
            }

        return jsonString.toString()
    }

    def getVersionId(CatalogueElement c){
        String id = (c.latestVersionId)? c.latestVersionId + "." + c.versionNumber : c.id + "." + c.versionNumber
        return id
    }

}
