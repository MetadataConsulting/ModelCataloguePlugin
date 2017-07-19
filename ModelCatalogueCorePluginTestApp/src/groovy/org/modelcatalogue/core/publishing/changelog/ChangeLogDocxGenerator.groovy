package org.modelcatalogue.core.publishing.changelog

import com.craigburke.document.core.builder.DocumentBuilder
import com.google.common.collect.LinkedHashMultimap
import com.google.common.collect.Multimap
import grails.util.GrailsNameUtils
import groovy.util.logging.Log4j
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.ElementService
import org.modelcatalogue.core.PerformanceUtilService
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.audit.ChangeType
import org.modelcatalogue.core.comments.Comment
import org.modelcatalogue.core.export.inventory.DocxSpecificationDataHelper
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.delayable.Delayable
import org.modelcatalogue.core.util.docx.ModelCatalogueWordDocumentBuilder

import java.text.SimpleDateFormat

@Log4j
class ChangeLogDocxGenerator extends AbstractChangeLogGenerator{

    def customTemplate
    PerformanceUtilService performanceUtilService
    public static final int CLEAN_UP_GORM_FREQUENCY = 10    //tuned for speed
    String imagePath
    ElementService elementService

    DocxSpecificationDataHelper docHelper

    final static Closure defaultTemplate = {
        'document' font: [family: 'Calibri'], margin: [left: 30, right: 30]
        'paragraph.title' font: [color: '#13D4CA', size: 26.pt], margin: [top: 200.pt]
        'paragraph.subtitle' font: [color: '#13D4CA', size: 18.pt]
        'paragraph.description' font: [color: '#13D4CA', size: 16.pt, italic: true], margin: [left: 30, right: 30]
        'heading1' font: [size: 20, bold: true]
        'heading2' font: [size: 18, bold: true]
        'heading3' font: [size: 16, bold: true, italic: true]
        'heading4' font: [size: 14, italic: true]
        'heading5' font: [size: 13]
        'heading6' font: [size: 12, bold: true]
        'paragraph.heading1' font: [size: 20, bold: true]
        'paragraph.heading2' font: [size: 18, bold: true]
        'paragraph.heading3' font: [size: 16, bold: true]
        'paragraph.heading4' font: [size: 16]
        'paragraph.heading5' font: [size: 15]
        'paragraph.heading6' font: [size: 14]
        'cell.headerCell' font: [color: '#29BDCA', size: 12.pt, bold: true], background: '#F2F2F2'
        'cell' font: [size: 10.pt]

    }

    ChangeLogDocxGenerator(AuditService auditService, DataClassService dataClassService, PerformanceUtilService performanceUtilService, ElementService elementService, Integer depth = 3, Boolean includeMetadata = true, Closure customTemplate = defaultTemplate, String imagePath = null) {
        super(auditService, dataClassService, depth, includeMetadata)
        this.customTemplate=customTemplate
        this.imagePath=imagePath
        this.performanceUtilService = performanceUtilService
        this.elementService = elementService
    }

