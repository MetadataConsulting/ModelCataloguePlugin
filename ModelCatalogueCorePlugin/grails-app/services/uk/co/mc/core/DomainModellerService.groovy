package uk.co.mc.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.web.converters.configuration.configtest
import org.omg.CosNaming._NamingContextExtStub

@Transactional
class DomainModellerService {

    def grailsApplication

    def modelDomains() {
        RelationshipType.initDefaultRelationshipTypes()
        DataType.initDefaultDataTypes()
        def conceptualDomain = new ConceptualDomain(name:"uk.co.mc.core", description: "Model Catalogue Conceptual Domain").save()
        def domainClasses =  grailsApplication.domainClasses

        domainClasses.each{ GrailsDomainClass domainClass ->

            Model model = new Model(
                    name: domainClass.shortName
            ).save()

            model.addToHasContextOf(conceptualDomain)

            def constraints = domainClass.getConstrainedProperties()
            def properties = domainClass.getProperties()


            properties.each{ property ->
                String name = property.name
                DataType dataType = DataType.findByName(property.type.name)
                if(!dataType){
                    dataType = new DataType(
                        name: property.type.name
                        ).save()
                }

                def constraint = (constraints.get(name))? constraints.get(name).appliedConstraints : null


                ValueDomain valueDomain = new ValueDomain(
                                    name: name,
                                    dataType: dataType
                                    ).save()
                DataElement dataElement = new DataElement(
                                    name: name
                                    ).save()

                dataElement.addToContainedIn(model)
                dataElement.addToInstantiatedBy(valueDomain)
                valueDomain.addToIncludedIn(conceptualDomain)

            }
        }

        println "-"*100
        println "Domain Models"
        println "-"*100
        println " "
        def models = Model.list()
        models.each{ model ->
            println "-"*100
            println "Model Name : ${model.name}"
            println "-"*100
            println "contexts"
            println "${model.hasContextOf[0].name}"
            println " -- "
            println "contains (DataElement ( ValueDomain - DataType ))"
            def dataElements = model.contains
            dataElements.each{ DataElement dataElement->
                def instantiatedBy = dataElement.instantiatedBy
                println " -- ${dataElement.name} - (${instantiatedBy[0].name}  - ${instantiatedBy[0].dataType.name} )"
            }

        }




    }



    private static findOrCreateConceptualDomain(String name, String description) {
        def cd = ConceptualDomain.findByName(name)
        if (!cd) {
            cd = new ConceptualDomain(name: name, description: description).save()
        }
        return cd
    }


    def domainConstraintsToRegex(constraints){
        //FIXME at present this only looks at the maxSize constraint
        //we may need to add an additional concept - constraint - then we can add constraints
        //to value domains. If we give constraints languages we can start to look at converting
        //different constraints into the model i.e. sql, oracle, groovy etc.
        String aToZ = '/^[a-z][a-z0-9]*(?:_[a-z0-9]+)*$/'
        def size  = constraints.find{it.getName()=="maxSize"}
        if(size){
            def to = size.constraintParameter
            aToZ += "{1,${to}}"
        }
        return  aToZ
    }

}
