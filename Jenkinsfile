pipeline {
  agent any
  options {
    disableConcurrentBuilds()
  }
  stages {
    stage('Test Execute') {
      steps {
        dir(path: 'ModelCatalogueCorePluginTestApp') {
            echo "Currently I am running $BRANCH_NAME
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
