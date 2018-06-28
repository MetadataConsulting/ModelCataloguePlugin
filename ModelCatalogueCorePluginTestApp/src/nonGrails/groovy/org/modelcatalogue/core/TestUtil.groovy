package org.modelcatalogue.core

import java.util.regex.Pattern

class TestUtil {

    /**
     * Create Jenkins Files relative to project root, which should end in ModelCatalogueCorePluginTestApp
     * Create 5 files, splitting functional test cases into 5 groups.
     * @param projectRootPath
     */
    public static void createJenkinsFiles(String projectRootPath) {
        Set<String> testCases = getFunctionalTestCaseNames(projectRootPath)
        Integer testSize = testCases.size()
        Integer remainder = testSize % 5
        Integer quotient = testSize / 5

        if (!(remainder < 2.5)) {
            quotient += 1
        }

        // split tests into groups of size 1/5 of the total number of tests:
        List<List<String>> subsetOfTestCase = testCases.collate(quotient)
        if (subsetOfTestCase.size() > 5) {
            subsetOfTestCase[4].addAll(subsetOfTestCase[5])
        }

        // jenkinsfiles will be created in parent of project root:
        String parentFolder = new File(projectRootPath).parent
        FileTreeBuilder treeBuilder = new FileTreeBuilder(new File(parentFolder))
        File jenkinsFile = null

        // write jenkins files:
        subsetOfTestCase.eachWithIndex { List<String> tests, Integer index ->
            if (index < 5) {
                String newJenkinsFileName = "Jenkinsfile${index + 1}"
                jenkinsFile = new File("$parentFolder/$newJenkinsFileName")
                if (jenkinsFile.exists()) {
                    jenkinsFile.delete()
                }
                treeBuilder.file(newJenkinsFileName) {
                    write getJenkinsFileContent("sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome -DdownloadFilepath=/home/ubuntu -Dwebdriver.chrome.driver=/opt/chromedriver functional: ${tests.join(" ")}'")
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
        publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'ModelCatalogueCorePluginTestApp/target/test-reports/html', reportFiles: 'index.html', reportName: 'HTML Report', reportTitles: ''])

      }
    }
  }
}"""
    }

    /**
     * Get names of functional test cases. These are names of files *Spec.groovy.
     * @param projectRootPath
     * @return
     */
    private static Set<String> getFunctionalTestCaseNames(String projectRootPath) {
        String functionalTestRootPath = projectRootPath + "/test/functional/"
        Set<String> files = []
        new File(functionalTestRootPath).eachFileRecurse {
            String fileName = it.name
            Set<String> dataFiles = fileName.split(Pattern.quote("."))
            if (dataFiles[0].contains("Spec") && dataFiles[1] == "groovy")
                files.add(dataFiles[0])
        }

        return files
    }

    static void main(String... args) {
        println "in TestUtil main"
        String projectRootAbsolutePath = args[0]
        TestUtil.createJenkinsFiles(projectRootAbsolutePath)
    }
}
