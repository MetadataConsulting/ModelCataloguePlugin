import com.google.common.collect.ImmutableList
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovy.transform.Field
import org.hibernate.SessionFactory
import org.springframework.context.ApplicationContext

// ApplicationContext ctx = Holders.applicationContext

SessionFactory sessionFactory = ctx.getBean(SessionFactory)

@Field JsonSlurper slurper = new JsonSlurper()
@Field List<String> keysToRemove = ImmutableList.of('dateCreated',  'versionCreated', 'lastUpdated', 'classification', 'archived', 'ext', 'description', 'enumerations', 'internalModelCatalogueId', 'dataModel', 'modelCatalogueId')


1000.times {
    sessionFactory.currentSession.doWork { connection ->
        Sql sql = new Sql(connection)

        Map<Long, String> oldValues = [:]

        sql.eachRow("SELECT id, old_value FROM `change` WHERE old_value LIKE '%description%' limit 1000" ) { row ->
            trimValue(row, oldValues)
        }

        if (oldValues) {
            sql.withBatch(100) {
                oldValues.each {
                    sql.executeUpdate("UPDATE `change` c SET c.old_value = ? WHERE c.id = ?", [it.value, it.key] as Object[])
                }
            }
        }

        Map<Long, String> newValues = [:]

        sql.eachRow("SELECT id, new_value FROM `change` WHERE new_value LIKE '%description%' limit 1000") { row ->
            trimValue(row, newValues)
        }

        if (newValues) {
            sql.withBatch(100) {
                newValues.each {
                    sql.executeUpdate("UPDATE `change` c SET c.new_value = ? WHERE c.id = ?", [it.value, it.key] as Object[])
                }
            }
        }
    }
}




void trimValue(row, values) {
    String oldValue = row[1]
    try {
        Map<String, Object> oldValueJson = slurper.parseText(oldValue)
        oldValueJson.keySet().removeAll(keysToRemove)
        if (oldValueJson.source && oldValueJson instanceof Map) {
            oldValueJson.source.keySet().removeAll(keysToRemove)
        }
        if (oldValueJson.destination && oldValueJson instanceof Map) {
            oldValueJson.destination.keySet().removeAll(keysToRemove)
        }
        values[row[0]] = JsonOutput.toJson(oldValueJson)
    } catch (e) {
        e.printStackTrace()
    }
}
