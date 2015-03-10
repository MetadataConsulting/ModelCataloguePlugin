package asset.pipeline

import grails.util.Environment

class AssetMethodTagLib {

	static namespace = "g"
	static returnObjectForTags = ['assetPath']

	def grailsApplication
	def assetProcessorService


	def assetPath = { attrs ->
		def src
		def ignorePrefix = false
    if (attrs instanceof Map) {
    	src = attrs.src
    	ignorePrefix = attrs.containsKey('ignorePrefix')? attrs.ignorePrefix : false
    } else {
    	src = attrs
    }

		def conf = grailsApplication.config.grails.assets

		def assetRootPath = assetUriRootPath(grailsApplication, request)
		def assetUrl = (!ignorePrefix && conf.url) ? conf.url : "$assetRootPath"

		if(conf.precompiled) {
			def realPath = conf.manifest.getProperty(src)
			if(realPath) {
				return "${assetUrl}${realPath}"
			}
		}
		return "${assetUrl}${src}"
	}


	private assetUriRootPath(grailsApplication, request) {
		def context = grailsApplication.mainContext
		def conf    = grailsApplication.config.grails.assets
		def mapping = context.assetProcessorService.assetMapping

		return conf.url ?: (request.contextPath + "${request.contextPath?.endsWith('/') ? '' : '/'}$mapping/" )
	}
}
