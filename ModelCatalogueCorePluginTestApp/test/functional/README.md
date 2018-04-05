To run all functional tests: 

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.finalized.*

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.regression.*

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.remoteTesting.*

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.sanityTestSuite.*

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.secured.*

grails test-app -DdownloadFilepath=/Users/sdelamo/Downloads -Djava.io.tmpdir=/Users/sdelamo/Downloads -Dgeb.env=chrome -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver functional: org.modelcatalogue.core.suiteA.*