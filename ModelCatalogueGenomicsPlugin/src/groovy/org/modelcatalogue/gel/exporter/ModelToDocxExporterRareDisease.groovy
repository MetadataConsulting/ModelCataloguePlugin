package org.modelcatalogue.gel.exporter

import com.craigburke.document.core.builder.DocumentBuilder
import groovy.util.logging.Log4j
import org.hibernate.FetchMode
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.ModelService
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.ValueDomain
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

@Log4j
class ModelToDocxExporterRareDisease {

    private static final Map<String, Object> HEADER_CELL =  [background: '#F2F2F2']
    private static final Map<String, Object> HEADER_CELL_TEXT =  [font: [color: '#29BDCA', size: 12, bold: true, family: 'Times New Roman']]
    private static final Map<String, Object> ENUM_HEADER_CELL_TEXT =  [font: [size: 12, bold: true]]
    private static final Map<String, Object> CELL_TEXT =  [font: [size: 10, family: 'Calibri']]
    private static final Map<String, Object> CELL_TEXT_FIRST = [font: [size: 10, family: 'Calibri', bold: true]]
    private static final Map<String, Object> DOMAIN_NAME =  [font: [color: '#29BDCA', size: 14, bold: true]]
    private static final Map<String, Object> DOMAIN_CLASSIFICATION_NAME =  [font: [color: '#999999', size: 12, bold: true]]


    final ModelService modelService
    final Long modelId
    final Set<ValueDomain> usedValueDomains = new TreeSet<ValueDomain>([compare: { ValueDomain a, ValueDomain b ->
        a?.name <=> b?.name
    }] as Comparator<ValueDomain>)
    final Set<Long> processedModels = new HashSet<Long>()


    ModelToDocxExporterRareDisease(Model model, ModelService modelService) {
        this.modelId = model.getId()
        this.modelService = modelService
    }

