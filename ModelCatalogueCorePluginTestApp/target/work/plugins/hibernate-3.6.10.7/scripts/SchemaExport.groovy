/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.util.GrailsUtil

import org.hibernate.tool.hbm2ddl.SchemaExport as HibernateSchemaExport

includeTargets << grailsScript('_GrailsBootstrap')

/**
 * @author Burt Beckwith
 */

target(schemaExport: 'Run Hibernate SchemaExport') {
    depends checkVersion, configureProxy, enableExpandoMetaClass, compile, bootstrap

    String filename = "${grailsSettings.projectTargetDir}/ddl.sql"
    boolean export = false
    boolean stdout = false

    for (arg in argsMap.params) {
        switch(arg) {
            case 'export':   export = true; break
            case 'generate': export = false; break
            case 'stdout':   stdout = true; break
            default:         filename = arg
        }
    }

    String datasourceSuffix = argsMap.datasource ? '_' + argsMap.datasource : ''

    def file = new File(filename)
    ant.mkdir dir: file.parentFile

    def sessionFactory = appCtx.getBean('&sessionFactory' + datasourceSuffix)
    def configuration = sessionFactory.configuration

    def schemaExport = new HibernateSchemaExport(configuration, sessionFactory.dataSource.connection)
        .setHaltOnError(true)
        .setOutputFile(file.path)
        .setDelimiter(';')

    String action = export ? "Exporting" : "Generating script to ${file.path}"
    String ds = argsMap.datasource ? "for DataSource '$argsMap.datasource'" : "for the default DataSource"
    println "$action in environment '$grailsEnv' $ds"

    schemaExport.execute stdout, export, false, false

    if (schemaExport.exceptions) {
        def e = schemaExport.exceptions[0]
        GrailsUtil.deepSanitize e
        e.printStackTrace()
    }
}

setDefaultTarget schemaExport
