package org.modelcatalogue.core.dataarchitect

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.modelcatalogue.core.*
import org.obolibrary.oboformat.model.Clause
import org.obolibrary.oboformat.model.Frame
import org.obolibrary.oboformat.model.OBODoc
import org.obolibrary.oboformat.parser.OBOFormatParser
import org.modelcatalogue.core.util.builder.CatalogueBuilder

class OBOService {

    static transactional = false

    def relationshipService
    def elementService
    def classificationService

    private SimpleTemplateEngine engine = new SimpleTemplateEngine()

    private static final String OBO_ID = 'OBO ID'
    private static final String ALTERNATIVE_IDS = 'Alternative IDs'

    /**
     * The document has header frame and term frames. Term frames are converted to models including links to the
     * synonyms. Each term contain causes which are one of following:

     id
     count:              10761 in Human Phenotype (2014-10-21)
     values:             [HP:0011606]
     xrefs:              []
     qualifier values:   []
     import:             Model.ext[OBO ID]

     name
     count:              10761 in Human Phenotype (2014-10-21)
     values:             [Transposition of the great arteries with intact ventricular septum]
     xrefs:              []
     qualifier values:   []
     import:             Model.name

     def
     count:              7742 in Human Phenotype (2014-10-21)
     values:             [A congenital anomaly with an abnormal connection between the aorta and the main pulmonary artery resulting in an aortopulmonary shunt.]
     xrefs:              [DDD:dbrown, HPO:probinson]
     qualifier values:   []
     import:              Model.description

     xref
     count:              5464 in Human Phenotype (2014-10-21)
     values:             [<UMLS:C0426857 "Short arms">]
     xrefs:              []
     qualifier values:   []
     import:             Model.ext[xrefs]

     is_a
     count:              14075 in Human Phenotype (2014-10-21)
     values:             [HP:0001669]
     xrefs:              []
     qualifier values:   []
     import:             Model.childOf

     created_by
     count:              4864 in Human Phenotype (2014-10-21)
     values:             [peter]
     xrefs:              []
     qualifier values:   []
     import:             Model.ext[Created by]

     creation_date
     count:              4863 in Human Phenotype (2014-10-21)
     values:             [2012-04-08T02:57:22Z]
     xrefs:              []
     qualifier values:   []
     import:             Model.dateCreated

     namespace
     count:              10761 in Human Phenotype (2014-10-21)
     values:             [human_phenotype]
     xrefs:              []
     qualifier values:   []
     import:             Model.classifications

     alt_id
     count:              3432 in Human Phenotype (2014-10-21)
     values:             [HP:0006364]
     xrefs:              []
     qualifier values:   []
     import:             Model.ext[Alternative IDs]

     comment
     count:              1813 in Human Phenotype (2014-10-21)
     values:             [According to the Van Praagh classification (pmid:2856609). The Van Praagh classification additionally specifies the presence (subtype A) or absence (subtype B) of a ventricular septal defect.]
     xrefs:              []
     qualifier values:   []
     import:             Model.description (appendix)

     subset
     count:              850 in Human Phenotype (2014-10-21)
     values:             [hposlim_core]
     xrefs:              []
     qualifier values:   []
     import:             Model.classifications

     synonym
     count:              6946 in Human Phenotype (2014-10-21)
     values:             [TGA IVS, EXACT]
     xrefs:              []
     qualifier values:   []
     import:             EXACT: Model.synonym, ^EXACT: Model.relatedTo + ext[type]=which


     is_obsolete
     count:              46 in Human Phenotype (2014-10-21)
     values:             [true]
     xrefs:              []
     qualifier values:   []
     import:             Model.status = DEPRECATED


     replaced_by
     count:              15 in Human Phenotype (2014-10-21)
     values:             [HP:0009447]
     xrefs:              []
     qualifier values:   []
     import:             Model.supersededBy, Model.status = DEPRECATED


     consider
     count:              32 in Human Phenotype (2014-10-21)
     values:             [HP:0001428]
     xrefs:              []
     qualifier values:   []
     import:             Model.relatedTo, Model.relatedTo.consider = true


     is_anonymous
     count:              2 in Human Phenotype (2014-10-21)
     values:             [true]
     xrefs:              []
     qualifier values:   []
     import:             skipped

     property_value
     count:              2393 in Human Phenotype (2014-10-21)
     values:             [HP:0040005, `Peromelia` (HP:0009828) affecting only the lower limbs.  That is, the distal parts of the leg are missing leading to stump formation., xsd:string]
     xrefs:              []
     qualifier values:   [{xref=HPO:sdoelken}]
     import:             Model.ext[values[0]] = values[1], Model.ext[values[0] Type] = values[2] (if present)

     * This import is initially tuned up for Human Phenotype ontology, few parts are still missing.
     * See http://www.geneontology.org/GO.format.obo-1_2.shtml#S.1 for full current specification.
     */
    Classification importOntology(InputStream is, String name, String mcIDPattern) {
        log.info "Parsing OBO file for ${name}"
        OBODoc document = new OBOFormatParser().parse(new BufferedReader(new InputStreamReader(is)))

        CatalogueBuilder builder = new CatalogueBuilder(classificationService, elementService)

        Map<String, String> namespacesToClassifications = [:]
        Map<String, String> oboIdsToNames = [:]
        Template idTemplate = getIdTemplate(mcIDPattern)

        log.info "Collecting OBO IDs"
        document.termFrames.eachWithIndex { Frame frame, i ->
            oboIdsToNames[frame.id] = getName(frame)
        }


        log.info "Building new models"
        builder.build {

            for (Clause clause in document.headerFrame.getClauses('subsetdef')) {
                namespacesToClassifications[clause.value.toString()] = clause.value2.toString()
                classification(name: clause.value2.toString()) {
                    ext 'namespace', clause.value.toString()
                }
            }

            classification(name: name) {
                String defaultNamespace = document.headerFrame.getClause('default-namespace')?.value?.toString()
                if (defaultNamespace) {
                    ext 'namespace', defaultNamespace
                    namespacesToClassifications[defaultNamespace] = name
                }

                document.termFrames.eachWithIndex { Frame frame, i ->
                    log.debug "[${(i + 1).toString().padLeft(6, '0')}/${document.termFrames.size().toString().padLeft(6, '0')}] Importing model ${frame.id}: ${frame.getClause('name')?.value}"


                    String modelId = getId(frame.id, idTemplate)
                    model(name: getName(frame), id: modelId) {
                        ext OBO_ID, frame.id

                        handleDefAndComment(builder, frame)
                        handleCreatedBy(builder, frame)
                        handleCreatedDate(builder, frame)
                        handleNamespaceAndSubsets(builder, frame, namespacesToClassifications)
                        handleAltIds(builder, frame)
                        handleXrefs(builder, frame)
                        handleStatusForObsoleteAndReplacedBy(builder, frame)
                        handlePropertyValues(builder, frame)
                        handleIsA(builder, frame, idTemplate, oboIdsToNames)
                        handleSynonym(builder, frame)
                        handleReplacedBy(builder, frame, idTemplate, oboIdsToNames)
                    }
                }
            }
        }

        log.info "Import finished for ${name}"

        builder.lastCreated.find { it.instanceOf(Classification) }
    }

