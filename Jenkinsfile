pipeline {
	agent any
	stages{
		stage('Test Execute') {
			steps{
				cd ModelCatalogueCorePluginTestApp
				npm install
				bower install
				/opt/grails/bin/grails test-app -DdownloadFilepath=/home/ubuntu -Dserver.port=8081 -Dgeb.env=chrome -Dwebdriver.chrome.driver=/opt/chromedriver org.modelcatalogue.core.finalized.*
			}
		}
	}
}
