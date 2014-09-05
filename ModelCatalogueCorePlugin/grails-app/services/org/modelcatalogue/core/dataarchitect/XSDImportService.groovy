package org.modelcatalogue.core.dataarchitect

import grails.transaction.Transactional
import org.modelcatalogue.core.ConceptualDomain
import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.dataarchitect.xsd.XsdComplexType
import org.modelcatalogue.core.dataarchitect.xsd.XsdPattern
import org.modelcatalogue.core.dataarchitect.xsd.XsdRestriction
import org.modelcatalogue.core.dataarchitect.xsd.XsdSimpleType
import org.modelcatalogue.core.dataarchitect.xsd.XsdUnion

@Transactional
class XSDImportService {


    def createValueDomainsAndDataTypes(Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){

        simpleDataTypes.each{ XsdSimpleType simpleDataType ->
            def valueDomain = findValueDomain(simpleDataType.name, conceptualDomain.id)
            if(!valueDomain) createValueDomain(simpleDataType, simpleDataTypes, conceptualDomain)
        }

    }


    def createValueDomain(XsdSimpleType simpleDataType, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){
        def (dataType, rule, baseValueDomain) = getRestrictionDetails(simpleDataType.restriction, conceptualDomain, simpleDataType.name)

        def valueDomain = new ValueDomain(name: simpleDataType.name, description: simpleDataType.description, dataType: dataType, rule: rule).save()
        if(baseValueDomain) valueDomain.isBasedOn(baseValueDomain)
        if(simpleDataType.union) valueDomain = addUnions(valueDomain, simpleDataType.union, simpleDataTypes, conceptualDomain)

        //TODO: get metadata(is there any?)

        return valueDomain
    }

    def addUnions(ValueDomain valueDomain, XsdUnion union, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain){

        union.simpleTypes.each{simpleDataType ->
            def unionValueDomain = findValueDomain(simpleDataType.name, conceptualDomain.id)
            if(!unionValueDomain) unionValueDomain = createValueDomain(simpleDataType, simpleDataTypes, conceptualDomain)
            unionValueDomain.isUnitedIn(valueDomain)
        }

    }

    def findValueDomain(String name, Integer conceptualDomainId){

        def valueDomain = ConceptualDomain.withCriteria {
            eq('id', conceptualDomainId)
            valueDomains{
                eq('name', name)
            }
        }

        return valueDomain
    }

    def getRestrictionDetails(XsdRestriction restriction, Collection<XsdSimpleType> simpleDataTypes, ConceptualDomain conceptualDomain, String simpleTypeName){

        DataType dataType
        String rule
        String base = restriction?.base
        ValueDomain baseValueDomain
        rule = getRuleFromPattern(restriction.patterns)

        if(base && base.contains("xs:")){
            dataType = DataType.findByName(base)
            return [dataType, rule, ""]
        }else if(base){
            baseValueDomain = findValueDomain(base, conceptualDomain.id)
            if(!baseValueDomain) {
                XsdSimpleType simpleDataType = simpleDataTypes.find{it.name==base}
                if(simpleDataType) baseValueDomain = createValueDomain(simpleDataType)
            }
            if(!baseValueDomain){ throw Exception("imported Simple Type base does not exist in the schema or in the system, please validate you schema or import the schema it is dependant on")}
            dataType = baseValueDomain.dataType
            rule = addToRule(rule, baseValueDomain.rule)
        }

        if(restriction.enumeration) dataType =  createEnumeratedType(simpleTypeName, restriction.enumeration)

        return [dataType, rule, baseValueDomain]
    }

    def createModelsAndElements(Collection<XsdComplexType> complexDataTypes){

    }


    def getRuleFromPattern(ArrayList<XsdPattern> patterns){
        String rule = ""
        patterns.each{ XsdPattern pattern
            if(!rule=="") rule =  addToRule(rule , pattern.value ) else rule = pattern.value
        }
        return rule
    }

    def addToRule(String rule1, String rule2){
        String rule
        rule = rule1 + "||" +  rule2
        return rule
    }

    def createEnumeratedType(String name, String enumerations){

    }

}
