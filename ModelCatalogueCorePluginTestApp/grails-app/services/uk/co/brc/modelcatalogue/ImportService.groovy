package uk.co.brc.modelcatalogue

import uk.co.mc.core.*

import javax.xml.crypto.Data

class ImportService {

    static transactional = true
    def grailsApplication

    def importData() {

        DataType.initDefaultDataTypes()

        def applicationContext = grailsApplication.mainContext
        String basePath = applicationContext.getResource("/").getFile().toString()

        functions.keySet().each { filename ->
            new File("${basePath}" + "/WEB-INF/bootstrap-data" + filename).toCsvReader([charset: 'UTF-8', skipLines: 1]).eachLine { tokens ->
                functions[filename](tokens);
            }
        }
    }


    private static functions = [

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

                        vd.addToIncludedIn(cd)

                        def de = new DataElement(name: tokens[3],
                                description: tokens[4], code: tokens[0])
                        //dataElementConcept: models,
                        //extension: ext).save(failOnError: true)

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


                        de.addToInstantiatedBy(vd)

                        //de.addToDataElementValueDomains(vd);
                        //de.save();
                        println "importing: " + tokens[0] + "_Round1_CAN"
                    }
    ]


    private static importModels(categories, ConceptualDomain conceptualDomain) {
        //categories look something like ["Animals", "Mammals", "Dogs"]
        //where animal is a parent of mammals which is a parent of dogs......

        categories.inject { parentName, childName ->

            //if there isn't a name for the child return the parentName
            if (childName.equals("")) {
                return parentName;
            }

            //def matches = Model.findAllWhere("name" : name, "parentName" : models)

            //see if there are any models with this name
            def matches
            def namedChildren = Model.findAllWhere("name": childName)

            //see if there are any models with this name that have the same parentName
            if (namedChildren) {
                matches = namedChildren.childOf.contains(parentName)
            }

            //if there isn't a matching model with the same name and parentName
            if (!matches) {
                //new Model('name': name, 'parentName': parentName).save()
                def child
                def parent

                //create the child model
                child = new Model('name': childName).save()
                child.addToHasContextOf(conceptualDomain)

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
                matches.first();
            }
        }
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

                    //FIXME we need to query the enumerated types to ensure we don't include duplicate enumerated sets
                    dataTypeReturn = EnumeratedType.findWhere(enumAsString: enumerations.toString())

                    if(!dataTypeReturn){
                     dataTypeReturn = new EnumeratedType(name: name.replaceAll("\\s", "_"), enumerations: enumerations).save()
                    }
                }else{

                    dataTypeReturn = (DataType.findByName(name))?:DataType.findByName("String")

                }
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
}