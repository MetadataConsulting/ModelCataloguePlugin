package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsDomainClass

class DomainModellerService {

    static transactional = true

    def grailsApplication, initCatalogueService

    def modelDomains() {
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        def conceptualDomain = new ConceptualDomain(name:"uk.co.mc.core", description: "Model Catalogue Conceptual Domain").save()
        def domainClasses =  grailsApplication.domainClasses


//create models for all the domain classes first (so we can create relationships to them)
        domainClasses.each{ GrailsDomainClass domainClass ->

            Model model = new Model(
                    name: domainClass.shortName
            ).save()

            model.addToHasContextOf(conceptualDomain)

        }

//model all the domain class properties
        domainClasses.each{ GrailsDomainClass domainClass ->

            def constraints = domainClass.getConstrainedProperties()
            def properties = domainClass.getProperties()

            properties.each{ property ->
                String name = property.name
                Model model = Model.findByName(domainClass.shortName)
                def modelName = (domainClass.getRelatedClassType(name)) ? domainClass.getRelatedClassType(name).getSimpleName() : null

                 if(!domainClass.hasPersistentProperty(name)){

                     DataElement dataElement = new DataElement(
                             name: name
                     ).save()

                     linkTransients(dataElement, model)

                 }else if(domainClass.isBidirectional(name)){

                     RelationshipType manyToMany = RelationshipType.findByName("manyToMany")
                     if(!manyToMany){
                         manyToMany= new RelationshipType(destinationClass: Model, sourceClass: Model, destinationToSource: "many to many", sourceToDestination: "many to many", name: "manyToMany").save()
                     }
                     linkModels(modelName, manyToMany, model)


                }else if(domainClass.isManyToOne(name)){

                     RelationshipType manyToOne = RelationshipType.findByName("manyToOne")
                     if(!manyToOne){
                         manyToOne = new RelationshipType(destinationClass: Model, sourceClass: Model, sourceToDestination: "many to one", destinationToSource: "one to many", name: "manyToOne").save()
                     }
                     linkModels(modelName, manyToOne, model)

                }else if(domainClass.isOneToMany(name)){

                     RelationshipType oneToMany = RelationshipType.findByName("oneToMany")
                     if(!oneToMany){
                          oneToMany = new RelationshipType(destinationClass: Model, sourceClass: Model, destinationToSource: "many to one", sourceToDestination: "one to many", name: "oneToMany").save()
                     }
                     linkModels(modelName, oneToMany, model)


                }else {

                    DataType dataType = DataType.findByName(property.type.name)
                    if(!dataType){
                        dataType = new DataType(
                            name: property.type.name
                            ).save()
                    }

                    def constraint = (constraints.get(name))? constraints.get(name).appliedConstraints : null

                    ValueDomain valueDomain = new ValueDomain(
                                        name: name,
                                        dataType: dataType,
                                        description: formatDomainConstraints(constraint)
                                        ).save()
                    DataElement dataElement = new DataElement(
                                        name: name
                                        ).save()

                    dataElement.addToContainedIn(model)
                    dataElement.valueDomain = valueDomain
                    valueDomain.addToIncludedIn(conceptualDomain)
                }

            }
        }

        println "-"*100
        println "Domain Models"
        println "-"*100
        println " "
        def models = Model.list()

        models.each{ model ->

            def manyToMany = model.getRelationsByType(RelationshipType.findByName("manyToMany")).collect{it.name}
            def oneToMany = model.getRelationsByType(RelationshipType.findByName("oneToMany")).collect{it.name}
            def manyToOne = model.getRelationsByType(RelationshipType.findByName("manyToOne")).collect{it.name}
            def transients = model.getRelationsByType(RelationshipType.findByName("transients")).collect{it.name}

            println "-"*100
            println "Model Name : ${model.name}"
            println "-"*100
            println "contexts"
            println "--${model.hasContextOf[0]?.name}"
            if(manyToMany){
            println "has many (manyToMany)"
            println "--${manyToMany}"
            }
            if(oneToMany){
            println "has many (oneToMany)"
            println "--${oneToMany}"
            }
            if(manyToOne){
            println "belongs to (manyToOne)"
            println "--${manyToOne}"
            }
            if(transients){
                println "transients"
                println "--${transients}"
            }
            println "contains"
            def dataElements = model.contains
            dataElements.each{ DataElement dataElement->
                def instantiatedBy = dataElement.instantiatedBy
                println " -- ${dataElement.name} (${instantiatedBy[0].dataType.name}) (${instantiatedBy[0].description})"
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

    String formatDomainConstraints(constraints){

        Map formatted = [:]
        constraints.each{ constraint ->
            def propertyName = constraint.constraintPropertyName
            def constraintClass = constraint.getClass().getSimpleName()
            def constraintParameter
            String formattedProperty = formatted.get(propertyName)
            if(constraintClass=="ValidatorConstraint"){
                constraintParameter == "custom validator"
            }else if(constraintClass=="SizeConstraint"){
                constraintParameter = "[$constraint.constraintParameter.to ..  $constraint.constraintParameter.from]"
            }else{
                constraintParameter = constraint.constraintParameter
            }

            if(formattedProperty){
                formattedProperty += ", ${constraintClass} : ${constraintParameter}"
                formatted.put(propertyName, formattedProperty)
            }else{
                formatted.put(propertyName, "${constraintClass} : ${constraintParameter}")
            }
        }

        if(formatted.isEmpty()){
            return "no constraints"
        }

        return formatted.toString()

    }

    protected static void linkModels(modelName, manyToMany, model){
        if(modelName){
            Model relatedModel = Model.findByName(modelName)
            if(relatedModel){
                new RelationshipService().link(model, relatedModel, manyToMany)
            }
        }
    }


    protected static void linkTransients(element, model){

        RelationshipType transients = RelationshipType.findByName("transients")
        if(!transients){
            transients= new RelationshipType(destinationClass: DataElement, sourceClass: Model, destinationToSource: "transient properties of", sourceToDestination: "transient properties for", name: "transients").save()
        }
        if(element){
            new RelationshipService().link(model, element, transients)
        }

    }


    def domainConstraintsToRegex(constraints){
        //TODO at present this only looks at the maxSize constraint
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
