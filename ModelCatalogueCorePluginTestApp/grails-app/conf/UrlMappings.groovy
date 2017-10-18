class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                controller(inList: ['login', 'logout', 'userAdmin', 'aclClass', 'aclSid', 'aclEntry', 'aclObjectIdentity', 'register','requestmap','role', 'console', 'dbconsole'])
            }
        }
        "/"(view:"/index")

        "403"(controller: "errors", action: "error403")
        "500"(controller: "errors", action: "error500")
        "500"(controller: "errors", action: "error403", exception: AccessDeniedException)
        "500"(controller: "errors", action: "error403", exception: NotFoundException)
    }
}
