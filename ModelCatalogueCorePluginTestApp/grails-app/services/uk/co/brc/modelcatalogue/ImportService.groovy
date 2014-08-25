package uk.co.brc.modelcatalogue

import org.modelcatalogue.core.*

class ImportService {

    static transactional = true
    def grailsApplication
    def initCatalogueService

    private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    def importData() {
        initCatalogueService.initDefaultRelationshipTypes()
        initCatalogueService.initDefaultDataTypes()
        initCatalogueService.initDefaultMeasurementUnits()
        getNhicFiles().each { filename -> singleImport(filename) }
    }
//
//    private grantUserPermissions(objectOrList) {
//        if (objectOrList instanceof java.util.Collection) {
//            for (thing in objectOrList) {
//                grantUserPermissions(thing)
//            }
//        } else {
//            aclUtilService.addPermission objectOrList, 'ROLE_ADMIN', BasePermission.ADMINISTRATION
//            aclUtilService.addPermission objectOrList, 'ROLE_USER', BasePermission.READ
//        }
//    }
//
//    /**
//     * Get the list of available files for import
//     * @return the list of available files for import
//     */
    def getNhicFiles() {
        return fileFunctions.keySet()
    }
//
//    /**
//     * Carry out an import for a single file in the NHIC dataset
//     * @param filename The filename. Must exist in the collection returned by <code>getNhicFiles</code>
//     */
    def singleImport(String filename) {

        DataType.initDefaultDataTypes()
        initCatalogueService.initDefaultRelationshipTypes()
        def applicationContext = grailsApplication.mainContext
        String basePath = applicationContext.getResource("/").getFile().toString()

        new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
            fileFunctions[filename](tokens);
        }
    }

    private fileFunctions = [
            '/CAN_CUH.csv':
                    { tokens ->
                        def categories = ["NHIC Datasets", "Ovarian Cancer", "CUH", "Round 1", tokens[1], tokens[2]]
                        def cd = findOrCreateConceptualDomain("NHIC", "NHIC conceptual domain i.e. value domains used the NHIC project")
                        def models = importModels(categories, cd)
                        def dataTypes = [tokens[5]]
                        def dataType = importDataTypes(tokens[3], dataTypes)
                        def ext = new HashMap()

                        def vd = new ValueDomain(name: tokens[3].replaceAll("\\s", "_"),
                                //conceptualDomain: cd,
                                dataType: dataType,
                                description: tokens[5]).save(failOnError: true);

                        vd.addToConceptualDomains(cd)

                        def de = new DataElement(name: tokens[3],
                                description: tokens[4], code: tokens[0])
                        //dataElementConcept: models,
                        //extension: ext).save(failOnError: true)

                        de.valueDomain = vd
                        de.save()

                        de.ext.put("NHIC_Identifier:", tokens[0].take(255));
                        de.ext.put("Link_to_existing definition:", tokens[6].take(255));
                        de.ext.put("Notes_from_GD_JCIS", tokens[7].take(255));
                        de.ext.put("[Optional]_Local_Identifier", tokens[8].take(255));
                        de.ext.put("A", tokens[9].take(255));
                        de.ext.put("B", tokens[10].take(255));
                        de.ext.put("C", tokens[11].take(255));
                        de.ext.put("D", tokens[12].take(255));
                        de.ext.put("E", tokens[13].take(255));
                        de.ext.put("F", tokens[14].take(255));
                        de.ext.put("G", tokens[15].take(255));
                        de.ext.put("H", tokens[16]);
                        de.ext.put("E2", tokens[17].take(255))


                        vd.addToDataElements(de)
                        de.addToContainedIn(models)

                        //de.addToDataElementValueDomains(vd);
                        //de.save();
                        println "importing: " + tokens[0] + "_Round1_CAN"
                    }
    ]


    private static importModels(categories, ConceptualDomain conceptualDomain) {
        //categories look something like ["Animals", "Mammals", "Dogs"]
        //where animal is a parent of mammals which is a parent of dogs......

        def modelToReturn

        categories.inject { parentName, childName ->

            //if there isn't a name for the child return the parentName
            if (childName.equals("")) {
                return parentName;
            }

            //def matches = Model.findAllWhere("name" : name, "parentName" : models)

            //see if there are any models with this name
            Model match
            def namedChildren = Model.findAllWhere("name": childName)

            //see if there are any models with this name that have the same parentName
            if (namedChildren.size()>0) {
                namedChildren.each{ Model childModel ->
                    if(childModel.childOf.collect{it.name}.contains(parentName)){
                        match = childModel
                    }
                }
            }

            //if there isn't a matching model with the same name and parentName
            if (!match) {
                //new Model('name': name, 'parentName': parentName).save()
                Model child
                Model parent

                //create the child model
                child = new Model('name': childName).save()
                child.addToHasContextOf(conceptualDomain)

                modelToReturn = child

                //see if the parent model exists
                parent = Model.findWhere("name": parentName)

                //FIXME we should probably have unique names for models (or codes)
                // or at least within conceptual domains
                // or we need to have a way of choosing the model parent to use
                // at the moment it just uses the first one Model that is returned

                if (!parent) {
                    parent = new Model('name': parentName).save()
                    parent.addToHasContextOf(conceptualDomain)
                }

                child.addToChildOf(parent)

                child.name

                //add the parent child relationship between models

            } else {
                modelToReturn = match
                match.name
            }
        }

        modelToReturn
    }

    private static importDataTypes(name, dataType) {

        //default data type to return is the string data type
        def dataTypeReturn

        dataType.each { line ->

            String[] lines = line.split("\\r?\\n");

            def enumerated = false

            if (lines.size() > 0 && lines[] != null) {

                Map enumerations = new HashMap()

                lines.each { enumeratedValues ->

                    def EV = enumeratedValues.split(":")

                    if (EV != null && EV.size() > 1 && EV[0] != null && EV[1] != null) {
                        def key = EV[0]
                        def value = EV[1]

                        if (value.size() > 244) {
                            value = value[0..244]
                        }

                        key = key.trim()
                        value = value.trim()

                        enumerated = true
                        enumerations.put(key, value)
                    }
                }

                if (enumerated) {


                    String enumString = enumerations.sort() collect { key, val ->
                        "${this.quote(key)}:${this.quote(val)}"
                    }.join('|')

                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumString)

                    if (!dataTypeReturn) {
                        dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                    }
                } else {

                    dataTypeReturn = (DataType.findByName(name)) ?: DataType.findByName("String")

                }
            } else {
                dataTypeReturn = DataType.findByName("String")
            }
        }
        return dataTypeReturn
    }

    private static findOrCreateConceptualDomain(String name, String description) {
        def cd = ConceptualDomain.findByName(name)
        if (!cd) {
            cd = new ConceptualDomain(name: name, description: description).save()
        }
        return cd
    }

    private static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }
}