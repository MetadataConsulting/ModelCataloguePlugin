package org.modelcatalogue.core.dataarchitect

import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.obolibrary.oboformat.model.Clause
import org.obolibrary.oboformat.model.Frame
import org.obolibrary.oboformat.model.OBODoc
import org.obolibrary.oboformat.parser.OBOFormatParser

import javax.xml.bind.DatatypeConverter


class OBOService {

    static transactional = false

    def relationshipService
    def sessionFactory

    private final String OBO_ID = 'OBO ID'
    private final String DEFAULT_CLASSIFICATION = '__DEFAULT_CLASSIFICATION__'
    private final String ALTERNATIVE_IDS = 'Alternative IDs'

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

        Map<String, Classification> classificationsMap = findOrCreateClassifications(name, document.headerFrame)

        Template idTemplate = null
        if (mcIDPattern) {
            SimpleTemplateEngine engine = new SimpleTemplateEngine()
            idTemplate = engine.createTemplate(mcIDPattern)
        }

        Map<String, Model> models = findOrCreateModelsFromTermFrames(classificationsMap, document.termFrames, idTemplate)
        createRelationshipsFromTermFrames models, document.termFrames
        publishModelsAsDraft models.values()
        log.info "Import finished for ${name}"

        classificationsMap[DEFAULT_CLASSIFICATION]
    }

    private void publishModelsAsDraft(Collection<Model> models) {
        log.info "Publishing models as draft"
        models.eachWithIndex { Model model, Integer i ->
            if (model.status != PublishedElementStatus.PENDING) {
                // obsolete models
                return
            }
            log.info "[${(i + 1).toString().padLeft(6, '0')}/${models.size().toString().padLeft(6, '0')}] Publishing model ${model.ext[OBO_ID]}: ${model.name} as DRAFT"
            model.status = PublishedElementStatus.DRAFT
            model.save(failOnError: true)
            if (i % 1000 == 0) {
                cleanUpGorm()
            }
        }
    }

    private void createRelationshipsFromTermFrames(Map<String, Model> models, Collection<Frame> frames) {
        log.info "Creating relationships for terms"
        frames.eachWithIndex { Frame frame, Integer i ->
            log.info "[${(i + 1).toString().padLeft(6, '0')}/${frames.size().toString().padLeft(6, '0')}] Importing relations for model ${frame.id}: ${frame.getClause('name')?.value}"
            addRelationFor(models, frame)
            if (i % 1000 == 0) {
                cleanUpGorm()
            }
        }
    }

    private void addRelationFor(Map<String, Model> models, Frame frame) {
        Model model = models[frame.id]

        if (!model) {
            throw new IllegalArgumentException("Model for  ${frame.id}: ${frame.getClause('name')?.value} does not exist")
        }

        handleIsA(model, models, frame)
        handleSynonym(model, models, frame)
        handleReplacedBy(model, models, frame)
    }

    private void handleIsA(Model model, Map<String, Model> models, Frame frame) {
        List<String> textual = []
        for (Clause clause in frame.getClauses('is_a')) {
            String value = clause.value?.toString()
            Model other = models[value]
            if (!other) {
                textual << value
            } else {
                model.addToChildOf other
            }
        }
        if (textual) {
            model.ext['Is a'] = textual.join(', ')
        }
    }

    private void handleSynonym(Model model, Map<String, Model> models, Frame frame) {
        RelationshipType synonym = RelationshipType.findByName('synonym')
        RelationshipType relation = RelationshipType.findByName('relatedTo')

        List<String> textual = []

        for (Clause clause in frame.getClauses('synonym')) {
            String value = clause.value?.toString()
            Model other = models[value]
            if (!other) {
                textual << value
            } else {
                if (clause.value2?.toString() == 'EXACT') {
                    if (relationshipService.link(model, other, synonym).hasErrors()) {
                        log.warn("Errors creating synonym ${model} to ${other}" )
                    }
                } else {
                    Relationship rel = relationshipService.link(model, other, relation)
                    if (!rel.hasErrors()) {
                        rel.ext['precision'] = clause.value2?.toString()
                    } else {
                        log.warn("Errors creating relation ${model} to ${other}" )
                    }
                }
            }
        }
        if (textual) {
            model.ext['Synonyms'] = textual.join(', ')
        }
    }

    private void handleReplacedBy(Model model, Map<String, Model> models, Frame frame) {
        List<String> textual = []
        for (Clause clause in frame.getClauses('replaced_by')) {
            String value = clause.value?.toString()
            Model newOne = models[value]
            if (!newOne) {
                textual << value
            } else {
                model.addToSupersededBy newOne
            }
        }
        if (textual) {
            model.ext['Replaced by'] = textual.join(', ')
        }
    }

    private Map<String, Model> findOrCreateModelsFromTermFrames(Map<String, Classification> classificationMap, Collection<Frame> termFrames, Template idTemplate) {
        log.info "Creating models for terms"
        Map<String, Model> modelsMap = [:]

        termFrames.eachWithIndex { Frame frame, Integer i ->
            log.info "[${(i + 1).toString().padLeft(6, '0')}/${termFrames.size().toString().padLeft(6, '0')}] Importing model ${frame.id}: ${frame.getClause('name')?.value}"
            modelsMap[frame.id] = findOrCreateModelFromTermFrame classificationMap, frame, idTemplate
            if (i % 1000 == 0) {
                cleanUpGorm()
            }
        }

        modelsMap
    }

    private Model findOrCreateModelFromTermFrame(Map<String, Classification> classificationMap, Frame frame, Template idTemplate) {
        Model model = findModelByOBOID(frame, idTemplate)

        handleDefAndComment(model, frame)
        handleCreatedBy(model, frame)
        handleCreatedDate(model, frame)
        handleNamespaceAndSubsets(model, frame, classificationMap)
        handleAltIds(model, frame)
        handleXrefs(model, frame)
        handleStatusForObsoleteAndReplacedBy(model, frame)
        handlePropertyValues(model, frame)

        model
    }

    private void handleDefAndComment(Model model, Frame frame) {
        String definition = frame.getClause('def')?.value?.toString()
        String comment = frame.getClause('comment')?.value?.toString()
        String description = [definition, comment].grep().join('\n\nComment:\n')
        if (description) {
            model.description = description
        }
    }

    private void handleCreatedBy(Model model, Frame frame) {
        String createdBy = frame.getClause('created_by')?.value?.toString()
        if (createdBy) {
            model.ext['Created by'] = createdBy
        }
    }

    private void handleCreatedDate(Model model, Frame frame) {
        String createdDate = frame.getClause('creation_date')?.value?.toString()
        if (createdDate) {
            model.dateCreated = DatatypeConverter.parseDate(createdDate).getTime()
        }
    }

    private void handleNamespaceAndSubsets(Model model, Frame frame, Map<String, Classification> classificationMap) {
        Classification defaultClassification = classificationMap[DEFAULT_CLASSIFICATION]
        if (defaultClassification) {
            model.addToClassifications(defaultClassification)
            defaultClassification.addToClassifies(model)
        }

        String namespace = frame.getClause('namespace').value?.toString()
        if (namespace) {
            Classification namespaceClassification = classificationMap[namespace]
            model.addToClassifications(namespaceClassification)
            namespaceClassification.addToClassifies(model)
        }

        for (Clause clause in frame.getClauses('subset')) {
            String subset = clause.value?.toString()
            if (!subset) {
                continue
            }
            Classification classification = classificationMap[subset]
            if (!classification) {
                continue
            }
            model.addToClassifications(classification)
            classification.addToClassifies(model)
        }
    }

    private void handleAltIds(Model model, Frame frame) {
        String value = concatClausesValues('alt_id', frame)
        if (!value) {
            return
        }
        model.ext[ALTERNATIVE_IDS] = value
    }

    private void handleXrefs(Model model, Frame frame) {
        String value = concatClausesValues('xref', frame)
        if (!value) {
            return
        }
        model.ext['xrefs'] = value
    }

    private String concatClausesValues(String tag, Frame frame) {
        Iterable<Clause> clauses = frame.getClauses(tag)
        if (!clauses) {
            return ''
        }
        clauses*.value*.toString().join(', ')
    }

    private void handleStatusForObsoleteAndReplacedBy(Model model, Frame frame) {
        if (frame.getClause('is_obsolete') || frame.getClause('replaced_by')) {
            model.status = PublishedElementStatus.DEPRECATED
        }
    }

    private void handlePropertyValues(Model model, Frame frame) {
        for (Clause clause in frame.getClauses('property_value')) {
            model.ext[clause.value.toString()] = clause.value2.toString()
            if (clause.values.size() > 2) {
                model.ext[clause.value.toString() + ' Type'] = clause.values[2].toString()
            }
        }
    }

    private Model findModelByOBOID(Frame frame, Template idTemplate) {
        String mcid = null

        if (idTemplate) {
            StringWriter sw = new StringWriter()
            idTemplate.make(id: frame.id).writeTo(sw)
            mcid = sw.toString()
        }

        if (!mcid) {
            Iterable<ExtensionValue> extensionValues = ExtensionValue.findAllByNameAndExtensionValue(OBO_ID, frame.id)
            if (!extensionValues) {
                extensionValues = []
            }
            for (ExtensionValue value in extensionValues) {
                if (value.element instanceof Model) {
                    return value.element as Model
                }
            }
        } else {
            Model existing = Model.findByModelCatalogueId(mcid)
            if (existing) {
                return existing
            }
        }



        Object name = frame.getClause('name')

        if (!name) {
            throw new IllegalArgumentException("Frame ${frame.id} is missing the 'name' property")
        }

        Model model = new Model(name: name.value.toString(), status: PublishedElementStatus.PENDING, modelCatalogueId: mcid).save(failOnError: true)
        model.ext[OBO_ID] = frame.id
        model
    }

    /**
     * Returns map of classification by namespace.
     * @param ontologyName name of the ontology (used as name of the default namespace)
     * @param headerFrame header frame of the document
     * @return map of classification by namespace
     */
    private Map<String, Classification> findOrCreateClassifications(String ontologyName, Frame headerFrame) {
        Map<String, Classification> classificationMap = [:]
        String defaultNamespace = headerFrame.getClause('default-namespace')?.value?.toString()
        if (defaultNamespace) {
            Classification defaultClassification = findOrCreateClassification(ontologyName, defaultNamespace)
            classificationMap[defaultNamespace] = defaultClassification
            classificationMap[DEFAULT_CLASSIFICATION] = defaultClassification
        }

        for (Clause clause in headerFrame.getClauses('subsetdef')) {
            String namespace = clause.value.toString()
            classificationMap[namespace] = findOrCreateClassification(clause.value2.toString(), namespace)
        }

        classificationMap
    }

    /**
     * Returns existing classification or new one with given name and namespace.
     * @param name the name of the newly created classification
     * @param namespace the namespace of the newly created classification
     * @return existing classification or new one with given name and namespace
     */
    private Classification findOrCreateClassification(String name, String namespace) {
        Classification classification = Classification.findByNamespace(namespace)
        if (classification) {
            return classification
        }
        new Classification(name: name, namespace: namespace).save(failOnError: true)
    }

    /**
     * Clean up the session to speed up the import.
     * @see http://naleid.com/blog/2009/10/01/batch-import-performance-with-grails-and-mysql
     */
    def cleanUpGorm() {
        // XXX: this made classifications in map fail to persist classifies set
//        def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP
//        def session = sessionFactory.currentSession
//        session.flush()
//        session.clear()
//        propertyInstanceMap.get().clear()
    }

//    private void collectCauseTypes(Collection<Frame> frames, Map<String, Map<String, Object>> clausesTypes) {
//        for (Frame frame in frames) {
//            for (Clause clause in frame.clauses) {
//                def type = clausesTypes[clause.tag]
//                type.count++
//                type.example = clause.properties
//            }
//        }
//    }
//
//    private void prettyPrintTypes(Map<String, Map<String, Object>> clauseTypes) {
//        for(Map<String, Object> type in clauseTypes.values())
//        println """
//        ${type.example.tag}
//            count:              ${type.count} in Human Phenotype (2014-10-21)
//            values:             ${type.example.values}
//            xrefs:              ${type.example.xrefs}
//            qualifier values:   ${type.example.qualifierValues}
//        """.stripIndent()
//    }
}
