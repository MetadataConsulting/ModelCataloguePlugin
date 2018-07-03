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
                    write getJenkinsFileContent("sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome -DdownloadFilepath=/home/ubuntu -Dwebdriver.chrome.driver=/opt/chromedriver functional: ${tests.join(" ")}'", "continuous-integration/jenkins${index + 1}")
//                    write getJenkinsFileContent("sh '/opt/grails/bin/grails test-app -Dserver.port=8081 -Dgeb.env=chrome -DdownloadFilepath=/home/ubuntu -Dwebdriver.chrome.driver=/opt/chromedriver functional: ${tests[0]}'", "continuous-integration/jenkins${index + 1}") // Just do one test for the purpose of testing Jenkins Pipelines
                }
            }
        }
//        subsetOfTestCase.eachWithIndex { List<String> tests, Integer index ->
//            if (index < 5) {
//                String newJenkinsFileName = "Jenkinsfile${index + 1}"
//                jenkinsFile = new File("$parentFolder/$newJenkinsFileName")
//                if (jenkinsFile.exists()) {
//                    jenkinsFile.delete()
//                }
//                treeBuilder.file(newJenkinsFileName) {
//                    write getJenkinsFileContent(runFunctionalTestsCommand(tests))
//                }
//            }
//        }
    }

    static String getJenkinsFileContent(String scriptText, String context) {
        return """
def getRepoURL() {
  sh "mkdir -p .git"
  sh "git config --get remote.origin.url > .git/remote-url"
  return readFile(".git/remote-url").trim()
}
def getCommitSha() {
  sh "mkdir -p .git"
  sh "git rev-parse HEAD > .git/current-commit"
  return readFile(".git/current-commit").trim()
}
 
def updateGithubCommitStatus(build, String context, String buildUrl, String message, String state) {
  // workaround https://issues.jenkins-ci.org/browse/JENKINS-38674
  repoUrl = getRepoURL()
  commitSha = getCommitSha()
  println "Updating Github Commit Status" 
  println "repoUrl \$repoUrl"
  println "commitSha \$commitSha"
  println "build result: \${build.result}, currentResult: \${build.currentResult}"
 
  step([
    \$class: 'GitHubCommitStatusSetter',
    reposSource: [\$class: "ManuallyEnteredRepositorySource", url: repoUrl],
    commitShaSource: [\$class: "ManuallyEnteredShaSource", sha: commitSha],
    errorHandlers: [[\$class: 'ShallowAnyErrorHandler']],
    contextSource: [\$class: "ManuallyEnteredCommitContextSource", context: context],
    statusBackrefSource: [\$class: "ManuallyEnteredBackrefSource", backref: buildUrl],
        
    statusResultSource: [
      \$class: 'ConditionalStatusResultSource',
      results: [
        [\$class: 'AnyBuildResult', state: state, message: message]
      ]
    ]
  ])
}
pipeline {
  agent any
  options {
    disableConcurrentBuilds()
  }
  stages {
    stage('Test Execute') {
      steps {

        updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "In Progress", "PENDING")
        dir(path: 'ModelCatalogueCorePluginTestApp') {
            updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Installing Node Modules", "PENDING")
            sh 'npm install'
            updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Installing Bower Components", "PENDING")
            sh 'bower install'
            updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Running Functional Tests", "PENDING")
            wrap([\$class: 'Xvfb']) {
              ${scriptText}
            }
        }        
      }
    }
    stage("Post") {
        post {
                always {
                    publishHTML(target: [allowMissing: true,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'ModelCatalogueCorePluginTestApp/target/test-reports',
                            reportFiles: 'html/index.html',
                            reportName: 'HTML Report',
                            reportTitles: ''])
                }      
                failure {
                    updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Build Failed.", 'FAILURE')     
                }
                success {
                    updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Build Success!", 'SUCCESS')
                }
                unstable {
                    updateGithubCommitStatus(currentBuild, "$context", BUILD_URL, "Build Unstable.", 'UNSTABLE')     
                }
            }  
    }
         

  }
}"""
    }
    // comment

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
