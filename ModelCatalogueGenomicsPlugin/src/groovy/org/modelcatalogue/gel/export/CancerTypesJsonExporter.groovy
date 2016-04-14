package org.modelcatalogue.gel.export

import groovy.json.JsonBuilder
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement

/**
 * Created by rickrees on 31/03/2016.
 *
 */
@Log4j
class CancerTypesJsonExporter {

    public static final String CANCER_TYPES = 'CancerTypes'
    public static final String SUB_TYPE = 'subType'
    public static final String SUB_TYPES = 'subTypes'
    public static final String PRESENTATION = 'presentation'
    public static final String PRESENTATIONS = 'presentations'
    public static final String ID_TAG = 'id'
    public static final String TYPE_TAG = 'type'
    public static final String DESCRIPTION_TAG = 'description'
    private final OutputStream out

    CancerTypesJsonExporter(OutputStream out) {
        this.out = out
    }


    void exportCancerTypesAsJson(model) {
        int depth = 3
        int level = 1
        def graphList = []
        def builder = new JsonBuilder()

        log.info "Exporting CancerTypes as json ${model.name} (${model.combinedVersion})"

        descendModels(model, level, graphList, depth,'')

        builder "$CANCER_TYPES": graphList
        out << builder.toPrettyString()
    }


    def descendModels(CatalogueElement model, level, graphList, depth, String subTag) {
        if (level > depth) return

        log.debug "descendModels level=$level graghList=$graphList model=$model"

        def modelList = []
        def map = [:]


        switch (level) {
            case 1:
                modelList = graphList
                iterateAndDescendModels(model, level, modelList, depth, '')
                return
            case 2:
                map.put(ID_TAG, "$model.id")
                map.put(TYPE_TAG, model.name)

                def presentationList = []
                map.put(SUB_TYPES, modelList)
                map.put(PRESENTATIONS, presentationList)

                iterateAndDescendModels(model, level, modelList, depth, SUB_TYPE)
                iterateAndDescendModels(model, level, presentationList, depth, PRESENTATION)

                graphList << map
                return
            case 3:
                map.put(subTag, model.name)
                map.put(DESCRIPTION_TAG, model.description ?: '')

                graphList << map
                return
            default:
                return
        }

    }

    private void iterateAndDescendModels(CatalogueElement model, level, modelList, depth, String includePattern) {
        //don't recurse dataElements
        if (model instanceof DataElement) return

        model.contains.each { CatalogueElement child ->
            if (child.name.matches("(?i:.*$includePattern.*)")) {
                descendModels(child, level + 1, modelList, depth, includePattern)
            }
        }
        model.parentOf?.eachWithIndex { CatalogueElement child, i ->
            if (child.name.matches("(?i:.*$includePattern.*)")) {
                descendModels(child, level + 1, modelList, depth, includePattern)
            }
        }
    }

}









