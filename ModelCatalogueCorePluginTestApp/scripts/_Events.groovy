//eventCompileStart = { msg ->
//    try {
//        def revision = 'git rev-list --count HEAD'.execute().text.trim()
//        def hashShort = 'git rev-parse --short HEAD'.execute().text.trim()
//        def hash = 'git rev-parse HEAD'.execute().text.trim()
//        def branch = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
//        def version = "r${revision}.${hashShort}"
//        if (branch != 'master') {
//            version = "$version ($branch)"
//        }
//        new FileOutputStream("grails-app/views/_version.gsp", false) << """<a href="https://github.com/MetadataRegistry/ModelCataloguePlugin/tree/$hash" target="_blank">$version</a>"""
//    } catch (e) {
//        println "Cannot determine current version: $e"
//        new FileOutputStream("grails-app/views/_version.gsp", false) << """Unknown Version"""
//    }
//
//}

import grails.util.Metadata

try {
    def revision = 'git rev-list --count HEAD'.execute().text.trim()
    def hashShort = 'git rev-parse --short HEAD'.execute().text.trim()
    def hash = 'git rev-parse HEAD'.execute().text.trim()
    def branch = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
    def version = "r${revision}.${hashShort}"
    if (branch != 'master') {
        version = "$version ($branch)"
    }

    def travisTag = System.getenv('TRAVIS_TAG')

    if (travisTag) {
        version = travisTag
        hash = travisTag
    }
    new FileOutputStream("grails-app/views/_version.gsp", false) << """<a href="https://github.com/MetadataRegistry/ModelCataloguePlugin/tree/$hash" target="_blank">$version</a>"""
} catch (e) {
    println "Cannot determine current version: $e"
    new FileOutputStream("grails-app/views/_version.gsp", false) << """Unknown Version"""
}

eventCleanStart = { args ->
    File tmpFolder = new File("${System.getenv("MC_TMP_LOCATION")}/${Metadata.getCurrent().getApplicationName()}/${Metadata.getCurrent().getApplicationVersion()}")
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

eventTestPhaseStart = { args ->
    System.properties["grails.test.phase"] = args
}
