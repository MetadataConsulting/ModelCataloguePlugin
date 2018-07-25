package org.modelcatalogue.core

import grails.transaction.Transactional
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct

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

        String trueString = 'true'

        return new MDXFeatures(
            northThamesFeatures: (grailsApplication.config.mdx.features.northThames == trueString),
            gelFeatures: (grailsApplication.config.mdx.features.gel == trueString)
        )
    }
}

class MDXFeatures {
    boolean northThamesFeatures
    boolean gelFeatures
}
