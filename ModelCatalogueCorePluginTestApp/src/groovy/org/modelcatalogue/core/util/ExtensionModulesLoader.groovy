package org.modelcatalogue.core.util

import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl

/**
 * @see http://stackoverflow.com/questions/19564902/applying-groovy-extensions-in-grails-produces-missingmethodexception-for-string
 */
class ExtensionModulesLoader {

    static addExtensionModules() {

        Map<CachedClass, List<MetaMethod>> map = [:]
        ClassLoader classLoader = Thread.currentThread().contextClassLoader
        try {
            Enumeration<URL> resources = classLoader.getResources(MetaClassRegistryImpl.MODULE_META_INF_FILE)
            for (URL url in resources) {
                if (url.path.contains('groovy-all')) {
                    // already registered
                    continue
                }
                Properties properties = new Properties()
                InputStream inStream = null
                try {
                    inStream = url.openStream()
                    properties.load(inStream)
                    GroovySystem.metaClassRegistry.registerExtensionModuleFromProperties(properties,
                            classLoader, map)
                }
                catch (IOException e) {
                    throw new GroovyRuntimeException("Unable to load module META-INF descriptor", e)
                } finally {
                    inStream?.close()
                }
            }
        } catch (IOException ignored) {
        }
        map.each { CachedClass cls, List<MetaMethod> methods ->
            cls.addNewMopMethods(methods)
        }
    }

}
