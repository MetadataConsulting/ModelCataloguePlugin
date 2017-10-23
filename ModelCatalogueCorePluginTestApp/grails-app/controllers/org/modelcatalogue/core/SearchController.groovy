package org.modelcatalogue.core

import org.modelcatalogue.core.persistence.CatalogueElementGormService
import org.modelcatalogue.core.rx.LoggingSubscriber
import org.modelcatalogue.core.util.lists.Lists

import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.OK

class SearchController extends AbstractRestfulController<CatalogueElement> {

    static responseFormats = ['json', 'xml']

    ExecutorService executorService

    CatalogueElementGormService catalogueElementGormService

    SearchController() {
        super(CatalogueElement, true)
    }

    protected CatalogueElement findById(long id) {
        catalogueElementGormService.findById(id)
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
        if (!modelCatalogueSecurityService.hasRole("ADMIN", getDataModel())) {
            unauthorized()
            return
        }

        log.info "Reindexing search service ..."

        boolean soft = params.boolean('soft')
        executorService.submit {
                modelCatalogueSearchService.reindex(soft).toBlocking().subscribe(LoggingSubscriber.create(log, "... reindexing finished", "Error reindexing the catalogue"))
        }

        respond(success: true, status: OK)
    }

    protected setSafeMax(Integer max) {
        withFormat {
            json {
                params.max = Math.min(max ?: 10, 100)
            }
        }
    }
}
