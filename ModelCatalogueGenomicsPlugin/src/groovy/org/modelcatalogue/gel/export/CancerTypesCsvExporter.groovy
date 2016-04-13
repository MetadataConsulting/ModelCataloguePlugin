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

    static final String CANCER_TYPES_HEADER = "Id,Cancer Types,Presentations,Id SubTypes,Cancer SubTypes"

    private final OutputStream out

    CancerTypesCsvExporter(OutputStream out) {
        this.out = out
    }


    void exportCancerTypesAsCsv(model) {
        int level = 1
        def lines = []
        def exclusions = []
        Map<String, String> groupDescriptions = new HashMap<>()

        log.info "Exporting CancerTypes as csv ${model.name} (${model.combinedVersion})"

        lines << CANCER_TYPES_HEADER
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
            case 1:     //ignore top Cancer types level
                break

            case 2:     // add cancer id,description
                String groupDescription = "${model.combinedVersion},${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 3:
                generateLine(line, groupDescriptions, level, model, modelName)
                lines << line.join(',')
                return  //don't go deeper

            default:    //don't go deeper
                return
        }


        //don't recurse dataElements
        if(model instanceof DataElement) return

        model.contains.eachWithIndex { CatalogueElement child, i ->
            descendModelsCsv(child, lines, level + 1, groupDescriptions, exclusions)
        }
        model.parentOf?.eachWithIndex { CatalogueElement child, i ->
            descendModelsCsv(child, lines, level + 1, groupDescriptions, exclusions)
        }

    }

    private void generateLine(List line, groupDescriptions, level, CatalogueElement model, String modelName) {
        line << groupDescriptions.get(level - 1)

        if (model.name.matches("(?i:.*Subtype.*)")) {

            line << ""
            line << model.combinedVersion
            line << "$modelName"

        } else if (model.name.matches("(?i:.*Presentation.*)")) {
            line << "$modelName,,"
        }
    }

}









