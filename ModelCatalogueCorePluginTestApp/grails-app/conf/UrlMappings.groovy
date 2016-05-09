class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                controller(inList: ['login', 'logout', 'userAdmin', 'register', 'console', 'dbconsole'])
            }
        }

        "/"(view:"/index")
	}
}
