package org.modelcatalogue.core.util.test

import groovy.sql.Sql
import org.hibernate.SessionFactory

/**
 * Created by ladin on 18.02.15.
 */
class FreshData {

    static initFreshDb(SessionFactory sessionFactory, String tempSqlFileName, Closure initCode) {
        if (sessionFactory.currentSession.connection().metaData.databaseProductName != 'H2') {
            throw new IllegalStateException("Only H2 Database supported!")
        }

        String scriptLocation = "${System.getProperty('java.io.tmpdir')}/mc/${tempSqlFileName}"

        def sql = new Sql(sessionFactory.currentSession.connection())

        if (new File(scriptLocation).exists()) {
            long start = System.currentTimeMillis()
            sql.execute("RUNSCRIPT FROM ${scriptLocation}")
            sessionFactory.currentSession.clear()
            println "database restored from in ${scriptLocation} ${System.currentTimeMillis() - start} ms"
            return
        }

        long start = System.currentTimeMillis()
        String clearScriptLocation = "${System.getProperty('java.io.tmpdir')}/mc/dropfiles/$tempSqlFileName"
        sql.execute("SCRIPT NODATA DROP TO ${clearScriptLocation}")
        println "Clear script created in $clearScriptLocation"
        sql.execute("RUNSCRIPT FROM ${clearScriptLocation}")
        println "Database cleared from $clearScriptLocation"

        initCode()

        sessionFactory.currentSession.flush()

        sql.execute("SCRIPT DROP TO ${scriptLocation}")
        println "Data script created in $scriptLocation"
        println "database created in ${System.currentTimeMillis() - start} ms"
    }
}