    private static void handleIsA(CatalogueBuilder builder, Frame frame, Template idTemplate, Map<String, String> oboIdsToNames) {
        for (Clause clause in frame.getClauses('is_a')) {
            if (idTemplate) {
                builder.rel 'hierarchy' from builder.ref(getId(clause.value?.toString(), idTemplate))
            } else {
                builder.rel 'hierarchy' from oboIdsToNames[clause.value?.toString()]
            }
        }
    }

    private static void handleSynonym(CatalogueBuilder builder, Frame frame) {
        List<String> textual = []

        for (Clause clause in frame.getClauses('synonym')) {
            textual << clause.value?.toString()
        }
        builder.ext 'Synonyms', textual.join(', ')
    }

    private static void handleReplacedBy(CatalogueBuilder builder, Frame frame, Template idTemplate, Map<String, String> oboIdsToNames) {
        for (Clause clause in frame.getClauses('replaced_by')) {
            if (idTemplate) {
                builder.rel 'supersession' to builder.ref(getId(clause.value?.toString(), idTemplate))
            } else {
                builder.rel 'supersession' to oboIdsToNames[clause.value?.toString()]
            }
        }
    }

    private static String getId(oboId, Template idTemplate) {
        if (idTemplate) {
            StringWriter sw = new StringWriter()
            idTemplate.make(id: oboId).writeTo(sw)
            return sw.toString()
        }
        Iterable<ExtensionValue> extensionValues = ExtensionValue.findAllByNameAndExtensionValue(OBO_ID, oboId)
        if (!extensionValues) {
            extensionValues = []
        }
        for (ExtensionValue value in extensionValues) {
            if (value.element instanceof Model) {
                return value.element.modelCatalogueId ?: value.element.getDefaultModelCatalogueId(true)
            }
        }
        return null
    }

