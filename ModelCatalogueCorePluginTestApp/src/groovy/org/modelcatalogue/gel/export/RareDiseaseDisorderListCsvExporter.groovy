package org.modelcatalogue.gel.export

import org.modelcatalogue.core.DataClass

/**
 * Created by rickrees on 24/03/2016.
 */
class RareDiseaseDisorderListCsvExporter {

    private final def headerline = "id,Level 2 DiseaseGroup,id,Level 3 Disease Subgroup,id,Level 4 Specific Disorder"
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

            case 2:     // add disease group id,description
                String groupDescription = "${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getLatestVersionId() ?: model.getId()},${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 3: // add disease sub-group id,description
                String groupDescription = "${groupDescriptions.get(level - 1)},${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getLatestVersionId() ?: model.getId()},${modelName}"
                groupDescriptions.put(level, groupDescription)
                break

            case 4: // generate line and add to list
                line << groupDescriptions.get(level - 1)
                line << "${model.ext.get('http://www.modelcatalogue.org/metadata/genomics/#gel-id') ?: model.getLatestVersionId() ?: model.getId()}"
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









