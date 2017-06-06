package org.modelcatalogue.gel

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass

@Log4j
class GelJsonExporter {

    public static final int LEVEL1 = 1
    private final OutputStream out
    Integer modelCount = 0;

    GelJsonExporter(OutputStream out) {
        this.out = out
    }

    void printDiseaseOntology(CatalogueElement model) {
        int depth = 4
        int level = 1
        def graphList = []
        def builder = new JsonBuilder()
        def exclusions = ['Eligibility', 'Guidance']

        log.info "Exporting Rare Disease HPO Phenotypes as json ${model.name} (${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:''})"

        //define the json tagnames to use for each level in the model
        def levelTag1 = [tag1: 'DiseaseGroups']
        def levelTag2 = [tag1: 'id', tag2: 'name', tag3: 'subGroups']
        def levelTag3 = [tag1: 'id', tag2: 'name', tag3: 'specificDisorders']
        def levelTag4 = [tag1: 'id', tag2: 'name', tag3: 'eligibilityQuestion']
        def levelTag5 = [tag1: 'id', tag2: 'name', tag3: 'shallowPhenotypes']
        def levelTag6 = [tag1: 'id', tag2: 'name']
        def levelMetaData = [1: levelTag1, 2: levelTag2, 3: levelTag3, 4: levelTag4, 5: levelTag5, 6: levelTag6]

        descendModels(model, level, graphList, depth, levelMetaData, exclusions)
        builder "${levelMetaData.get(LEVEL1).tag1}": graphList
        out << builder.toPrettyString()

        log.info "Rare Disease HPO Phenotypes exported as json ${model.name} (${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:''}) ."
    }


    def descendModels(CatalogueElement model, level, graphList, depth, Map levelMetaData, exclusions) {
        if (level > depth) return
        //TODO - comment out for prod
//        if (dataClassCount > 2) return

        log.debug "descendModels level=$level graghList=$graphList model=$model"

        def modelList = []
        def map = [:]


        if (level == 1) {
            modelList = graphList
        }
        if (level == 2) {
            modelCount = modelCount + 1
            // resolves to something concrete like - higherMap.put('id', "$model.id")
            map.put(levelMetaData.get(level).tag1, model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:model.getCombinedVersion())
            map.put(levelMetaData.get(level).tag2, model.name)
            map.put(levelMetaData.get(level).tag3, modelList)

            graphList << map
        }
        if (level == 3) {
            map.put(levelMetaData.get(level).tag1, model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:model.getCombinedVersion())
            map.put(levelMetaData.get(level).tag2, model.name)
            map.put(levelMetaData.get(level).tag3, modelList)

            graphList << map
        }
        if (level == 4) {
            map.put(levelMetaData.get(level).tag1, model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id')?:model.getCombinedVersion())
            map.put(levelMetaData.get(level).tag2, model.name)

            def miniMap = [:]
            miniMap.put('date',model.lastUpdated.format("yyyy-MM-dd"))
            miniMap.put('version',"$model.versionNumber")
            map.put(levelMetaData.get(level).tag3, miniMap)

            addPhenotypesAndTests(model, levelMetaData, map, modelList)

            graphList << map
            return
        }

        model.parentOf?.eachWithIndex { CatalogueElement child, i ->
            descendModels(child, level + 1, modelList, depth, levelMetaData, exclusions)
        }

    }

    private void addPhenotypesAndTests(CatalogueElement model, Map levelMetaData, LinkedHashMap map, ArrayList modelList) {
        def phenotypeModel, testModel

        model.parentOf.each { DataClass dataClass ->

            if (dataClass.name.matches("(?i:.*Phenotypes.*)") && !dataClass.name.matches("(?i:.*Eligibility.*)") && !dataClass.name.matches("(?i:.*Tests.*)") && !dataClass.name.matches("(?i:.*Guidance.*)")) {
                phenotypeModel = dataClass
            }
            if (!dataClass.name.matches("(?i:.*Phenotypes.*)") && !dataClass.name.matches("(?i:.*Eligibility.*)") && dataClass.name.matches("(?i:.*Tests.*)") && !dataClass.name.matches("(?i:.*Guidance.*)")) {
                testModel = dataClass
            }
        }

        if (phenotypeModel) {
            addPhenotypeDetail(phenotypeModel, levelMetaData, map, true)
        } else {
            map.put("shallowPhenotypes", [])
        }
        if (testModel) {
            addPhenotypeDetail(testModel, levelMetaData, map, false)
        } else {
            map.put("tests", [])
        }
    }


    private void addPhenotypeDetail(DataClass model, Map levelMetaData, Map higherMap, boolean isPhenotype) {
        def list = []

        if (isPhenotype) {
            higherMap.put("shallowPhenotypes", list)
        } else {
            higherMap.put("tests", list)
        }

        model.parentOf.each { DataClass child ->
            def map = [:]
            if(child.ext.get("Investigation Type")!="Specialised") {
                map.put(levelMetaData.get(6).tag2, child.name)


                def id
                if (isPhenotype) {
                    id = child.ext.get("OBO ID") ?: ""
                } else { // tests
                    id = child.ext.get("http://www.modelcatalogue.org/metadata/genomics/#gel-test-id-versioned") ?: child.getCombinedVersion()
                }
                map.put(levelMetaData.get(6).tag1, id)
                list << map
            }
        }
    }

}









