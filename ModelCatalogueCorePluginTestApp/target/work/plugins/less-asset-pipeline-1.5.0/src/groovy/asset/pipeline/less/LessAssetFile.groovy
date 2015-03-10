package asset.pipeline.less
import asset.pipeline.CacheManager
import asset.pipeline.AssetHelper
import asset.pipeline.AbstractAssetFile
import asset.pipeline.processors.CssProcessor

class LessAssetFile extends AbstractAssetFile {
	static final String contentType = 'text/css'
	static extensions = ['less', 'css.less']
	static final String compiledExtension = 'css'
	static processors = [LessProcessor,CssProcessor]

	String directiveForLine(String line) {
		line.find(/\*=(.*)/) { fullMatch, directive -> return directive }
	}
}
