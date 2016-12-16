package org.modelcatalogue.gel.export

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement

/**
 * Created by rickrees on 31/03/2016.
 *
 */
@Log4j
class RareDiseasesJsonExporter {

    public static final int LEVEL1 = 1
    private final OutputStream out
    private Integer modelCount = 0

    RareDiseasesJsonExporter(OutputStream out) {
        this.out = out
    }


    void exportEligibilityCriteriaAsJson(CatalogueElement model) {
        int depth = 6
        def exclusions = ['Phenotype','Clinical Test','Closing Statement','Guidance']

        log.info "Exporting Rare Disease Eligibility Criteria as json ${model.name} (${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getModelCatalogueId() ?: model.getLatestVersionId() ?: model.getId()})"

        //define the json tagnames to use for each level in the model
        def levelTag1 = [tag1: 'DiseaseGroups']
        def levelTag2 = [tag1: 'id', tag2: 'name', tag3: 'subGroups']
        def levelTag3 = [tag1: 'id', tag2: 'name', tag3: 'specificDisorders']
        def levelTag4 = [tag1: 'id', tag2: 'name', tag3: 'disorderCriteria']
        def levelTag5 = [tag1: 'id', tag2: 'name', tag3: 'eligibilityCriteria']
        def levelTag6 = [tag1: 'id', tag2: 'name', tag3: 'description']
        def levelMetaData = [1: levelTag1, 2: levelTag2, 3: levelTag3, 4: levelTag4, 5: levelTag5, 6: levelTag6]

        exportJson(model, depth, levelMetaData, exclusions)

        log.info "Rare Disease Eligibility Criteria exported as json ${model.name} (${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getModelCatalogueId() ?: model.getLatestVersionId() ?: model.getId()}) ."
    }

    void exportDiseaseListOnly(DataClass model) {
        Integer level = 1
        def builder = new JsonBuilder()
        Map<String, String> groupDescriptions = new HashMap<>()
        def lines = []
        descendDiseaseOnlyModels(model, lines, level, groupDescriptions)

        builder "diseaseList" : lines

        out << builder.toPrettyString()
    }

    def descendDiseaseOnlyModels(DataClass model, lines, level, groupDescriptions) {
        def line = []
        def map = [:]
        def splitDiseaseGroups

        //strip then re-add surrounding quotes to ensure only one set surround the name
        //and to ensure names with commas are treated as a single column
        String modelName = model.name.replaceAll('^\"|\"$', '')

        switch (level) {
            case 1:     //ignore top Rare Diseases level
                break

            case 2:     // add disease group id,description
                String groupDescription = "${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 3: // add disease sub-group id,description
                String groupDescription = "${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 4: // generate line and add to list

                map.put("id", model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getModelCatalogueId() ?: model.getLatestVersionId() ?: model.getId())
                map.put("diseaseGroup",  groupDescriptions.get(level - 2))
                map.put("diseaseSubgroup",  groupDescriptions.get(level - 1))
                map.put("diseaseName", modelName)
                lines << map
                return  //don't go deeper

            default:    //don't go deeper
                return
        }


        model.parentOf?.each { DataClass child ->
            descendDiseaseOnlyModels(child, lines, level + 1, groupDescriptions)
        }
    }




    private void exportJson(model, depth, levelMetaData, exclusions) {
        int level = 1
        def graphList = []
        def builder = new JsonBuilder()

        log.info "depth $depth exclusions $exclusions \n levelMetaData $levelMetaData"

        descendModels(model, level, graphList, depth, levelMetaData, exclusions)

        builder "${levelMetaData.get(LEVEL1).tag1}": graphList

        out << builder.toPrettyString()
    }


    def descendModels(CatalogueElement model, level, graphList, depth, Map levelMetaData, exclusions) {
        if (level > depth) return

        log.debug "descendModels level=$level model=$model"

        def modelList = []
        def map = [:]


        switch (level) {
            case 1:
                modelList = graphList
                break

            case [2,3,4,5]:
                if(level==2) modelCount = modelCount + 1
                // resolves to something concrete like - map.put('id', "$model.id")
                map.put(levelMetaData.get(level).tag1, model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getModelCatalogueId() ?: model.getLatestVersionId() ?: model.getId())
                map.put(levelMetaData.get(level).tag2, model.name)
                map.put(levelMetaData.get(level).tag3, modelList)

                graphList << map
                break

            case 6:
                map.put(levelMetaData.get(level).tag1, model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getModelCatalogueId() ?: model.getLatestVersionId() ?: model.getId())
                map.put(levelMetaData.get(level).tag2, model.name)
                map.put(levelMetaData.get(level).tag3, model.description ?: '')

                graphList << map

            default:    //don't go deeper
                return
        }


        //don't recurse dataElements
        if(model instanceof DataElement) return

        model.contains.eachWithIndex { CatalogueElement child, i ->
            recurseIfIncluded(i, child, level, modelList, depth, levelMetaData, exclusions)
        }
        model.parentOf?.eachWithIndex { CatalogueElement child, i ->
            recurseIfIncluded(i, child, level, modelList, depth, levelMetaData, exclusions)
        }

    }

    private void recurseIfIncluded(i, CatalogueElement child, level, modelList, depth, levelMetaData, exclusions) {
        def include = true
        exclusions.each { pattern ->
            if (child.name.matches("(?i:.*$pattern.*)")) {
                include = false
            }
        }
        if (include) {
            descendModels(child, level + 1, modelList, depth, levelMetaData, exclusions)
        }
    }

}









