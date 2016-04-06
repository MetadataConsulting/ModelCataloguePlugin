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
class CancerTypesExporter {

    public static final int LEVEL1 = 1
    private final OutputStream out

    CancerTypesExporter(OutputStream out) {
        this.out = out
    }


    void exportCancerTypesAsJson(model) {
        int depth = 3
        def exclusions = ['Presentations']

        log.info "Exporting CancerTypes as json ${model.name} (${model.combinedVersion})"

        //define the json tagnames to use for each level in the model
        def levelTag1 = [tag1: 'CancerTypes']
        def levelTag2 = [tag1: 'id', tag2: 'type', tag3: 'subTypes']
        def levelTag3 = [tag1: 'subType', tag2: 'description']
        def levelMetaData = [1: levelTag1, 2: levelTag2, 3: levelTag3]

        exportJson(model, depth, levelMetaData, exclusions)
    }

    //this is best guess at the moment...
    void exportPresentationTypesAsJson(model) {
        int depth = 3
        def exclusions = ['Subtypes']

        log.info "Exporting PresentationTypes as json ${model.name} (${model.combinedVersion})"

        def levelTag1 = [tag1: 'CancerTypes']
        def levelTag2 = [tag1: 'id', tag2: 'type', tag3: 'presentations']
        def levelTag3 = [tag1: 'presentation', tag2: 'description']
        def levelMetaData = [1: levelTag1, 2: levelTag2, 3: levelTag3]

        exportJson(model, depth, levelMetaData, exclusions)
    }


    private void exportJson(model, depth, levelMetaData, exclusions) {
        int level = 1
        def graphList = []
        def builder = new JsonBuilder()

        log.info "depth $depth exclusions $exclusions \n levelMetaData $levelMetaData"

        descendModels(model, level, graphList, depth, levelMetaData, exclusions)

        builder "${levelMetaData.get(LEVEL1).tag1}": graphList

        out << builder.toString()
    }


    def descendModels(CatalogueElement model, level, graphList, depth, Map levelMetaData, exclusions) {
        if (level > depth) return

        log.debug "descendModels level=$level graghList=$graphList model=$model"

        def modelList = []
        def map = [:]


        if (level == 1) {
            modelList = graphList
        }
        if (level == 2) {
            // resolves to something concrete like - map.put('id', "$model.id")
            map.put(levelMetaData.get(level).tag1, "$model.id")
            map.put(levelMetaData.get(level).tag2, model.name)
            map.put(levelMetaData.get(level).tag3, modelList)

            graphList << map
        }
        if (level == 3) {
            map.put(levelMetaData.get(level).tag1, model.name)
            map.put(levelMetaData.get(level).tag2, model.description)

            graphList << map
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









