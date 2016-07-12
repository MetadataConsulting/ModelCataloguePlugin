package org.modelcatalogue.core

import org.modelcatalogue.core.rx.LoggingSubscriber
import org.modelcatalogue.core.util.lists.Lists

import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.OK

class SearchController extends AbstractRestfulController<CatalogueElement> {

    static responseFormats = ['json', 'xml', 'xlsx']

    ExecutorService executorService

    SearchController() {
        super(CatalogueElement, true)
    }

    def index(Integer max) {
        setSafeMax(max)

        if (!params.search) {
            respond errors: "No query string to search on"
            return
        }

        respond Lists.wrap(params, "/search/?search=${params.search.encodeAsURL()}", modelCatalogueSearchService.search(params))

    }

    /**
     * Toggle reindexing the catalogue. Use wisely, can take along time.
     * @return
     */
    def reindex() {
        if (!modelCatalogueSecurityService.hasRole("ADMIN")) {
            unauthorized()
            return
        }

        log.info "Reindexing search service ..."

        boolean soft = params.boolean('soft')
        executorService.submit {
            profile(excludeMethods: ['rx.observables.BlockingObservable.subscribe', 'groovyx.gprof.Profiler.stop']) {
                modelCatalogueSearchService.reindex(soft).toBlocking().subscribe(LoggingSubscriber.create(log, "... reindexing finished", "Error reindexing the catalogue"))
            }.prettyPrint()
        }

        respond(success: true, status: OK)
    }

    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
            xlsx {
                params.max = Math.min(max ?: 10000, 10000)
            }
        }
    }
}
