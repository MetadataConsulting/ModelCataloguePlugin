/*
 * Copyright 2013 SpringSource
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
package org.grails.plugins.tomcat.fork

import grails.util.BuildSettings
import grails.web.container.EmbeddableServer
import groovy.transform.CompileStatic

import org.codehaus.groovy.grails.cli.fork.ExecutionContext
import org.codehaus.groovy.grails.cli.fork.ForkedGrailsProcess

/**
 * Execution context for the forked Tomcat container
 *
 * @author Graeme Rocher
 * @since 2.3
 */
@CompileStatic
class TomcatExecutionContext extends ExecutionContext {
    private static final long serialVersionUID = 1

    String contextPath
    String host = EmbeddableServer.DEFAULT_HOST
    int port = EmbeddableServer.DEFAULT_PORT
    int securePort
    String warPath

    @Override
    protected List<File> buildMinimalIsolatedClasspath(BuildSettings buildSettings) {
        final buildDependencies = super.buildMinimalIsolatedClasspath(buildSettings)
        final tomcatJars = process.findSystemClasspathJars(buildSettings)
        buildDependencies.addAll(tomcatJars.findAll { File f -> !f.name.contains('juli')})

        return buildDependencies
    }
}
