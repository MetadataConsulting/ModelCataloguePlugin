package org.modelcatalogue.gel.export

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement

/**
 * Created by rickrees on 31/03/2016.
 *
 */
@Log4j
class CancerTypesCsvExporter {

    public static final int LEVEL1 = 1
    static final String cancerTypesHeader = "id,Cancer Types,id,Cancer SubTypes"
    static final String presentationTypesHeader = "id,Cancer Types,id,Cancer Presentations"

    private final OutputStream out

    CancerTypesCsvExporter(OutputStream out) {
        this.out = out
    }


    void exportCancerTypesAsCsv(model) {
        int level = 1
        def lines = []
        def exclusions = ['Presentations']
        Map<String, String> groupDescriptions = new HashMap<>()

        log.info "Exporting CancerTypes as csv ${model.name} (${model.combinedVersion})"

        lines << cancerTypesHeader
        descendModelsCsv(model, lines, level, groupDescriptions, exclusions)
        out << lines.join('\n')
    }

    void exportPresentationTypesAsCsv(model) {
        int level = 1
        def lines = []
        def exclusions = ['Subtypes']
        Map<String, String> groupDescriptions = new HashMap<>()

        log.info "Exporting PresentationTypes as csv ${model.name} (${model.combinedVersion})"

        lines << presentationTypesHeader
        descendModelsCsv(model, lines, level, groupDescriptions, exclusions)
        out << lines.join('\n')
    }


    def descendModelsCsv(CatalogueElement model, lines, level, groupDescriptions, exclusions) {
        def line = []

        //strip then re-add surrounding quotes to ensure only one set surround the name
        //and to ensure names with commas are treated as a single column
        String modelName = model.name.replaceAll('^\"|\"$', '')
        modelName = "\"${modelName}\""

        switch (level) {
            case 1:     //ignore top Rare Diseases level
                break

            case 2:     // add disease group id,description
                String groupDescription = "${model.combinedVersion},${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 3: // generate line and add to list
                line << groupDescriptions.get(level - 1)
                line << model.combinedVersion
                line << modelName
                lines << line.join(',')
                return  //don't go deeper

            default:    //don't go deeper
                return
        }


        //don't recurse dataElements
        if(model instanceof DataElement) return

        model.contains.eachWithIndex { CatalogueElement child, i ->
            recurseIfIncludedCsv(child, lines, level, groupDescriptions, exclusions)
        }
        model.parentOf?.eachWithIndex { CatalogueElement child, i ->
            recurseIfIncludedCsv(child, lines, level, groupDescriptions, exclusions)
        }

    }

    private void recurseIfIncludedCsv(CatalogueElement child, lines, level, groupDescriptions, exclusions) {
        def include = true
        exclusions.each { pattern ->
            if (child.name.matches("(?i:.*$pattern.*)")) {
                include = false
            }
        }
        if (include) {
            descendModelsCsv(child, lines, level + 1, groupDescriptions, exclusions)
        }
    }

}









