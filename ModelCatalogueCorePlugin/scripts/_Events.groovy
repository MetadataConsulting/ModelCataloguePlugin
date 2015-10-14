eventCleanStart = { args ->
    File tmpFolder = new File("${System.getProperty('java.io.tmpdir')}/mc")
    if (tmpFolder.exists() && tmpFolder.directory) {
        println "\nRemoving old test databases from previous runs\n"
        tmpFolder.deleteDir()
    }
}

eventTestCaseStart = { name ->
    println '-' * 60
    println "|$name : started"
}

eventTestCaseEnd = { name, err, out ->
    println "\n|$name : finished"
}
