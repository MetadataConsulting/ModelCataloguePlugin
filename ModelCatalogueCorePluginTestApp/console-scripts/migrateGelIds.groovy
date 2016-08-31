import groovy.sql.Sql
import org.modelcatalogue.core.util.builder.BuildProgressMonitor

import javax.sql.DataSource
import java.util.concurrent.ExecutorService

DataSource dataSource = ctx.dataSource
Sql db = new Sql(dataSource)
ExecutorService executorService = ctx.getBean('executorService')

Long id = System.currentTimeMillis()
BuildProgressMonitor monitor = BuildProgressMonitor.create("Update GEL IDS ${new Date()}", id)

Map<Long, String> gelids = [:]

executorService.submit {
    db.eachRow ("""
        select
        x.element_id as elid, substring_index(x.extension_value, '/', -1) as gelid
        from extension_value x
        join catalogue_element c
        on c.id = x.element_id
        where x.name = 'http://www.modelcatalogue.org/metadata/genomics/#gel-id'
		and x.model_catalogue_id is null
    """) {
        gelids[it.elid] = it.gelid
        monitor.onNext("Fetched gel id $it.gelid for element $it.elid")
    }

    gelids.each { elid, gelid ->
        try {
            db.executeUpdate("""update catalogue_element c set c.model_catalogue_id = $gelid where c.id = $elid""")
            monitor.onNext("Updated element $elid to gel id $gelid")
        } catch (e) {
            monitor.onError(e)
        }
    }

    monitor.onCompleted()
}


"The progress can be displayed at ${config.grails.serverURL}/#/catalogue/feedback/$id"
