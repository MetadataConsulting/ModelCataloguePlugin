package org.modelcatalogue.gel.export

import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.util.HibernateHelper

import static org.modelcatalogue.core.util.HibernateHelper.getEntityClass

/**
 * Created by rickrees on 31/03/2016.
 *
 */
@Log4j
class CancerTypesCsvExporter {

    static final String CANCER_TYPES_HEADER = "Id,Cancer Types,Presentations,Id SubTypes,Cancer SubTypes,Id Enum,Enum SubType"

    private final OutputStream out

    CancerTypesCsvExporter(OutputStream out) {
        this.out = out
    }


    void exportCancerTypesAsCsv(CatalogueElement model) {
        int level = 1
        def lines = []
        def exclusions = []
        Map<String, String> groupDescriptions = new HashMap<>()

        log.info "Exporting CancerTypes as csv ${model.name} (${model.combinedVersion})"

        lines << CANCER_TYPES_HEADER
        descendModelsCsv(model, lines, level, groupDescriptions, exclusions)
        out << lines.join('\n')
    }



    def descendModelsCsv(CatalogueElement model, lines, level, Map groupDescriptions, exclusions) {
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
                subTypeOrPresentation(lines, groupDescriptions, level, model, modelName)
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

    private void subTypeOrPresentation(List lines, groupDescriptions, level, CatalogueElement model, String subTypeName) {

        if (subTypeName.matches("(?i:.*Subtype.*)")) {
            iterateEnumTypes(lines, groupDescriptions, level, model, subTypeName)

        } else if (subTypeName.matches("(?i:.*Presentation.*)")) {
            String line = generateLine(groupDescriptions, level, model, null, null, subTypeName)
            lines << line
        }
    }

    private List<String> iterateEnumTypes(List lines, groupDescriptions, level, CatalogueElement model, String subTypeName) {

        if(subTypeName.matches("(?i:.*Subtype.*)") && getEntityClass(model) == DataElement && getEntityClass(model.dataType) == EnumeratedType) {
            EnumeratedType enumTypes = model.dataType as EnumeratedType
            log.debug("found enumType $enumTypes.name")

            enumTypes.enumerations.each { enumType ->
                String line = generateLine(groupDescriptions, level, model, enumTypes, enumType, subTypeName)
                lines << line
            }
        }
        lines
    }


    private String generateLine(groupDescriptions, level, DataElement cancerSubType, EnumeratedType enumTypes, Map.Entry enumType, String subTypeName) {
        def vars = []
        String line = ''

        vars << groupDescriptions.get(level - 1)

        if (enumType) {

            vars << ""
            vars << cancerSubType.combinedVersion
            vars << "$subTypeName"
            vars << "$enumTypes.combinedVersion"
            vars << "\"${enumType.key}\""

        } else {
            vars << "$subTypeName,,,,"
        }
        line = vars.join(',')
    }


}









