package org.modelcatalogue.core.util.test

import grails.util.Metadata
import groovy.sql.Sql
import org.hibernate.SessionFactory
import groovy.util.logging.Log4j


@log4J
class TestDataHelper {

    /**
     * Runs the closure once and than caches the current state of the database in the temp folder so the database
     * will be exactly the same every time until the grails clean command is run or the mc folder in user's temp
     * folder is deleted
     *
     * @param sessionFactory
     * @param tempSqlFileName
     * @param initCode
     */
    static runOnceAndPreserve(SessionFactory sessionFactory, String tempSqlFileName, Closure initCode) {
        initDb(sessionFactory, false, tempSqlFileName, initCode)
    }

    static initFreshDb(SessionFactory sessionFactory, String tempSqlFileName, Closure initCode) {
        initDb(sessionFactory, true, tempSqlFileName, initCode)
    }

    private static initDb(SessionFactory sessionFactory, boolean drop, String tempSqlFileName, Closure initCode) {
        if (isH2(sessionFactory)) {
            return initCode()
        }

        //def tmp = ${System.getProperty('java.io.tmpdir')}

        def tmpLocation = "${System.getenv('MC_TMP_LOCATION')}/${Metadata.getCurrent().getApplicationName()}"
        def tmpDir = new File(tmpLocation)

        if( tmpDir.exists()  ){
            def result = tmpDir.deleteDir()  // Returns true if all goes well, false otherwise.
            println result
        }

        tmpLocation = System.getenv('MC_TMP_LOCATION')

        log.info  tmpLocation

        String scriptLocation = "${System.getenv('MC_TMP_LOCATION')}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/${tempSqlFileName}"

        log.info scriptLocation

        if (new File(scriptLocation).exists()) {
            long start = System.currentTimeMillis()
            new Sql(sessionFactory.currentSession.connection()).execute("RUNSCRIPT FROM ${scriptLocation}")
            sessionFactory.currentSession.clear()
            println "database restored from in ${scriptLocation} ${System.currentTimeMillis() - start} ms"
            return
        }

        long start = System.currentTimeMillis()

        if (drop) {
            String clearScriptLocation = "${System.getenv("MC_TMP_LOCATION")}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}/dropfiles/$tempSqlFileName"
            new Sql(sessionFactory.currentSession.connection()).execute("SCRIPT NODATA DROP TO ${clearScriptLocation}")
            println "Clear script created in $clearScriptLocation"
            new Sql(sessionFactory.currentSession.connection()).execute("RUNSCRIPT FROM ${clearScriptLocation}")
            println "Database cleared from $clearScriptLocation"
        }


        initCode()

        sessionFactory.currentSession.flush()

        new Sql(sessionFactory.currentSession.connection()).execute("SCRIPT DROP TO ${scriptLocation}")
        println "Data script created in $scriptLocation"
        println "database created in ${System.currentTimeMillis() - start} ms"
    }

    static boolean isH2(SessionFactory sessionFactory) {
        sessionFactory.currentSession.connection().metaData.databaseProductName != 'H2'
    }
}
