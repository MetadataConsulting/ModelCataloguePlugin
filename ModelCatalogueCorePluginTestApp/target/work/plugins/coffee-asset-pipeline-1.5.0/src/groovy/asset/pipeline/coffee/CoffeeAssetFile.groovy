package asset.pipeline.coffee
import asset.pipeline.CacheManager
import asset.pipeline.AbstractAssetFile
import asset.pipeline.AssetHelper

class CoffeeAssetFile extends AbstractAssetFile {
	static final contentType = ['application/javascript','application/x-javascript','text/javascript']
	static extensions = ['coffee', 'js.coffee']
	static final String compiledExtension = 'js'
	static processors = [CoffeeScriptProcessor]

	String directiveForLine(String line) {
		line.find(/#=(.*)/) { fullMatch, directive -> return directive }
	}
}
