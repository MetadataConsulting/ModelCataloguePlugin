pipeline {
	agent any
	stages{
		stage('Test Execute') {
			steps{
				dir ('ModelCatalogueCorePluginTestApp'){
					wrap([$class: 'Xvfb', screen: '1440x900x24']) {
	                                	sh 'npm install'
        	                        	sh 'bower install'
                	                	sh '/opt/grails/bin/grails test-app -DdownloadFilepath=/home/ubuntu -Dserver.port=8081 -Dgeb.env=chrome -Dwebdriver.chrome.driver=/opt/chromedriver org.modelcatalogue.core.finalized.*'
					}
				}
			}
		}
	}
}
