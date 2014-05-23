package org.modelcatalogue.core.util

import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.modelcatalogue.core.Extendible
import org.modelcatalogue.core.Extension

/**
 * Created by ladin on 12.02.14.
 */
class ExtensionsWrapper implements Map<String, String> {

    final Extendible element

    ExtensionsWrapper(Extendible element) {
        this.element = element
    }

    @Override
    int size() {
        element.listExtensions()?.size() ?: 0
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
        if (isEmpty()) return null
        findExtensionValueByName(key)?.extensionValue
    }

    @Override
    String put(String key, String value) {
        if (!key || key.length() < 1) throw new IllegalArgumentException("Invalid key: $key. The key must be contain at least one character")
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

    private Extension findExtensionValueByName(key) {
        if (!key) return null
        element.listExtensions()?.find { it.name == key }
    }

    private Map<String, String> asReadOnlyMap() {
        if (!element.listExtensions()) return Collections.emptyMap()
        Collections.unmodifiableMap(element.listExtensions().collectEntries {
            [it.name, it.extensionValue]
        })
    }

    private String createOrUpdate(String name, String value) {
        Extension existing = findExtensionValueByName(name)
        if (existing) {
            String old = existing.extensionValue
            existing.extensionValue = value?.toString()
            if (existing.save()) {
                return old
            }
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
