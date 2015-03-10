import grails.util.BuildScope

scriptScope = BuildScope.WAR

includeTargets << grailsScript("_GrailsWar")

ant.taskdef(name: "deploy",   classname: "org.apache.catalina.ant.DeployTask")
ant.taskdef(name: "list",     classname: "org.apache.catalina.ant.ListTask")
ant.taskdef(name: "undeploy", classname: "org.apache.catalina.ant.UndeployTask")

target(tomcat: '''\
Script used to interact with remote Tomcat. The following subcommands are available:

grails tomcat deploy - Deploy to a tomcat server
grails tomcat undeploy - Undeploy from a tomcat server
''') {

    depends(parseArguments, compile, createConfig)

    String cmd = argsMap.params ? argsMap.params[0] : 'deploy'
    argsMap.params.clear()
    String user = config.tomcat.deploy.username ?: 'manager'
    String pass = config.tomcat.deploy.password ?: 'secret'
    String url = config.tomcat.deploy.url ?: 'http://localhost:8080/manager'

    switch (cmd) {
        case 'deploy':
            war()
            println "Deploying application $serverContextPath to Tomcat"
            deploy(war: warName, url: url, path: serverContextPath, username: user, password: pass)
            break

        case 'list':
            list(url: url, username: user, password: pass)
            break

        case 'undeploy':
            configureServerContextPath()
            println "Undeploying application $serverContextPath from Tomcat"
            println '''\
NOTE: If you experience a classloading error during undeployment you need to take the following step:

* Pass this system argument to Tomcat: -Dorg.apache.catalina.loader.WebappClassLoader.ENABLE_CLEAR_REFERENCES=false

See http://tomcat.apache.org/tomcat-7.0-doc/config/systemprops.html for more information
'''
            undeploy(url: url, path: serverContextPath, username: user, password: pass)
    }
}

setDefaultTarget "tomcat"
