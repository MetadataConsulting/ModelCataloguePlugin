package org.modelcatalogue.core

import grails.util.Environment

/**
 * Created by ladin on 26.06.14.
 */
class PostInitJob {

    def publishedElementService
    def importService

    static triggers = {
        simple name: 'postInitTrigger', startDelay: 10000, repeatInterval: 1000, repeatCount: 0
    }

    def description = "Init test data job"

    def execute(){
        if (Environment.current == Environment.DEVELOPMENT) {
            println 'Running post init job'
            println 'Importing data'
            importService.importData()
            def de = new DataElement(name: "testera", description: "test data architect").save(failOnError: true)
            de.ext.metadata = "test metadata"

            println 'Creating dummy models'
            15.times {
                new Model(name: "Another root #${String.format('%03d', it)}").save(failOnError: true)
            }

            def parentModel1 = Model.findByName("Another root #001")

            15.times{
                def child = new Model(name: "Another root #${String.format('%03d', it)}").save(failOnError: true)
                parentModel1.addToParentOf(child)
            }


            for (DataElement element in DataElement.list()) {
                parentModel1.addToContains element
            }


            println 'Finalizing all published elements'
            PublishedElement.list().each {
                it.status = PublishedElementStatus.FINALIZED
                it.save(failOnError: true)
            }

            println 'Creating history for NHS NUMBER STATUS INDICATOR CODE'
            def withHistory = DataElement.findByName("NHS NUMBER STATUS INDICATOR CODE")

            10.times {
                log.info "Creating archived version #${it}"
                publishedElementService.archiveAndIncreaseVersion(withHistory)
            }
            println "Post init job finished"
        }
    }
}