    @Override
    void generateChangelog(DataClass dataClass, OutputStream outputStream) {
        log.info "Generating changelog for data class $dataClass.name ($dataClass.combinedVersion)"
        DocumentBuilder builder = new ModelCatalogueWordDocumentBuilder(outputStream)

        docHelper = new DocxSpecificationDataHelper(builder, depth, elementService)

        Delayable<DocumentBuilder> delayable = new Delayable<>(builder)

        if(!customTemplate) customTemplate = defaultTemplate

        builder.create {
            document(template: customTemplate) {

                def rootModel = dataClass.dataModel

                def thisOrganisation = rootModel.ext.get(Metadata.ORGANISATION)

                byte[] imageData
                if(imagePath) imageData = new URL(imagePath).bytes

                if (thisOrganisation && imagePath) {
                    paragraph(align: 'right') {
                        image(data: imageData, height: 1.366.inches, width: 2.646.inches)
                    }

                    paragraph(style: 'title', align: 'center') {
                        text thisOrganisation
                    }

                    paragraph(style: 'subtitle', align: 'center') {
                        text "${rootModel.name}"
                    }

                    paragraph(style: 'document', margin: [top: 120]) {
                        text "Version ${rootModel.versionNumber} ${rootModel.status}"
                        lineBreak()
                        text SimpleDateFormat.dateInstance.format(new Date())
                    }
                } else {
                    paragraph "Changelog for ${dataClass.name}", style: 'title',  align: 'center'
                    paragraph(style: 'subtitle', align: 'center') {
                        text "${dataClass.combinedVersion}"
                        lineBreak()
                        text "${dataClass.status}"
                        lineBreak()
                        text SimpleDateFormat.dateInstance.format(new Date())
                    }
                    if (dataClass.description) {
                        paragraph(style: 'description', margin: [left: 50, right: 50]) {
                            text dataClass.description
                        }
                    }
                }

                pageBreak()

                delayable.whilePaused {
                    delayable.heading1 "Root Data Class Changes"
                    printPropertiesChanges(delayable, dataClass)
                }

                heading1 'Data Classes'

                Collection<DataClass> classes = collectDataClasses(dataClass)
                int counter = 1
                int size = classes.size()
                performanceUtilService.cleanUpGorm()

                for (DataClass child in classes) {
                    if ((counter % CLEAN_UP_GORM_FREQUENCY == 0)) {
                        performanceUtilService.cleanUpGorm()
                    }

                    log.info "[${counter++}/${size}] Processing changes from Data Class $child.name - depth $depth"

                    delayable.whilePaused {
                        docHelper.printClass(child, false, depth)   //don't recurse
                        printPropertiesChanges(delayable, child)
                        printClassStructuralChanges(delayable, child)
                    }

                }
            }
        }

        log.info "Data Class $dataClass.name changelog exported to Word Document"


    }

    private void printPropertiesChanges(Delayable<DocumentBuilder> builder, CatalogueElement element, int headingLevel = 2) {
        builder.with {
            "heading${Math.min(headingLevel, 5)}" "$element.name ($element.combinedVersion, $element.status)", ref: "${element.getId()}"

            if (getChanges(element, ChangeType.NEW_VERSION_CREATED)) {
                requestRun()
                paragraph {
                    text "This is a new version "
                    text element.versionNumber, font: [bold: true]
                    text " of the ${GrailsNameUtils.getNaturalName(element.class.name)}."
                }
            } else if (getChanges(element, ChangeType.NEW_ELEMENT_CREATED)) {
                requestRun()
                paragraph "This is a new ${GrailsNameUtils.getNaturalName(element.class.name)}"
            } else if (getChanges(element, ChangeType.ELEMENT_DEPRECATED)) {
                requestRun()
                paragraph {
                    text "This ${GrailsNameUtils.getNaturalName(element.class.name)} has been "
                    text " deprecated", font: [bold: true]
                }
            }


            if (isForumEnabled()) {
                List<Comment> comments = getComments(element)
                if (comments) {
                    builder.with {
                        requestRun()
                        heading3 'Comments'
                        // first comment is always a description and link
                        for (Comment comment in comments.tail()) {
                                paragraph "${comment.username} (${SimpleDateFormat.dateTimeInstance.format(comment.created)})" , font: [bold: true]
                                paragraph comment.text
                        }
                    }
                }
            }

            Map<String, List<String>> changedProperties = collectChangedPropertiesRows(element)

            if (changedProperties) {
                requestRun()
                paragraph font: [bold: true], "Changed Properties"
                printChangesTable builder, changedProperties
            }

            withChangedNestedProperties(element) { CatalogueElement ce ->
                builder.whilePaused {
                    printPropertiesChanges(builder, ce, headingLevel + 1)
                }
            }

        }
    }

    private void printChangesTable(Delayable<DocumentBuilder> builder, Map<String, List<String>> rows) {
        builder.table(border: [size: 1, color: '#D2D2D2'], columns: [1,2,2], font: [size: 10]) {
            row(background: '#F2F2F2') {
                cell "Property", style: 'headerCell'
                cell "Old Value", style: 'headerCell'
                cell "New Value", style: 'headerCell'
            }

            for (Map.Entry<String, List<String>> change in rows) {
                String background = "#FFFFFF"

                if (change.value[0] && !change.value[1]) {
                    background = "#F2DEDE"
                } else if (!change.value[0] && change.value[1]) {
                    background = '#DFF0D8'
                }
                row (background: background){
                    cell change.key, font: [bold: true]
                    cell change.value[0]
                    cell change.value[1]
                }
            }
        }
    }

