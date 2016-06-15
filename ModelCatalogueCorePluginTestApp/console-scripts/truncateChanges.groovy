import com.google.common.collect.ImmutableList
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql
import groovy.transform.Field
import org.hibernate.SessionFactory
import org.modelcatalogue.core.util.builder.BuildProgressMonitor

SessionFactory sessionFactory = ctx.getBean(SessionFactory)

@Field JsonSlurper slurper = new JsonSlurper()
@Field List<String> keysToRemove = ImmutableList.of('dateCreated', 'versionCreated', 'lastUpdated', 'classification', 'archived', 'ext', 'description', 'enumerations', 'internalModelCatalogueId', 'modelCatalogueId')
@Field List<String> nestedToClean = ImmutableList.of('source', 'destination', 'dataModel', 'dataType', 'measurementUnit', 'unitOfMeasure', 'relationship', 'element')

BuildProgressMonitor monitor = BuildProgressMonitor.create('Truncate changes', 123)

1000.times { count ->
    monitor.onNext("Running $count iteration")
    sessionFactory.currentSession.doWork { connection ->
        Sql sql = new Sql(connection)

        Map<Long, List<String>> values = [:].withDefault { [] }

        sql.eachRow("SELECT id, old_value, new_value FROM `change` limit 1000 offset ${count * 1000}") { row ->
            trimValues(row, values)
        }

        monitor.onNext("Starting update for ${values.size()} values")

        if (values) {
            sql.withBatch(100) {
                values.each {
                    try {
                        sql.executeUpdate("UPDATE `change` c SET c.old_value = ?, c.new_value = ? WHERE c.id = ?", [it.value[0]?.toString(), it.value[1]?.toString(), it.key.longValue()] as Object[])
                    } catch (e) {
                        monitor.onNext("Error executing update for $it")
                        log.warn "Error executing update for $it", e
                    }

                }
            }
        }
    }
    monitor.onCompleted()
}


void trimValues(row, values) {
    try {
        values[row[0]][0] = trimValue(row[1])
        values[row[0]][1] = trimValue(row[2])
    } catch (e) {
        e.printStackTrace()
    }
}

private String trimValue(String oldValue) {
    if (!oldValue) {
        return oldValue
    }

    def oldValueJsonAny = slurper.parseText(oldValue)
    if (!oldValueJsonAny) {
        return oldValue
    }

    if (!(oldValueJsonAny instanceof Map)) {
        return oldValue
    }

    Map<String, Object> oldValueJson = oldValueJsonAny

    oldValueJson.keySet().removeAll(keysToRemove)

    deepCleanNested(oldValueJson)

    JsonOutput.toJson(oldValueJson)
}

private deepCleanNested(Map<String, Object> oldValueJson) {
    nestedToClean.each {
        cleanNested(oldValueJson, it)
    }
}

private void cleanNested(Map<String, Object> oldValueJson, String key) {
    if (oldValueJson.containsKey(key) && oldValueJson.get(key) instanceof Map) {
        oldValueJson.get(key).keySet().removeAll(keysToRemove)
        deepCleanNested(oldValueJson.get(key) as Map)
    }
}
