eventCleanStart = { args ->
    File tmpFolder = new File("${System.getProperty('java.io.tmpdir')}/mc")
    if (tmpFolder.exists() && tmpFolder.directory) {
        println "\nRemoving old test databases from previous runs\n"
        tmpFolder.deleteDir()
    }
}
