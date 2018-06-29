package org.modelcatalogue.core

import java.util.regex.Pattern

class TestUtil {

    public static void createJenkinsFiles(String testPath) {
        Set<String> testCases = getTestCases(testPath)
        Integer testSize = testCases.size()
        Integer remainder = testSize % 5
        Integer quotient = testSize / 5

        if (!(remainder < 2.5)) {
            quotient += 1
        }

        List<List<String>> subsetOfTestCase = testCases.collate(quotient)
        if (subsetOfTestCase.size() > 5) {
            subsetOfTestCase[4].addAll(subsetOfTestCase[5])
        }

        String parentFolder = new File(testPath).parent
        FileTreeBuilder treeBuilder = new FileTreeBuilder(new File(parentFolder))
        File jenkinsFile = null
        subsetOfTestCase.eachWithIndex { List<String> tests, Integer index ->
            if (index < 5) {
                String newJenkinsFileName = "Jenkinsfile${index + 1}"
                jenkinsFile = new File("$parentFolder/$newJenkinsFileName")
                if (jenkinsFile.exists()) {
                    jenkinsFile.delete()
                }
                treeBuilder.file(newJenkinsFileName) {
                    write getJenkinsFileContent("sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome -DdownloadFilepath=/home/ubuntu/download -Dwebdriver.chrome.driver=/opt/chromedriver functional: ${tests.join(" ")}'")
                }
            }
        }
    }

    static String getJenkinsFileContent(String scriptText) {
        return """pipeline {
  agent any
  options {
    disableConcurrentBuilds()
  }
  stages {
    stage('Test Execute') {
      steps {
        dir(path: 'ModelCatalogueCorePluginTestApp') {
            sh 'npm install'
            sh 'bower install'
            wrap([\$class: 'Xvfb']) {
              ${scriptText}
            }

        }
      }
    }
  }
}"""
    }


    private static Set<String> getTestCases(String testPath) {
        String pathTillFunctioal = testPath + "/test/functional/"
        Set<String> files = []
        new File(pathTillFunctioal).eachFileRecurse {
            String fileName = it.name
            Set<String> dataFiles = fileName.split(Pattern.quote("."))
            if (dataFiles[0].contains("Spec") && dataFiles[1] == "groovy")
                files.add(dataFiles[0])
        }

        return files
    }
}