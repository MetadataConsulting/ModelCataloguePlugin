package org.modelcatalogue.core.enumeration

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
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
            return from(o as Map<String, Object>)
        }

        throw new IllegalArgumentException("Cannot create enumeration from $o")
    }

    @CompileDynamic
    static Enumerations from(String text) {
        if (!text) {
            return new Enumerations()
        }
        try {
            JsonSlurper slurper = new JsonSlurper()
            def payload =  slurper.parseText(text)

            Enumerations enumerations = new Enumerations()

            if (payload.type && payload.type == 'orderedMap' && payload.values != null && payload.values instanceof List) {
                for (value in payload.values) {
                    if (!value.id && !value.key && !value.value) {
                        continue
                    }

                    boolean deprecated = value.deprecated?.toBoolean() ? true : false
                    if (!value.id) {
                        enumerations.put(value.key?.toString(), value.value?.toString(), deprecated)
                    }

                    Long id
                    if (value.id instanceof Number) {
                        id = (value.id as Number).longValue()
                    } else {
                        id = Long.parseLong(value.id.toString(), 10)
                    }

                    enumerations.put(id, value.key?.toString(), value.value?.toString(), deprecated)
                }

                return enumerations
            }

            throw new IllegalArgumentException("Unparsable enumeration JSON string $text")
        } catch (Exception ignored) {}

        return from(LegacyEnumerations.stringToMap(text))

    }
    @CompileDynamic
    static Enumerations from(List<Map<String, String>> enumerations) {
        if (!enumerations) {
            return new Enumerations()
        }
        Enumerations enums = new Enumerations()

        for (enumeration in (enumerations ?: [])) {
            if (!enumeration.id && !enumeration.key && !enumeration.value) {
                continue
            }
            Long id = (enumeration.id as Number)?.longValue()
            boolean deprecated = enumeration.deprecated ? true : false
            if (id != null) {
                enums.put(id, enumeration.key?.toString(), enumeration.value?.toString(), deprecated)
            } else {
                enums.put(enumeration.key?.toString(), enumeration.value?.toString(), deprecated)
            }
        }
        return enums
    }
    @CompileDynamic
    static Enumerations from(Map<String, Object> enumerations) {
        if (!enumerations) {
            return new Enumerations()
        }
        Enumerations enums = new Enumerations()

        if (enumerations.type && enumerations.type == 'orderedMap') {
            for (value in (enumerations.values ?: [])) {
                if (!value.id && !value.key && !value.value) {
                    continue
                }
                Long id = (value.id as Number)?.longValue()
                boolean deprecated = value.deprecated ? true : false
                if (id != null) {
                    enums.put(id, value.key?.toString(), value.value?.toString(), deprecated)
                } else {
                    enums.put(value.key?.toString(), value.value?.toString(), deprecated)
                }
            }
            return enums
        }

        enumerations.each { String key, String value ->
            enums.put(key,value)
        }

        return enums
    }

    static Enumerations create() {
        return new Enumerations()
    }

    private final Set<Enumeration> enumerations = new LinkedHashSet<Enumeration>()
    private final Map<String, Enumeration> enumerationsByKeys = new LinkedHashMap<String, Enumeration>()
    private final Map<Long, Enumeration> enumerationsById = new LinkedHashMap<Long, Enumeration>()
    private long genid = 1;

    private Enumerations() {}

    Enumerations copy() {
        Enumerations enumerations = new Enumerations()
        enumerations.@genid = this.@genid
        for (Enumeration e in this) {
            enumerations.put(e.id, e.key, e.value)
        }
        return enumerations
    }

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

    Enumeration getEnumerationById(Long id){
        return enumerationsById.get(id)
    }

    Enumeration getEnumerationByKey(String key){
        return enumerationsByKeys.get(key)
    }

    String put(Long id, String key, String value, boolean deprecated = false) {
        genid = Math.max(genid, id) + 1

        Enumeration existing = enumerationsById.get(id)

        if (existing) {
            return replaceExistingEnumeration(id, key, value, deprecated, existing)
        }

        existing = enumerationsByKeys.get(key)
        if (existing) {
            return replaceExistingEnumeration(id, key, value, deprecated, existing)
        }

        Enumeration newOne = Enumeration.create(id, key, value, deprecated)
        enumerations.add(newOne)
        enumerationsByKeys.put(key, newOne)
        enumerationsById.put(id, newOne)
        return null
    }

    private String replaceExistingEnumeration(long id, String key, String value, boolean deprecated, Enumeration existing) {
        Enumeration newOne = Enumeration.create(id, key, value, deprecated)
        enumerationsByKeys.remove(existing.key)
        enumerationsByKeys.put(key, newOne)
        enumerationsById.remove(existing.id)
        enumerationsById.put(id, newOne)
        enumerations.remove(existing)
        enumerations.add(newOne)
        return existing.value
    }

    String put(String key, String value, boolean deprecated) {
        put(genid, key, value ?: '', deprecated)
    }

    @Override
    String put(String key, String value) {
        put(genid, key, value ?: '')
    }

    @Override
    String remove(Object key) {
        Enumeration existing = enumerationsByKeys.get(key)
        if (existing) {
            enumerations.remove(existing)
            enumerationsByKeys.remove(key)
            enumerationsById.remove(existing.id)
            return existing.value
        }
        return null
    }

    Enumeration removeEnumerationById(Long id) {
        Enumeration existing = getEnumerationById(id)

        if (!existing) {
            return null
        }

        remove(existing.key)

        return existing
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
        enumerationsById.clear()
        genid = 1
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
        json (toJsonMap())
        json.toString()
    }

    Map<String, Object> toJsonMap() {
        [type: 'orderedMap', values: enumerations.collect { [id: it.id, key: it.key, value: it.value, deprecated: it.deprecated] }]
    }

    @CompileDynamic
    Enumerations withDeprecatedEnumeration(Long id, boolean deprecated) {
        Map<String, Object> asJson = toJsonMap()
        List<Map<String, Object>> values = asJson.values
        values.find({ it.id == id }).deprecated = deprecated
        return from(asJson)
    }
}
