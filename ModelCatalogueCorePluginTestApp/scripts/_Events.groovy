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

try {
    def revision = 'git rev-list --count HEAD'.execute().text.trim()
    def hashShort = 'git rev-parse --short HEAD'.execute().text.trim()
    def hash = 'git rev-parse HEAD'.execute().text.trim()
    def branch = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
    def version = "r${revision}.${hashShort}"
    if (branch != 'master') {
        version = "$version ($branch)"
    }
    new FileOutputStream("grails-app/views/_version.gsp", false) << """<a href="https://github.com/MetadataRegistry/ModelCataloguePlugin/tree/$hash" target="_blank">$version</a>"""
} catch (e) {
    println "Cannot determine current version: $e"
    new FileOutputStream("grails-app/views/_version.gsp", false) << """Unknown Version"""
}