package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication

import javax.annotation.PostConstruct

@Transactional
class MDXFeaturesService {

    GrailsApplication grailsApplication

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
        return new MDXFeatures(
            northThamesFeatures: (grailsApplication.config.mdx.features.northThames == 'true'),
            gelFeatures: (grailsApplication.config.mdx.features.gel == 'true')
        )
    }
}

class MDXFeatures {
    boolean northThamesFeatures
    boolean gelFeatures
}
