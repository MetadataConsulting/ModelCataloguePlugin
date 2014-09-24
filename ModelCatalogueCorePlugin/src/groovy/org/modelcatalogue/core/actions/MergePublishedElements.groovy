package org.modelcatalogue.core.actions

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.PublishedElement
import org.modelcatalogue.core.PublishedElementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.ObjectError

/**
 * Action Runner to create new catalogue elements.
 */
class MergePublishedElements extends AbstractActionRunner {

    @Autowired PublishedElementService publishedElementService

    static String description = """
        Merges element 'source' into element 'destination'.

        The validity of 'source', 'destination' are validated immediately. Both elements need to be of the same type.

        The merge process is composed by following steps:
            New version of element 'destination' is created
            Relationship 'source' supersedes 'destination' is created
            Extension values which are not preset in 'destination' but exist in 'source' are migrated
            Non-system relationships which are not present by name in 'destination' but exist in 'source' are migrated.
            Non-system relationships which exist by name in 'source' and are already present in 'destination' are archived
            If any reference are present in one of 'source' or 'destination' it's set to 'destinations'
            If any reference is set in both 'source' or 'destination' to different values then error is returned
            If relationship on source belongs to same classification as the source and relationship with same name, type and direction on destination exists on destination, these two relationships are merged as well


        Parameters:
            source: the element to be merged into 'destination' and than archived in format 'gorm://<entity class name>:<id>'
            destination: the element to be merged with 'source' in format the destination of the relationship in format 'gorm://<entity class name>:<id>'
    """

    String getMessage() {
        normalizeDescription """Merge ${GrailsNameUtils.getNaturalName(source.class.simpleName)} <a target="_blank" href="#/catalogue/${GrailsNameUtils.getPropertyName(source.class.simpleName)}/${source.id}">${source.classifiedName}</a> into ${GrailsNameUtils.getNaturalName(destination.class.simpleName)} <a target="_blank" href="#/catalogue/${GrailsNameUtils.getPropertyName(destination.class.simpleName)}/${destination.id}">${destination.classifiedName}</a> including all related elements having at least one classification as the source"""
    }

    @Override
    Map<String, String> validate(Map<String, String> params) {
        Map<String, String> ret = [:]

        Object source = decodeEntity(params.source)
        Object destination = decodeEntity(params.destination)

        if (!source) {
            ret.source = 'Missing source'
        }

        if (!destination) {
            ret.destination = 'Missing destination'
        }


        if (source && !(source instanceof PublishedElement)) {
            ret.source = 'Source must be published element'
        }

        if (destination && !(destination instanceof PublishedElement)) {
            ret.destination = 'Destination must be published element'
        }

        if (source?.class != destination?.class) {
            ret.destination = 'Destination must be of the same type as the source'
        }

        ret.putAll super.validate(params)

        ret
    }

    @Override void run() {
        PublishedElement merged = merge()
        if (!merged.hasErrors()) {

            out << """Merged ${GrailsNameUtils.getNaturalName(source.class.simpleName)} <a target="_blank" href="#/catalogue/${GrailsNameUtils.getPropertyName(source.class.simpleName)}/${source.id}">${source.classifiedName}</a> into ${GrailsNameUtils.getNaturalName(destination.class.simpleName)} <a target="_blank" href="#/catalogue/${GrailsNameUtils.getPropertyName(destination.class.simpleName)}/${destination.id}">${destination.classifiedName}</a>"""
            result = encodeEntity merged
        } else {
            fail("""Unable to merge ${GrailsNameUtils.getNaturalName(source.class.simpleName)} ${source.classifiedName} into ${GrailsNameUtils.getNaturalName(destination.class.simpleName)} ${destination.classifiedName}""")
            printErrors(merged.errors.allErrors)
        }
    }


    public <E extends PublishedElement> E merge() {
        if (source && destination) {
            publishedElementService.merge(source as PublishedElement, destination as PublishedElement)
        } else {
            throw new IllegalStateException("The action wasn't initialized yet with the valid parameters")
        }
    }

    def getSource() {
        decodeEntity(parameters.source)
    }

    def getDestination() {
        decodeEntity(parameters.destination)
    }

    @Override
    List<String> getRequiredParameters() {
        ['source', 'destination']
    }
}
