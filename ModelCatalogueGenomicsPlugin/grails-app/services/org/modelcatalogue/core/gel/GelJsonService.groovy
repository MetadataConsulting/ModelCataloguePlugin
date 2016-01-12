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
        jsonStringBuffer<<=printChild(model, 0)
        jsonStringBuffer<<="]\n}"
        return jsonStringBuffer.toString();
    }


    def printChild(Model child, Integer level){
        def jsonString=''
        def id = child.latestVersionId ?: child.id



        if(level==0){
            child.parentOf?.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printChild(cd, level + 1)
            }
        }

        if (level == 1) {

            jsonString<<='{ \n'
            jsonString<<='   "id" : "' + id  + '",\n'
            jsonString<<='   "name" : "' + child.name+ '",'
            jsonString<<='   "subGroups" : ['

            child.parentOf.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printChild(cd, level + 1)
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
                jsonString<<=printChild(cd, level + 1)
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
                jsonString<<=printChild(cd, level + 1)
            }

            jsonString<<="           ]\n"
            jsonString<<="           }\n"
        }

        if (level == 4 && child.name.matches("(?i:.*Phenotypes.*)") && !child.name.matches("(?i:.*Eligibility.*)") && !child.name.matches("(?i:.*Test.*)") && !child.name.matches("(?i:.*Guidance.*)")) {
            child.parentOf.eachWithIndex { Model cd, index ->
                if(index!=0){jsonString<<=','}
                jsonString<<=printChild(cd, level + 1)
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
}
