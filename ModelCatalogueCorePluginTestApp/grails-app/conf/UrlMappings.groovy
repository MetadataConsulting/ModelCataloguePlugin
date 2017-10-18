class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                controller(inList: ['login', 'logout', 'userAdmin', 'aclClass', 'aclSid', 'aclEntry', 'aclObjectIdentity', 'register','requestmap','role', 'console', 'dbconsole'])
            }
        }

        "/"(view:"/index")
	}
}
