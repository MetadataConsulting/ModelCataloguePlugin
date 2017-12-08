To run all functional tests: 

test-app functional: -Dgeb.env=chromeHeadless -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver -Dspock.ignore.suiteA=true -Dspock.ignore.secured=true

test-app functional: -Dgeb.env=chromeHeadless -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver -Dspock.ignore.suiteB=true -Dspock.ignore.secured=true

test-app functional: -Dgeb.env=chromeHeadless -Dwebdriver.chrome.driver=/Users/sdelamo/Applications/chromedriver -Dspock.ignore.suiteB=true -Dspock.ignore.suiteA=true