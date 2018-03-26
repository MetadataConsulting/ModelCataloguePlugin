package org.modelcatalogue.core.change

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl

class ChangeSqlService {

    def dataSource

    ListWithTotalAndType<Change> findAllByDataModelFilter(DataModelFilter dataModelFilter, PaginationQuery paginationQuery, SortQuery sortQuery) {
        List<GroovyRowResult> countRows = findAllRowsByDataModelFilter(dataModelFilter, "count(*) ")
        int total = countRows ? countRows.first()[0] as int : 0
        List<GroovyRowResult> selectRows = findAllRowsByDataModelFilter(dataModelFilter, "`change`.id ", paginationQuery, sortQuery)
        List<Long> ids = selectRows.collect { it[0] }
        List<Change> l =Change.where {
            id in (ids)
        }.list()
        new ListWithTotalAndTypeImpl<Change>(Change, l, total)
    }

    List<GroovyRowResult> findAllRowsByDataModelFilter(DataModelFilter dataModels, String selectQuery, PaginationQuery paginationQuery = null, SortQuery sortQuery = null) {
        final Sql sql = new Sql(dataSource)
        final String query = """\
select ${selectQuery} 
FROM `change`
WHERE
  (
    `change`.changed_id IN (${queryByDataModelFilter(dataModels)})
  )
  and
  `change`.parent_id is null and
  `change`.system<>true and
  `change`.other_side<>true
"""
        if ( paginationQuery ) {
            query += " ${paginationQuery.toSQL()}".toString()
        }
        if ( sortQuery ) {
            query += " ${sortQuery.toSQL()}".toString()
        }
        query = query.replaceAll('\n', '')

        Map params = queryMapByDataModelFilter(dataModels)
        sql.rows(query, params)
    }

    Map queryMapByDataModelFilter(DataModelFilter dataModels) {
        if (dataModels.unclassifiedOnly) {
            return [:]
        }
        if (dataModels.excludes && !dataModels.includes) {
            return [dataModelIds: dataModels.excludes]
        }
        if (dataModels.excludes && dataModels.includes) {
            return [:]
        }
        if (dataModels.includes && !dataModels.excludes) {
            return [dataModelIds: dataModels.includes]
        }
        return [:]
    }

    String queryByDataModelFilter(DataModelFilter dataModels) {
        if (dataModels.unclassifiedOnly) {
            return '''\
SELECT id
FROM catalogue_element
WHERE data_model_id is null
'''
        }
        if (dataModels.excludes && !dataModels.includes) {
            return '''\
SELECT id
    FROM catalogue_element
    WHERE catalogue_element.data_model_id not in (:dataModelIds) or catalogue_element.id not in (:dataModelIds)
'''
        }
        if (dataModels.excludes && dataModels.includes) {
            throw new IllegalStateException("Combining exclusion and inclusion is no longer supported. Exclusion would be ignored!")
        }
        if (dataModels.includes && !dataModels.excludes) {
            return '''\
SELECT id
    FROM catalogue_element
    WHERE catalogue_element.data_model_id in (:dataModelIds) or catalogue_element.id in (:dataModelIds)
'''
        }
    }
}
