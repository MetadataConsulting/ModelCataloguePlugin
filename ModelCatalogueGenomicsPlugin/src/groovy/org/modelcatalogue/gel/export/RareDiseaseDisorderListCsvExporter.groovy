package org.modelcatalogue.gel.export

import groovy.json.JsonOutput
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 24/03/2016.
 */
class RareDiseaseDisorderListCsvExporter {

    private final def headerline = "id,diseaseGroup,diseaseSubgroup,diseaseName"
    private final OutputStream out

    RareDiseaseDisorderListCsvExporter(OutputStream out) {
        this.out = out
    }


    void export(DataClass model) {
        Integer level = 1
        Map<String, String> groupDescriptions = new HashMap<>()
        def lines = []

        lines << headerline
        descendModels(model, lines, level, groupDescriptions)
        out << lines.join('\n')
    }

    def descendModels(DataClass model, lines, level, groupDescriptions) {
        def line = []

        //strip then re-add surrounding quotes to ensure only one set surround the name
        //and to ensure names with commas are treated as a single column
        String modelName = model.name.replaceAll('^\"|\"$', '')
        modelName = "\"${modelName}\""

        switch (level) {
            case 1:     //ignore top Rare Diseases level
                break

            case 2:     // add disease group description
                groupDescriptions.put(level, modelName)
                break

            case 3: // add disease sub-group description
                String groupDescription = "${groupDescriptions.get(level - 1)},${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 4: // generate line and add to list
                line << model.id
                line << groupDescriptions.get(level - 1)
                line << modelName
                lines << line.join(',')
                return  //don't go deeper

            default:    //don't go deeper
                return
        }


        model.parentOf?.each { DataClass child ->
            descendModels(child, lines, level + 1, groupDescriptions)
        }
    }


}









