package org.modelcatalogue.core.dataarchitect

import groovy.json.JsonSlurper

/**
 * James Welch, A.Milward
 */
import groovy.json.internal.LazyMap
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.util.builder.CatalogueBuilder

class UmljService {

    def classificationService, elementService

    static transactional = false

    protected void importUmlDiagram(InputStream is, String name, Classification classification) {

        def allClasses, topLevelClasses, allDataTypes, allEnumerations;

        log.info "Parsing Umlj file for ${name}"
        def slurper = new JsonSlurper()
        def result  = slurper.parse(new BufferedReader(new InputStreamReader(is)))
        def umlFile = new StarUMLDiagram(result)
        generateCatalogueElements(umlFile, classification)
    }


    protected void generateCatalogueElements(StarUMLDiagram umlFile, Classification clsf) {

        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)
        builder.build {
            classification(name: clsf.name) {
                globalSearchFor dataType
                model(name: clsf.name){
                    umlFile.topLevelClasses.each { String id, LazyMap cls ->
                        createClasses(builder, cls, umlFile)
                    }
                }
            }
        }

    }


    static createValueDomain(CatalogueBuilder builder, LazyMap att, StarUMLDiagram umlFile) {

            if (!(att.type instanceof String) && att.type?.$ref && umlFile.allDataTypes.get(att?.type?.$ref)) {
                // Find highest supertype
                def currType = umlFile.allDataTypes.get(att.type?.$ref)
                while (currType.ownedElements?.findAll({ oe -> oe._type.equals("UMLGeneralization") }) != null) {
                    currType = umlFile.allDataTypes.get(currType.ownedElements.findAll({ oe -> oe._type.equals("UMLGeneralization") }).get(0).target?.$ref)
                }

                return builder.valueDomain(name: currType.name.toString()) {
                        dataType(name: currType.name.toString())
                    }

            }

            if (!(att.type instanceof String) && att.type?.$ref && umlFile.allEnumerations.get(att.type?.$ref)) {
                def enumeration = umlFile.allEnumerations.get(att.type?.$ref)
                def enumMap = [:]
                enumeration.literals.each { ev ->
                    enumMap.put(ev.name, ev.documentation)
                }

                return builder.valueDomain(name: enumeration.name) {
                    dataType(name: enumeration.name, enumerations: enumMap)
                }

            } else if (att.type instanceof String) {
                if (att.type == "") att.type = "xs:string"

                return builder.valueDomain(name: att.type) {
                        dataType(name: att.type)
                    }
        }
    }



    protected createClasses(CatalogueBuilder builder, LazyMap cls, StarUMLDiagram umlFile, ArrayList<Object> carried_forward_atts = new ArrayList<Object>(), ArrayList<Object> carried_forward_comps = new ArrayList<Object>()) {

        println("Outputting model: " + cls.name)

        def cfa = getAttributes(cls, carried_forward_atts)
        def cfc = getComponents(cls, carried_forward_comps, umlFile)
        def subtypes = getSubTypes(cls, umlFile)
        if (!cls.isAbstract) {
            builder.model(name: cls.name.replaceAll("_", " "), description: cls.documentation) {
                    // first output the attributes for this class
                    cfa.each { att ->
                       def multiplicity = getMultiplicity(att)

                        dataElement(name: att.name.replaceAll("_", " "), description: att.documentation) {

                            if(att.tags?.value) ext("cosd id", att.tags?.value[0])
                            if(multiplicity.size()>0){
                                relationship {
                                    if (multiplicity["minRepeat"]) ext("Min Occurs", multiplicity["minRepeat"])
                                    if (multiplicity["maxRepeat"]) ext("Max Occurs", multiplicity["maxRepeat"])
                                }
                            }

                            createValueDomain(builder, att, umlFile)

                        }


                    }
                    println("No. of components: " + cfc.size())
                    cfc.each { component ->
                            createClasses(builder, component, umlFile)
                    }
                }

        } else {
            println("Abstract class: " + cls.name);
        }

        subtypes.each { subtype ->
            createClasses(builder, subtype.value, umlFile, cfa, cfc)
        }

    }




    protected getAttributes(cls, carried_forward_atts){
        def cfa = new ArrayList<Object>()
        cfa.addAll(carried_forward_atts)
        if(cls.attributes)cfa.addAll(cls.attributes)
        return cfa
    }

    protected getComponents(cls, carried_forward_comps, umlFile){
        def cfc = new ArrayList<Object>()
        cfc.addAll(carried_forward_comps)
        def components = umlFile.allClasses.findAll{
            id, component -> component.ownedElements.findAll{
                ct ->
                    ct._type.equals("UMLAssociation") && ct.end2.aggregation.equals("composite") && ct.end2.reference?.$ref.equals(cls._id)
            }.size() > 0

        }

        if(components) components.each { id, component -> cfc.add(component) }

        return cfc

    }

    protected getMultiplicity(att){

        def multiplicity = [:]

        switch (att.multiplicity){
            case "1":
                multiplicity["minRepeat"] = "1";
                multiplicity["maxRepeat"] = "1";
                break;
            case "0..1":
                multiplicity["minRepeat"] = "0";
                multiplicity["maxRepeat"] = "1";
                break;
            case "0..*":
                multiplicity["minRepeat"] = "0";
                multiplicity["maxRepeat"] = "unbounded";
                break;
            case "1..*":
                multiplicity["minRepeat"] = "1";
                multiplicity["maxRepeat"] = "unbounded";
                break;

            default:
                println("unknown multiplicity: " + att.multiplicity);

        }

        return multiplicity

    }



    protected getSubTypes(cls, umlFile){
        return umlFile.allClasses.findAll{
            id, subtype -> subtype.ownedElements.findAll{
                oe ->
                    oe._type?.equals("UMLGeneralization") && oe?.target?.$ref.equals(cls._id)
            }.size() > 0
        }
    }


    static String quote(String s) {
        if (s == null) return null
        String ret = s
        EnumeratedType.QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }


}