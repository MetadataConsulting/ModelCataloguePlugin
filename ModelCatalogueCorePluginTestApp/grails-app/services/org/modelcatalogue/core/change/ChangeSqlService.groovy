package org.modelcatalogue.core.change

import grails.transaction.Transactional
import grails.util.Environment
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.modelcatalogue.core.audit.Change
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.PaginationQuery
import org.modelcatalogue.core.util.SortQuery
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.ListWithTotalAndTypeImpl

import java.beans.MetaData
import java.sql.Connection
import java.sql.DatabaseMetaData

@CompileStatic
class ChangeSqlService {
    final String changeTable = Environment.current == Environment.TEST ? 'CHANGE' : '`change`'
    def dataSource

    @Transactional(readOnly = true)
    ListWithTotalAndType<Change> findAllByDataModelFilter(DataModelFilter dataModelFilter, PaginationQuery paginationQuery, SortQuery sortQuery) {
        List<GroovyRowResult> countRows = findAllRowsByDataModelFilter(dataModelFilter, "count(*) ")
        Long total = countRows ? countRows.first()[0] as int : 0
        List<GroovyRowResult> selectRows = findAllRowsByDataModelFilter(dataModelFilter, "${changeTable}.id ", paginationQuery, sortQuery)
        List<Long> ids = selectRows.collect { GroovyRowResult row -> row.getAt(0) as Long } as List<Long>
        List<Change> l = ids ? Change.where { id in (ids) }.list() : [] as List<Change>
        new ListWithTotalAndTypeImpl<Change>(Change, l, total)
    }

    @CompileDynamic
    Sql instantiateSql() {
        new Sql(dataSource)
    }

    List<GroovyRowResult> findAllRowsByDataModelFilter(DataModelFilter dataModels, String selectQuery, PaginationQuery paginationQuery = null, SortQuery sortQuery = null) {
        final Sql sql = instantiateSql()
        final String query = """\
SELECT ${selectQuery} 
FROM ${changeTable} WHERE 
  ${changeTable}.changed_id IN (${queryByDataModelFilter(dataModels)}) AND 
  ${changeTable}.parent_id IS NULL AND
  ${changeTable}.system <> true AND
  ${changeTable}.other_side <> true
"""
        if ( sortQuery ) {
            query += " ${sortQuery.toSQL()}".toString()
        }
        if ( paginationQuery ) {
            query += " ${paginationQuery.toSQL()}".toString()
        }
        query = query.replaceAll('\n', '')
        sql.rows(query, dataModelIds: queryMapByDataModelFilter(dataModels).join(','))
    }

    Set<Long> queryMapByDataModelFilter(DataModelFilter dataModels) {
        if (dataModels.unclassifiedOnly) {
            return [] as Set<Long>
        }
        if (dataModels.excludes && !dataModels.includes) {
            return dataModels.excludes
        }
        if (dataModels.excludes && dataModels.includes) {
            return [] as Set<Long>
        }
        if (dataModels.includes && !dataModels.excludes) {
            return dataModels.includes
        }
        return [] as Set<Long>
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
