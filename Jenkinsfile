pipeline {
  options {
    disableConcurrentBuilds()
  }
  agent any
  stages {
    stage('Test Execute') {
      steps {
        dir(path: 'ModelCatalogueCorePluginTestApp') {
		sh 'npm install'
	        sh 'bower install'
          	wrap([$class: 'Xvfb']) {
            		sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome -DdownloadFilepath=/home/ubuntu -Dwebdriver.chrome.driver=/opt/chromedriver  org.modelcatalogue.core.finalized.*'
         	 }

        }
      }
    }
  }
}
