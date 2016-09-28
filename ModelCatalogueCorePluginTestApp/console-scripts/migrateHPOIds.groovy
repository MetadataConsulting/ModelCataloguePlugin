import groovy.sql.Sql
import org.modelcatalogue.core.util.builder.BuildProgressMonitor

import javax.sql.DataSource
import java.util.concurrent.ExecutorService

DataSource dataSource = ctx.dataSource
Sql db = new Sql(dataSource)
ExecutorService executorService = ctx.getBean('executorService')

Long id = System.currentTimeMillis()
BuildProgressMonitor monitor = BuildProgressMonitor.create("Update HPO OBO IDS ${new Date()}", id)

Map<Long, String> oboids = [:]

executorService.submit {
    db.eachRow ("""
        select
        x.element_id as elid, x.extension_value as oboid
        from extension_value x
        where x.name = 'OBO ID'
    """) {
        oboids[it.elid] = it.oboid
        monitor.onNext("Fetched obo id $it.oboid for element $it.elid")
    }

    oboids.each { elid, oboid ->
        try {
            db.executeUpdate("""update catalogue_element c set c.model_catalogue_id = $oboid where c.id = $elid""")
            monitor.onNext("Updated element $elid to obo id $oboid")
        } catch (e) {
            monitor.onError(e)
        }
    }

    monitor.onCompleted()
}


"The progress can be displayed at ${config.grails.serverURL}/#/catalogue/feedback/$id"
