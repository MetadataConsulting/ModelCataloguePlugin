package uk.co.mc.core.util

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import uk.co.mc.core.ExtendibleElement
import uk.co.mc.core.ExtensionValue

/**
 * Created by ladin on 12.02.14.
 */
class ExtendibleElementExtensionsWrapper implements Map<String, String> {

    final ExtendibleElement element

    ExtendibleElementExtensionsWrapper(ExtendibleElement element) {
        this.element = element
    }

    @Override
    int size() {
        ExtensionValue.countByElement(element)
    }

    @Override
    boolean isEmpty() {
        size() == 0
    }

    @Override
    boolean containsKey(Object key) {
        findExtensionValueByName(key)
    }

    @Override
    boolean containsValue(Object value) {
        if (isEmpty()) return false
        findExtensionValueByValue(value)
    }

    @Override
    String get(Object key) {
        if (isEmpty()) return null
        findExtensionValueByName(key)?.value
    }

    @Override
    String put(String key, String value) {
        if (!key || key.length() < 2) throw new IllegalArgumentException("Invalid key: $key. The key must be contain at least two characters")
        createOrUpdate(key, value)
    }

    @Override
    String remove(Object key) {
        deleteIfPresent(key)
    }

    @Override
    void putAll(Map<? extends String, ? extends String> m) {
        m?.each { key, val ->
            put(key, val)
        }
    }

    @Override
    void clear() {
        for (String key in keySet()) {
            remove(key)
        }
    }

    @Override
    Set<String> keySet() {
        asReadOnlyMap().keySet()
    }

    @Override
    Collection<String> values() {
        asReadOnlyMap().values()
    }

    @Override
    Set<Map.Entry<String, String>> entrySet() {
        asReadOnlyMap().entrySet()
    }

    @Override
    String toString() {
        DefaultGroovyMethods.toMapString(this)
    }

    private ExtensionValue findExtensionValueByName(key) {
        if (!key) return null
        ExtensionValue.findByElementAndName(element, key.toString())
    }

    private ExtensionValue findExtensionValueByValue(key) {
        if (!key) return null
        ExtensionValue.findByElementAndValue(element, key.toString())
    }

    private Map<String, String> asReadOnlyMap() {
        Collections.unmodifiableMap(element.extensions.collectEntries {
            [it.name, it.value]
        })
    }

    private String createOrUpdate(String name, String value) {
        ExtensionValue existing = findExtensionValueByName(name)
        if (existing) {
            String old = existing.value
            existing.value = value?.toString()
            existing.save()
            element.addToExtensions(old)
            assert existing.errors.errorCount == 0
            return old
        }
        ExtensionValue newOne = new ExtensionValue(name: name?.toString(), value: value?.toString(), element: element)
        element.addToExtensions(newOne)
        newOne.save()
        assert newOne.errors.errorCount == 0
        return null

    }

    private String deleteIfPresent(Object key) {
        ExtensionValue existing = findExtensionValueByName(key?.toString())
        if (!existing) return null
        element.removeFromExtensions(existing)
        existing.delete()
        existing.value
    }
}
