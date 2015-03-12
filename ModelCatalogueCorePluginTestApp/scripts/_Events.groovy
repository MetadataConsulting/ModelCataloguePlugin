eventCompileStart = { msg ->
    def revision = 'git rev-list --count HEAD'.execute().text.trim()
    def hash = 'git rev-parse --short HEAD'.execute().text.trim()
    def branch = 'git rev-parse --abbrev-ref HEAD'.execute().text.trim()
    def version = "r${revision}.${hash}"
    if (branch != 'master') {
        version = "$version ($branch)"
    }
    new FileOutputStream("grails-app/views/_version.gsp", false) << version
}