package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired

@Transactional
class MDXFeaturesService {

    @Autowired GrailsApplication grailsApplication

// if we want to get it one-off

//    final MDXFeatures mdxFeatures
//
//    @PostConstruct
//    private void init() {
//        this.mdxFeatures = new MDXFeatures(
//            northThamesFeatures: grailsApplication.config.mdx.features.northThames,
//            gelFeatures: grailsApplication.config.mdx.features.gel
//        )
//    }

    /**
     * Get grails application config values dynamically.
     * Maybe better to get them one-off in a PostConstruct?
     * @return
     */
    MDXFeatures getMDXFeatures() {
        return MDXFeaturesEnum.createFeatureObj(grailsApplication)

    }


}

/**
 * Object representing the settings of features.
 * The names of the fields should correspond to the configNames in MDXFeaturesEnum as described in the docstring of configName.
 */
class MDXFeatures {
    boolean northThamesFeatures
    boolean gelFeatures


}

/**
 * Enumeration of possible features.
 *
 */
enum MDXFeaturesEnum {
    VANILLA(''),
    NORTH_THAMES('northThames'),
    GEL('gel')

    /**
     * grailsApplication.config.mdx.features[configName] will be the config variable checked.
     * The corresponding field in MDXFeatures object should be "${configName}Features"
     */
    final String configName

    MDXFeaturesEnum(String configName) {
        this.configName = configName
    }

    boolean trueFromConfig(GrailsApplication grailsApplication) {

        return grailsApplication.config.mdx.features[configName] == 'true'
    }

    /**
     * Values (except VANILLA) as map to be put as argument for MDXFeatures constructor.
     * @param grailsApplication
     * @return
     */
    static Map<String,Boolean> asMap(GrailsApplication grailsApplication) {
        (values() - [VANILLA]).collectEntries {
            [(it.configName + 'Features'): it.trueFromConfig(grailsApplication)]
        }
    }

    static MDXFeatures createFeatureObj(GrailsApplication grailsApplication) {
        return new MDXFeatures(
            asMap(grailsApplication)
        )
    }
}
