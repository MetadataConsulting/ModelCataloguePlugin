class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                controller(inList: ['login', 'logout', 'userAdmin', 'register','requestmap','role', 'console', 'dbconsole'])
            }
        }

        "/"(view:"/index")
	}
}