    private Template getIdTemplate(String mcIDPattern) {
        if (!mcIDPattern) {
            return null
        }
        engine.createTemplate(mcIDPattern)
    }

    private static String getName(Frame frame) {
        Object name = frame.getClause('name')

        if (!name) {
            throw new IllegalArgumentException("Frame ${frame.id} is missing the 'name' property")
        }

        return name.value.toString()
    }

    private static void handleDefAndComment(CatalogueBuilder builder, Frame frame) {
        String definition = frame.getClause('def')?.value?.toString()
        String comment = frame.getClause('comment')?.value?.toString()
        String description = [definition, comment].grep().join('\n\nComment:\n')
        if (description) {
            builder.description description
        }
    }

    private static void handleCreatedBy(CatalogueBuilder builder, Frame frame) {
        String createdBy = frame.getClause('created_by')?.value?.toString()
        if (createdBy) {
            builder.ext 'Created by', createdBy
        }
    }

    private static void handleCreatedDate(CatalogueBuilder builder, Frame frame) {
        String createdDate = frame.getClause('creation_date')?.value?.toString()
        if (createdDate) {
            builder.ext 'Creation Date', createdDate
        }
    }

    private static void handleNamespaceAndSubsets(CatalogueBuilder builder, Frame frame, Map<String, String> namespacesToClassification) {
        String namespace = frame.getClause('namespace').value?.toString()

        if (namespace) {
            builder.rel 'classification' from namespacesToClassification[namespace]
        }

        for (Clause clause in frame.getClauses('subset')) {
            String subset = clause.value?.toString()
            if (!subset) {
                continue
            }
            String classification = namespacesToClassification[subset]
            if (!classification) {
                continue
            }
            builder.rel 'classification' from classification
        }
    }

    private static void handleAltIds(CatalogueBuilder builder, Frame frame) {
        String value = concatClausesValues('alt_id', frame)
        if (!value) {
            return
        }
        builder.ext ALTERNATIVE_IDS, value
    }

    private static void handleXrefs(CatalogueBuilder builder, Frame frame) {
        String value = concatClausesValues('xref', frame)
        if (!value) {
            return
        }
        builder.ext 'xrefs',  value
    }

    private static String concatClausesValues(String tag, Frame frame) {
        Iterable<Clause> clauses = frame.getClauses(tag)
        if (!clauses) {
            return ''
        }
        clauses*.value*.toString().join(', ')
    }

    private static void handleStatusForObsoleteAndReplacedBy(CatalogueBuilder builder, Frame frame) {
        if (frame.getClause('is_obsolete') || frame.getClause('replaced_by')) {
            builder.status builder.deprecated
        }
    }

    private static void handlePropertyValues(CatalogueBuilder builder, Frame frame) {
        for (Clause clause in frame.getClauses('property_value')) {
            builder.ext clause.value.toString(), clause.value2.toString()
            if (clause.values.size() > 2) {
                builder.ext clause.value.toString() + ' Type', clause.values[2].toString()
            }
        }
    }
}
