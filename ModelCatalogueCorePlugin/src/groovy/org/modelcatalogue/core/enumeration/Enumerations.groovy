package org.modelcatalogue.core.enumeration

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.json.internal.Exceptions.JsonInternalException
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@CompileStatic class Enumerations implements Map<String, String>, Iterable<Enumeration> {

    static Enumerations from(Object o) {
        if (o == null) {
            return new Enumerations()
        }

        if (o instanceof String) {
            return from(o as String)
        }

        if (o instanceof Map) {
            return from(o as Map<String, String>)
        }

        throw new IllegalArgumentException("Cannot create enumeration from $o")
    }

    @CompileDynamic
    static Enumerations from(String text) {
        try {
            JsonSlurper slurper = new JsonSlurper()
            def payload =  slurper.parseText(text)

            Enumerations enumerations = new Enumerations()

            if (payload.type && payload.type == 'orderedMap' && payload.values && payload.values instanceof List) {
                for (value in payload.values) {
                    if (!value.id) {
                        enumerations.put(value.key?.toString(), value.value?.toString())
                    }

                    Long id
                    if (value.id instanceof Number) {
                        id = (value.id as Number).longValue()
                    } else {
                        id = Long.parseLong(value.id.toString(), 10)
                    }

                    enumerations.put(id, value.key?.toString(), value.value?.toString())
                }

                return enumerations
            }

            throw new IllegalArgumentException("Unparsable enumeration JSON string $text")
        } catch (JsonInternalException ignored) {}

        return from(LegacyEnumerations.stringToMap(text))

    }

    static Enumerations from(Map<String, String> enumerations) {
        Enumerations enums = new Enumerations()

        enumerations.each { String key, String value ->
            enums.put(key,value)
        }

        return enums
    }

    private Set<Enumeration> enumerations = new LinkedHashSet<Enumeration>()
    private Map<String, Enumeration> enumerationsByKeys = new LinkedHashMap<String, Enumeration>()
    private long genid = 1;

    private Enumerations() {}

    @Override
    Iterator<Enumeration> iterator() {
        return enumerations.iterator()
    }

    @Override
    int size() {
        return enumerationsByKeys.size()
    }

    @Override
    boolean isEmpty() {
        return enumerationsByKeys.isEmpty()
    }

    @Override
    boolean containsKey(Object key) {
        return enumerationsByKeys.containsKey(key)
    }

    @Override
    boolean containsValue(Object value) {
        return enumerations.any { it.value == value }
    }

    @Override
    String get(Object key) {
        return enumerationsByKeys.get(key)?.value
    }

    String put(Long id, String key, String value) {
        Enumeration existing = enumerationsByKeys.get(key)
        if (existing) {
            Enumeration newOne = Enumeration.create(id, key, value)
            enumerationsByKeys.put(key, newOne)
            enumerations.remove(existing)
            enumerations.add(newOne)
            return existing.value
        }

        Enumeration newOne = Enumeration.create(id, key, value)
        enumerations.add(newOne)
        enumerationsByKeys.put(key, newOne)
        return null
    }

    @Override
    String put(String key, String value) {
        Enumeration existing = enumerationsByKeys.get(key)
        if (existing) {
            Enumeration newOne = Enumeration.create(existing.id, key, value)
            enumerationsByKeys.put(key, newOne)
            enumerations.remove(existing)
            enumerations.add(newOne)
            return existing.value
        }

        Enumeration newOne = Enumeration.create(genid++, key, value)
        enumerations.add(newOne)
        enumerationsByKeys.put(key, newOne)
        return null
    }

    @Override
    String remove(Object key) {
        Enumeration existing = enumerationsByKeys.get(key)
        if (existing) {
            enumerations.remove(existing)
            enumerationsByKeys.remove(key)
            return existing.value
        }
        return null
    }

    @Override
    void putAll(Map<? extends String, ? extends String> m) {
        m.each { String key, String value ->
            put(key, value)
        }
    }

    @Override
    void clear() {
        enumerations.clear()
        enumerationsByKeys.clear()
    }

    @Override
    Set<String> keySet() {
        return enumerationsByKeys.keySet()
    }

    @Override
    Collection<String> values() {
        return enumerationsByKeys.values().collect { it.value }
    }

    @Override
    Set<Map.Entry<String, String>> entrySet() {
        return (enumerationsByKeys.collectEntries { String key, Enumeration value -> [key, value.value] } as Map<String, String>).entrySet()
    }

    @Override
    String toString() {
        return toJsonString()
    }

    String toJsonString() {
        JsonBuilder json = new JsonBuilder()
        json (
            type: 'orderedMap',
            values: enumerations.collect { [id: it.id, key: it.key, value: it.value ] }
        )
        json.toString()
    }
}
