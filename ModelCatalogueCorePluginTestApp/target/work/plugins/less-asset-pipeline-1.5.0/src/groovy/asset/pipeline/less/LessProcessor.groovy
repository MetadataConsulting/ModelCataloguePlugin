package asset.pipeline.less
import asset.pipeline.AssetHelper
import org.mozilla.javascript.Context
import org.mozilla.javascript.JavaScriptException
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.NativeArray
import org.springframework.core.io.ClassPathResource
import groovy.util.logging.Log4j
import asset.pipeline.CacheManager

@Log4j
class LessProcessor {
  public static final java.lang.ThreadLocal threadLocal = new ThreadLocal();
  Scriptable globalScope
  ClassLoader classLoader
  def precompilerMode

  LessProcessor(precompiler=false){
    this.precompilerMode = precompiler
    try {
      classLoader = getClass().getClassLoader()

      def shellJsResource    = new ClassPathResource('asset/pipeline/less/shell.js', classLoader)
      def envRhinoJsResource = new ClassPathResource('asset/pipeline/less/env.rhino.js', classLoader)
      def hooksJsResource    = new ClassPathResource('asset/pipeline/less/hooks.js', classLoader)
      def lessJsResource     = new ClassPathResource('asset/pipeline/less/less-1.6.0.js', classLoader)
      def compileJsResource  = new ClassPathResource('asset/pipeline/less/compile.js', classLoader)

      Context cx = Context.enter()
      cx.setOptimizationLevel(-1)
      globalScope = cx.initStandardObjects()
      this.evaluateJavascript(cx, shellJsResource)
      this.evaluateJavascript(cx, envRhinoJsResource)
      this.evaluateJavascript(cx, hooksJsResource)
      this.evaluateJavascript(cx, lessJsResource)
      this.evaluateJavascript(cx, compileJsResource)

    } catch (Exception e) {
      throw new Exception("LESS Engine initialization failed.", e)
    } finally {
      try {
        Context.exit()
      } catch (IllegalStateException e) {}
    }
  }

  def evaluateJavascript(context, resource) {
    def inputStream = resource.inputStream
    context.evaluateReader(globalScope, new InputStreamReader(inputStream, 'UTF-8'), resource.filename, 0, null)

  }

  def process(input, assetFile) {
    try {
      if(!this.precompilerMode) {
        threadLocal.set(assetFile);
      }
      def assetRelativePath = relativePath(assetFile.file)
      // def paths = AssetHelper.scopedDirectoryPaths(new File("grails-app/assets").getAbsolutePath())

      // paths += [assetFile.file.getParent()]
      def paths = AssetHelper.getAssetPaths()
      def relativePaths = paths.collect { [it,assetRelativePath].join(AssetHelper.DIRECTIVE_FILE_SEPARATOR)}
      // println paths
      paths = relativePaths + paths


      def pathstext = paths.collect{
        def p = it.replaceAll("\\\\", "/")
        if (p.endsWith("/")) {
          "'${p}'"
        } else {
          "'${p}/'"
        }
      }.toString()

      def cx = Context.enter()
      def compileScope = cx.newObject(globalScope)
      compileScope.setParentScope(globalScope)
      compileScope.put("lessSrc", compileScope, input)

      def result = cx.evaluateString(compileScope, "compile(lessSrc, ${pathstext})", "LESS compile command", 0, null)
      return result
    } catch (JavaScriptException e) {
      throw new Exception("""
        LESS Engine compilation of LESS to CSS failed.
        $e: ${e.value}
        """,e)
    } catch (Exception e) {
      throw new Exception("""
        LESS Engine compilation of LESS to CSS failed.
        $e
        """)
    } finally {
      Context.exit()
    }
  }

  static void print(text) {
    log.debug text
  }

  static String resolveUri(String path, NativeArray paths) {
    def assetFile = threadLocal.get();
    log.debug "resolveUri: path=${path}"
    for (Object index : paths.getIds()) {
      def it = paths.get(index, null)
      def file = new File(it, path)
      log.trace "test exists: ${file}"
      if (file.exists()) {
        log.trace "found file: ${file}"
        if(assetFile) {
          CacheManager.addCacheDependency(assetFile.file.canonicalPath, file)
        }
        return file.toURI().toString()
      }
    }

    return null
  }

  def relativePath(file, includeFileName=false) {
    def path
    if(includeFileName) {
      path = file.class.name == 'java.io.File' ? file.getCanonicalPath().split(AssetHelper.QUOTED_FILE_SEPARATOR) : file.file.getCanonicalPath().split(AssetHelper.QUOTED_FILE_SEPARATOR)
    } else {
      path = file.getParent().split(AssetHelper.QUOTED_FILE_SEPARATOR)
    }

    def startPosition = path.findLastIndexOf{ it == "grails-app" }
    if(startPosition == -1) {
      startPosition = path.findLastIndexOf{ it == 'web-app' }
      if(startPosition+2 >= path.length) {
        return ""
      }
      path = path[(startPosition+2)..-1]
    }
    else {
      if(startPosition+3 >= path.length) {
        return ""
      }
      path = path[(startPosition+3)..-1]
    }

    return path.join(AssetHelper.DIRECTIVE_FILE_SEPARATOR)
  }
}
