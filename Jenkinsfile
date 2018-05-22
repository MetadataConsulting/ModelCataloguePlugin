pipeline {
  agent any
  stages {
    stage('Test Execute') {
      steps {
        dir(path: 'ModelCatalogueCorePluginTestApp') {
          wrap(delegate: [$class: 'Xvfb', screen: '1440x900x24']) {
            sh 'npm install'
            sh 'bower install'
            sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome org.modelcatalogue.core.finalized.*'
          }

        }

      }
    }
  }
}