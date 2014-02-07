class UrlMappings {




	static mappings = {
        "/dataElement"(resources:"DataElement")
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