    void export(OutputStream outputStream) {

        usedValueDomains.clear()
        processedModels.clear()

        Model rootModel = Model.get(modelId)

        log.info "Exporting model $rootModel to Word Document"

        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        def customTemplate = {
            'document' font: [family: 'Calibri'], margin: [left: 20, right: 10]
            'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
            'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
            'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
            'heading1' font: [size: 20, bold: true]
            'heading2' font: [size: 18, bold: true]
            'heading3' font: [size: 16, bold: true]
            'heading4' font: [size: 16]
            'heading5' font: [size: 15]
            'heading6' font: [size: 14]
            'paragraph.heading1' font: [size: 20, bold: true]
            'paragraph.heading2' font: [size: 18, bold: true]
            'paragraph.heading3' font: [size: 16, bold: true]
            'paragraph.heading4' font: [size: 16]
            'paragraph.heading5' font: [size: 15]
            'paragraph.heading6' font: [size: 14]
            'cell.headerCell' font: [color: '#29BDCA', size: 12.pt, bold: true], background: '#F2F2F2'
            'cell' font: [size: 10.pt]

        }


        builder.create {
            document(template: customTemplate) {
                paragraph rootModel.name, style: 'title',  align: 'center'
                paragraph(style: 'subtitle', align: 'center') {
                    text "${rootModel.status}"
                    lineBreak()
                    text SimpleDateFormat.dateInstance.format(new Date())
                }
                if (rootModel.description) {
                    paragraph(style: 'classification.description', margin: [left: 50, right: 50]) {
                        text rootModel.description
                    }
                }
                pageBreak()

                for (Model model in rootModel.parentOf) {
                    printModel(builder, model, 0, true)
                }

                processedModels.clear()

                for (Model model in rootModel.parentOf) {
                    printModel(builder, model, 0, false)
                }

                if (usedValueDomains) {
                    pageBreak()
                    heading1 'Value Domains'

                    for (ValueDomain domain in usedValueDomains) {

                        log.debug "Exporting value domain $domain to Word Document"

                        Map<String, Object> attrs = [ref: "${domain.id}", style: 'heading2']
                        attrs.putAll(DOMAIN_NAME)

                        paragraph attrs, domain.name

                        if (domain.classifications) {
                            paragraph {
                                text DOMAIN_CLASSIFICATION_NAME, "(${domain.classifications.first().name})"
                            }
                        }

                        if (domain.description) {
                            paragraph {
                                text domain.description
                            }
                        }
                        if (domain.unitOfMeasure || domain.dataType || domain.rule) {
                            table(columns: [1,4], border: [size: 0], font: [color: '#5C5C5C']) {
                                if (domain.dataType) {
                                    row {
                                        cell 'Data Type'
                                        cell {
                                            text domain.dataType.name
                                            if (domain.dataType.description) {
                                                text ' ('
                                                text domain.dataType.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (domain.unitOfMeasure) {
                                    row {
                                        cell 'Unit of Measure'
                                        cell {
                                            text domain.unitOfMeasure.name
                                            if (domain.unitOfMeasure.description) {
                                                text ' ('
                                                text domain.unitOfMeasure.description
                                                ')'
                                            }
                                        }
                                    }
                                }

                                if (domain.regexDef) {
                                    row {
                                        cell 'Regular Expression'
                                        cell domain.regexDef
                                    }
                                } else if (domain.rule) {
                                    row {
                                        cell 'Rule'
                                        cell domain.rule
                                    }
                                }

                            }

                            if (domain.dataType?.instanceOf(EnumeratedType)) {
                                paragraph(font:  [color: '#999999', bold: true]){
                                    text 'Enumerations'
                                }

                                table (border: [size: 1, color: '#D2D2D2']) {
                                    row (background: '#F2F2F2'){
                                        cell ENUM_HEADER_CELL_TEXT, 'Code'
                                        cell ENUM_HEADER_CELL_TEXT, 'Description'
                                    }
                                    for (Map.Entry<String, String> entry in domain.dataType.enumerations) {
                                        row {
                                            cell entry.key
                                            cell entry.value
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        log.debug "Model $rootModel exported to Word Document"
    }


    private void printEligibilityModels(DocumentBuilder builder, Model model, int level) {

        builder.with {

            if (model.getId() in processedModels) {
                "heading${Math.min(level + 1, 6)}" model.name, ref: "${model.getId()}"
            } else {
                "heading${Math.min(level + 1, 6)}" model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
            }


                if (model.description) {
                    paragraph {
                        text model.description
                    }
                }



            for (Model child in model.parentOf) {
                if (!child.name.matches("(?i:.*Closing.*)")) {
                    if (child.getId() in processedModels) {
                        "heading${Math.min(level + 2, 6)}" child.name, ref: "${model.getId()}"
                    } else {
                        "heading${Math.min(level + 2, 6)}" child.name
                    }
                }



                if (child.description) {
                    paragraph {
                        text child.description
                    }
                }

            }
            pageBreak()
        }
    }


    private void printTests(DocumentBuilder builder, Model model, int level, String guidanceText = "") {


        builder.with {
            paragraph {
                text ""
            }

                if (model.getId() in processedModels) {
                    "heading${Math.min(level + 1, 6)}" model.name, ref: "${model.getId()}"
                } else {
                    "heading${Math.min(level + 1, 6)}" model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
                }



            if (model.description) {
                paragraph {
                    text model.description
                }
            }

            if (guidanceText) {
                paragraph {
                    text guidanceText
                }
            }

            if(model.countParentOf()){
                table(padding: 1, border: [size: 1, color: '#D2D2D2'], columns: [2, 3, 2, 2, 3]) {
                    for (Model child in model.parentOf) {
                        row {
                            cell {
                                text CELL_TEXT_FIRST, "${child.name} (${child.getCombinedVersion()}) "
                            }
                        }

                    }
                }
            }

            pageBreak()



        }

    }



    private void printPhenotypeModels(DocumentBuilder builder, Model model, int level) {

        builder.with {

            paragraph {
                text ""
            }


                if (model.getId() in processedModels) {
                    "heading${Math.min(level + 1, 6)}" model.name, ref: "${model.getId()}"
                } else {
                    "heading${Math.min(level + 1, 6)}" model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
                }



                if (model.description) {
                    paragraph {
                        text model.description
                    }
                }


//            for (Model child in model.parentOf) {
//
//                if (child.getId() in processedModels) {
//                    "heading${Math.min(level + 2, 6)}" child.name, ref: "${model.getId()}"
//                } else {
//                    "heading${Math.min(level + 2, 6)}" child.name
//                }
//
//
//                paragraph {
//                    if (child.description) {
//                        text child.description
//                    } else {
//                        text "${child.name} model does not have any description yet.", font: [italic: true]
//                    }
//                }
//
//
//            }


            def models = []
            if (model.countParentOf()) {
                table(padding: 1, border: [size: 1, color: '#D2D2D2'], columns: [2, 3, 2, 2, 3]) {

                        for (Model child in model.parentOf) {

                            models.add("${child.name} (${child.ext.get("OBO ID")})")

                            if (models.size() == 3) {
                                row {
                                    cell {
                                        text CELL_TEXT_FIRST, models[0]
                                    }
                                    cell {
                                        text CELL_TEXT_FIRST, models[1]
                                    }
                                    cell {
                                        text CELL_TEXT_FIRST, models[2]
                                    }

                                }

                                models = []
                            }

                        }
                        if(models){
                            row {
                                cell {
                                    text CELL_TEXT_FIRST, (models[0] ? models[0] : "")
                                }
                                cell {
                                    text CELL_TEXT_FIRST, (models[1]?models[1]:"")
                                }
                                cell {
                                    text CELL_TEXT_FIRST, (models[2] ? models[2] : "")
                                }

                            }
                        }
                }
            }
        }

    }



    private void printModel(DocumentBuilder builder, Model model, int level, Boolean eligibility, String guidance = "") {

        if(model.name.matches("(?i:.*Eligibility.*)")){
           if(eligibility) printEligibilityModels(builder, model, level + 1)
        }else if(model.name.matches("(?i:.*Phenotype.*)") && !model.name.matches("Balanced translocations with an unusual phenotype") && !model.name.matches("VACTERL-like phenotypes")&& !model.name.matches("Disorders of unusual phenotypes") && !model.name.matches("Diabetes with additional phenotypes suggestive of a monogenic aetiology")){
            if(!eligibility) printPhenotypeModels(builder, model, level + 1)
        }else if(model.name.matches("(?i:.*Tests.*)")){
            if(!eligibility) printTests(builder, model, level + 1, guidance)
        }else if(!model.name.matches("(?i:.*Guidance.*)")) {

            if (level > 10) {
                // only go 4 levels deep
                return
            }

            log.debug "Exporting model $model to Word Document"

            builder.with {

                if (model.getId() in processedModels) {
                    "heading${Math.min(level + 1, 6)}" model.name, ref: "${model.getId()}"
                } else {
                    "heading${Math.min(level + 1, 6)}" model.name + " (${model.getLatestVersionId() ?: model.getId()}.${model.getVersionNumber() ?: 1})"
                }


                    if (model.description) {
                        paragraph {
                            text model.description
                        }
                    }
                }

//                if (!model.countContains() && !model.countParentOf()) {
//                    paragraph {
//                        text "${model.name} model does not have any child models or data elements yet.", font: [italic: true]
//                    }
//                }


                if (model.countContains()) {
                    table(padding: 1, border: [size: 1, color: '#D2D2D2'], columns: [2, 3, 2, 2, 3]) {
                        row {
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Name'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Description'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Multiplicity'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Data Type'
                            }
                            cell HEADER_CELL, {
                                text HEADER_CELL_TEXT, 'Same As'
                            }
                        }
                        for (Relationship dataElementRelationship in model.containsRelationships) {
                            DataElement dataElement = dataElementRelationship.destination
                            if (dataElement.valueDomain) {
                                usedValueDomains << dataElement.valueDomain
                            }
                            row {
                                cell {
                                    text CELL_TEXT_FIRST, "${dataElement.name} (${dataElement.getLatestVersionId() ?: dataElement.getId()}.${dataElement.getVersionNumber() ?: 1})"
                                }
                                cell {
                                    text CELL_TEXT, dataElement.description ?: ''
                                }
                                cell {
                                    text CELL_TEXT, getMultiplicity(dataElementRelationship)
                                }
                                cell {
                                    if (dataElement.valueDomain) {
                                        Map<String, Object> attrs = [url: "#${dataElement.valueDomain.id}", font: [bold: true]]
                                        attrs.putAll(CELL_TEXT)
                                        text attrs, dataElement.valueDomain.name
                                        if (dataElement.valueDomain.dataType?.instanceOf(EnumeratedType)) {
                                            text '\n\n'
                                            text 'Enumerations', font: [italic: true]
                                            text '\n'
                                            if (dataElement.valueDomain.dataType.enumerations.size() <= 10) {
                                                for (entry in dataElement.valueDomain.dataType.enumerations) {
                                                    text "${entry.key ?: ''}", font: [bold: true]
                                                    text ":"
                                                    text "${entry.value ?: ''}"
                                                    text "\n"
                                                }
                                            }

                                        }
                                    }
                                }
                                cell {
                                    for (CatalogueElement synonym in dataElement.isSynonymFor) {
                                        text CELL_TEXT, getSameAs(synonym)
                                        lineBreak()
                                    }

                                }
                            }
                        }
                    }
                }
                def guidanceText = ""
                if (!(model.getId() in processedModels)) {
                    if (model.countParentOf()) {
                        for (Model child in model.parentOf) {

                            if(level==2){
                                model.parentOf.each{ md ->
                                    if(md.name.matches("(?i:.*Guidance.*)")) {
                                        guidanceText = md.description
                                    }
                                }

                            }

                            printModel(builder, child, level + 1, eligibility, guidanceText)
                        }
                    }
                }

                processedModels << model.getId()
            }
        }

    private static String getSameAs(CatalogueElement element) {
        if (!element.classifications) {
            return "${element.name}"
        }
        "${element.name} (${element.ext['Data Item No'] ? "${element.ext['Data Item No']} from " : ''}${element.classifications.first().name})"
    }

    private static String getMultiplicity(Relationship relationship) {
        "${relationship.ext['Min Occurs'] ?: 0}..${relationship.ext['Max Occurs'] ?: 'unbounded'}"
    }

}

