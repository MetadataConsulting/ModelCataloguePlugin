package org.modelcatalogue.core.util

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.modelcatalogue.core.Extendible
import org.modelcatalogue.core.Extension

/**
 * Created by ladin on 12.02.14.
 * Provides a Map interface to an element making use of the Extendible interface.
 */
class ExtensionsWrapper implements Map<String, String> {

    final Extendible element

    ExtensionsWrapper(Extendible element) {
        this.element = element
    }

    @Override
    int size() {
        element.countExtensions()
    }

    @Override
    boolean isEmpty() {
        size() == 0
    }

    @Override
    boolean containsKey(Object key) {
        keySet().contains(key)
    }

    @Override
    boolean containsValue(Object value) {
        values().contains(value)
    }

    @Override
    String get(Object key) {
        if (!key) return null
        findExtensionValueByName(key?.toString())?.extensionValue
    }

    @Override
    String put(String key, String value) {
        if (!key || key.length() < 1) {
            if (value) {
                throw new IllegalArgumentException("Invalid key: $key. The key must be contain at least one character! (value = $value)")
            }
            return value
        }
        createOrUpdate(key, value)
    }

    @Override
    String remove(Object key) {
        deleteIfPresent(key)
    }

    @Override
    void putAll(Map<? extends String, ? extends String> m) {
        m?.each { key, val ->
            put(key, val?.toString())
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

    private Extension findExtensionValueByName(String key) {
        if (!key) return null
        element.findExtensionByName(key)
    }

    private Map<String, String> asReadOnlyMap() {
        def extensions = element.listExtensions()
        if (!extensions) return Collections.emptyMap()
        Collections.unmodifiableMap(new LinkedHashSet(extensions).collectEntries {
            [it.name, it.extensionValue]
        })
    }

    private String createOrUpdate(String name, String value) {
        Extension existing = findExtensionValueByName(name)
        if (existing) {
            String oldVal = existing.extensionValue
            element.updateExtension(existing, value)
            return oldVal
        }
        element.addExtension(name?.toString(), value?.toString())
        return null

    }

    private String deleteIfPresent(Object key) {
        Extension existing = findExtensionValueByName(key?.toString())
        if (!existing) return null
        element.removeExtension(existing)
        existing.extensionValue
    }
}
