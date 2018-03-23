package org.modelcatalogue.core.changes

import grails.gorm.DetachedCriteria
import grails.transaction.Transactional
import groovy.transform.CompileStatic
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.audit.Change

@CompileStatic
class ChangesController {

    @Transactional(readOnly = true)
    def index(ChangesIndexCommand cmd) {
        DetachedCriteria<Change> query = Change.where {
            parentId == null &&
            system != true &&
            otherSide != true &&
                    ( changedId in (CatalogueElement.where { id == cmd.dataModelId  }.id()) ||
                      changedId in (CatalogueElement.where { dataModel.id == cmd.dataModelId  }.id())
                    )
        }
        Map m = cmd.toPaginationQuery()?.toMap() ?: Collections.emptyMap()
        m += (cmd.toSortQuery()?.toMap() ?: Collections.emptyMap())
        List<Change> changesList = query.list(m)
        [changesList: changesList]
    }
}