    private void printClassStructuralChanges(Delayable<DocumentBuilder> builder, DataClass dataClass) {
        List<Change> relationshipChanges = getChanges(dataClass, ChangeType.RELATIONSHIP_CREATED, ChangeType.RELATIONSHIP_DELETED, ChangeType.RELATIONSHIP_ARCHIVED, ChangeType.RELATIONSHIP_METADATA_CREATED, ChangeType.RELATIONSHIP_METADATA_DELETED, ChangeType.RELATIONSHIP_METADATA_UPDATED)

        Multimap<String, Change> byDestinationsAndSources = LinkedHashMultimap.create()

        for (Change ch in relationshipChanges) {
            byDestinationsAndSources.put "out:${getRelationshipType(ch)}:${getDestinationId(ch)}".toString(), ch
            byDestinationsAndSources.put "in:${getRelationshipType(ch)}:${getSourceId(ch)}".toString(), ch
        }

        handleRelationshipChanges(builder, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.hierarchyType, byDestinationsAndSources).withChangesSummaryHeading("Changed Inner Data Classes").withNewRelationshipNote("New inner data class").withRemovedRelationshipNote("Inner data class removed"))
        handleRelationshipChanges(builder, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.containmentType, byDestinationsAndSources).withChangesSummaryHeading("Changed Data Elements").withNewRelationshipNote("New data element").withRemovedRelationshipNote("Data element removed").withDeep(true))
        handleRelationshipChanges(builder, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.synonymType, byDestinationsAndSources).withChangesSummaryHeading("Changed Synonyms").withNewRelationshipNote("New synonym").withRemovedRelationshipNote("Synonym removed"))
        handleRelationshipChanges(builder, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.relatedToType, byDestinationsAndSources).withChangesSummaryHeading("Changed Relations").withNewRelationshipNote("Newly related").withRemovedRelationshipNote("No longer related"))
        handleRelationshipChanges(builder, RelationshipChangesCheckConfiguration.create(dataClass, RelationshipType.baseType, byDestinationsAndSources).withChangesSummaryHeading("Changed Bases").withNewRelationshipNote("Newly based on").withRemovedRelationshipNote("No longer based on"))
    }

    private void handleRelationshipChanges(Delayable<DocumentBuilder> builder, RelationshipChangesCheckConfiguration configuration) {
        builder.whilePaused {
            builder.heading3 configuration.changesSummaryHeading


            Map<String, RelationshipChangeItem> changeItemsByHeading = collectRelationshipChanges(configuration)

            if (changeItemsByHeading) {
                builder.requestRun()
                builder.table(border: [size: 1, color: '#D2D2D2'], columns: [1] * 10, font: [size: 10]) {
                    for (Map.Entry<String, RelationshipChangeItem> entry in changeItemsByHeading) {
                        Map<String, List<String>> metadataChanges = changeItemsByHeading[entry.key].metadataChanges
                        if (entry.value.title || metadataChanges) {
                            String background = "#FFFFFF"

                            if (entry.value.title == configuration.newRelationshipNote) {
                                background = "#DFF0D8"
                            } else if (entry.value.title == configuration.removedRelationshipNote) {
                                background = '#F2DEDE'
                            }
                            row(background: background) {
                                cell entry.key, colspan: 5, font: [bold: true, size: 12]
                                cell(entry.value.title ?: 'Metadata Updated', colspan: 5)
                            }
                        }
                        if (metadataChanges) {
                            row(background: '#F2F2F2') {
                                cell 'Updated Metadata', colspan: 2, style: 'headerCell', font: [size: 10]
                                cell 'Old Value', colspan: 4, style: 'headerCell'
                                cell 'New Value', colspan: 4, style: 'headerCell'
                            }
                            for (Map.Entry<String, List<String>> metadataEntry in metadataChanges) {
                                String background = "#FFFFFF"

                                if (metadataEntry.value[0] && !metadataEntry.value[1]) {
                                    background = "#F2DEDE"
                                } else if (!metadataEntry.value[0] && metadataEntry.value[1]) {
                                    background = '#DFF0D8'
                                }
                                row(background: background) {
                                    cell metadataEntry.key, colspan: 2
                                    cell metadataEntry.value[0], colspan: 4
                                    cell metadataEntry.value[1], colspan: 4
                                }
                            }
                        }
                    }
                }
            }

            if (configuration.deep) {
                for (CatalogueElement element in configuration.nestedRelations) {
                    builder.whilePaused {
                        printPropertiesChanges(builder, element, 4)
                    }
                }
            }
        }
    }


}
