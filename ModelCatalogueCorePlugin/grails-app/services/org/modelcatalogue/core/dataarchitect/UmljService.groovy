package org.modelcatalogue.core.dataarchitect

/**
 * James Welch, A.Milward
 */


import groovy.json.JsonSlurper
import org.modelcatalogue.core.*


class UmljService {

    def modelCatalogueSearchService

    static transactional = false


    public void importUmlDiagram(InputStream is, String name, Classification classification) {

        def allClasses, topLevelClasses, allDataTypes, allEnumerations;

        log.info "Parsing Umlj file for ${name}"
        def slurper = new JsonSlurper()
        def result  = slurper.parse(new BufferedReader(new InputStreamReader(is)))
        def umlFile = new StarUMLDiagram(result)
        generateCatalogueElements(umlFile, classification)
    }


    protected void generateCatalogueElements(umlFile, classification) {
        Model topLevelModel = new Model(name: classification.name).save()
        umlFile.topLevelClasses.each { id, cls ->
            def model = createModels(cls, umlFile, classification, topLevelModel)
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

    protected ValueDomain findOrCreateValueDomain(att, umlFile, classification){
        if(!(att.type instanceof String) && att.type?.$ref && umlFile.allDataTypes.get(att?.type?.$ref)){
            // Find highest supertype
            def currType = umlFile.allDataTypes.get(att.type?.$ref)
            while(currType.ownedElements?.findAll({oe -> oe._type.equals("UMLGeneralization")})!= null)
            {
                currType = umlFile.allDataTypes.get(currType.ownedElements.findAll({oe -> oe._type.equals("UMLGeneralization")}).get(0).target?.$ref)
            }
            def dataType = DataType.findByNameIlike(currType.name.toString())
            if(!dataType) dataType = new DataType(name: currType.name.toString()).save()
            dataType.addToClassifications(classification)
            def valueDomain = ValueDomain.findByDataType(dataType)
            if(!valueDomain) valueDomain = new ValueDomain(name: dataType.name, dataType: dataType).save()
            valueDomain.addToClassifications(classification)
            return valueDomain
        }


        if(!(att.type instanceof String) && att.type?.$ref && umlFile.allEnumerations.get(att.type?.$ref)){
            def enumeration = umlFile.allEnumerations.get(att.type?.$ref)
            def enumMap = [:]
            enumeration.literals.each{ ev ->
                enumMap.put(ev.name, ev.documentation)
            }
            def enumString = mapToString(enumMap)
            def dataType = EnumeratedType.findByEnumAsStringAndName(enumString, enumeration.name)
            if(!dataType) dataType = new EnumeratedType(name: enumeration.name, enumerations: enumMap).save()
            dataType.addToClassifications(classification)

            def valueDomain = ValueDomain.findByDataType(dataType)
            if(!valueDomain) valueDomain = new ValueDomain(name: enumeration.name, dataType: dataType).save()
            valueDomain.addToClassifications(classification)
            return valueDomain
        } else if(att.type instanceof String){
            if(att.type=="") att.type="string"
            def dataType = DataType.findByNameIlike(att.type)
            if(!dataType) dataType = new DataType(name: att.type).save()
            def valueDomain = ValueDomain.findByDataType(dataType)
            if(!valueDomain) valueDomain = new ValueDomain(name: dataType.name, dataType: dataType).save()
            valueDomain.addToClassifications(classification)
            return valueDomain

        }
    }

    protected void addAttributeToModel(att, Model model, umlFile, classification){

        def multiplicity = getMultiplicity(att)
        def valueDomain = findOrCreateValueDomain(att, umlFile, classification)
        def dataElement = DataElement.findByNameAndValueDomain(att.name.replaceAll("_", " "), valueDomain)
        if(!dataElement) dataElement = new DataElement(name: att.name.replaceAll("_", " "), description: att.documentation, valueDomain: valueDomain).save()
        dataElement.addToClassifications(classification)
        if(att.tags?.value) dataElement.ext.put("cosd id", att.tags?.value[0])
        def rel = model.addToContains(dataElement)
        if(multiplicity["minRepeat"]) rel.ext.put("Min Occurs", multiplicity["minRepeat"])
        if(multiplicity["maxRepeat"]) rel.ext.put("Max Occurs", multiplicity["maxRepeat"])

    }

    protected createModels(cls, umlFile, classification, topLevelModel, ArrayList<Object> carried_forward_atts = new ArrayList<Object>(), ArrayList<Object> carried_forward_comps = new ArrayList<Object>()) {
        def model = findModel(classification, cls.name.replaceAll("_", " "))
        def cfa = getAttributes(cls, carried_forward_atts)
        def cfc = getComponents(cls, carried_forward_comps, umlFile)
        if (!cls.isAbstract) {
//      Model.findByName(name: cls.name.replaceAll("_", " "))
            if(!model) model = new Model(name: cls.name.replaceAll("_", " "), description: cls.documentation).save()
            model.addToClassifications(classification)
            model.addToChildOf(topLevelModel)
            println("Outputting model: " + cls.name)
            if(cls.name=="Patient"){
                println("test")
            }

            // first output the attributes for this class
            cfa.each {
                att -> addAttributeToModel(att, model, umlFile, classification)
            }
            println("No. of components: " + cfc.size())
            cfc.each {
                component ->
                    def comp = createModels(component, umlFile, classification, model, new ArrayList<Object>(), new ArrayList<Object>())
            }

        } else {
            println("Abstract class: " + cls.name);
        }

        // then output the sections corresponding to any subtypes
        def subtypes = getSubTypes(cls, umlFile)
        subtypes.each { subtype ->
            createModels(subtype.value, umlFile, classification, topLevelModel, cfa, cfc)
        }

        return model

    }

    protected getSubTypes(cls, umlFile){
        return umlFile.allClasses.findAll{
            id, subtype -> subtype.ownedElements.findAll{
                oe ->
                    oe._type?.equals("UMLGeneralization") && oe?.target?.$ref.equals(cls._id)
            }.size() > 0
        }
    }

    /**
     * Returns existing classification or new one with given name and namespace.
     * @param name the name of the newly created classification
     * @param namespace the namespace of the newly created classification
     * @return existing classification or new one with given name and namespace
     */
    private Classification findOrCreateClassification(String name, String namespace) {
        Classification classification = Classification.findByNamespace(namespace)
        if (classification) {
            return classification
        }
        new Classification(name: name, namespace: namespace).save(failOnError: true)
    }

    private static String mapToString(Map<String, String> map) {
        if (map == null) return null
        map.sort() collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')
    }


    private findModel(Classification classification, String query){
        List<Classification> classifications = [classification]

        String alias = Model.simpleName[0].toLowerCase()

        Map<String, Object> arguments = [
                query: query,
                statuses: [ElementStatus.DRAFT, ElementStatus.PENDING, ElementStatus.UPDATED, ElementStatus.FINALIZED],
                classifications: classifications,
                classificationType: RelationshipType.classificationType
        ]

        String listQuery = """
            from ${Model.simpleName} ${alias} join ${alias}.incomingRelationships as rel
            where
                ${alias}.status in :statuses
                and (
                    lower(${alias}.name) = lower(:query)
                )
                and rel.source in (:classifications)
                and rel.relationshipType = :classificationType
            """


        def results = Model.executeQuery(listQuery, arguments)
        if(results) results = results[0][0]
        return results
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