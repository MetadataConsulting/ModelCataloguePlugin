package org.modelcatalogue.core.dataarchitect

import org.biojava3.ontology.io.OboParser
import org.biojava3.ontology.Ontology
import org.biojava3.ontology.Term
import org.biojava3.ontology.Triple
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.ExtensionValue
import org.modelcatalogue.core.Model
import org.modelcatalogue.core.PublishedElementStatus


class OBOService {

    static transactional = false

    private static final String OBO_ID = 'OBO ID'

    def importOntology(InputStream is, String name, String description) {
        Classification classification = Classification.findOrCreateByName(name)

        OboParser parser = new OboParser()

        Ontology ontology = parser.parseOBO(new BufferedReader(new InputStreamReader(is)), name, description)

        Set<Term> relationshipTypes = [] as Set
        Set<Term> models            = [] as Set

        for (Term term in ontology.terms) {
            if (term instanceof Triple) {
                relationshipTypes << term.predicate
            } else {
                models << term
            }
        }

        models.eachWithIndex { Term term, int i ->
            log.info "[${i.toString().padLeft(6, '0')}/${models.size().toString().padLeft(6, '0')}] Importing term: ${term.name}: ${term.description} (${term.annotation.asMap().def})"
            ExtensionValue ext = ExtensionValue.findByNameAndExtensionValue(OBO_ID, term.name)

            if (ext) {
                if (!(ext.element instanceof Model)) {
                    log.warn("Expected ${ext.element} to be a model")
                } else {
                    Model model = ext.element
                    // TODO: check for updates, bump version if needed
                }
            } else {
                Model model = new Model(name: term.description ?: term.name, description: term.annotation.asMap().def, status: PublishedElementStatus.PENDING)
                model.save(failOnError: true)
                model.ext[OBO_ID] = term.name
                term.annotation.asMap().each {key, value ->
                    if (key == 'def') {
                        return
                    }
                    model.ext[key?.toString()] = value?.toString()
                }
            }
        }

        ontology.terms.eachWithIndex { Term term, int i ->
            log.info "[${i.toString().padLeft(6, '0')}/${ontology.terms.size().toString().padLeft(6, '0')}] Importing term: ${term.name}: ${term.description} (${term.annotation.asMap().def})"
            if (term instanceof Triple) {
                if (term.predicate.name == 'is_a') {
                    Model parent = ExtensionValue.findByNameAndExtensionValue(OBO_ID, term.object.name)?.element
                    Model child = ExtensionValue.findByNameAndExtensionValue(OBO_ID, term.subject.name)?.element
                    if (!parent) {
                        log.warn("Model ${term.object.description} (${term.object.name}) is missing in the catalogue")
                        return
                    }
                    if (!child) {
                        log.warn("Model ${term.subject.description} (${term.object.name}) is missing in the catalogue")
                        return
                    }

                    parent.addToParentOf child
                }
            }
        }

    }
}
