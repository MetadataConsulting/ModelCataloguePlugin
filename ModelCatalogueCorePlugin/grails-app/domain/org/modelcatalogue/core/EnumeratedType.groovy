package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.util.Inheritance
import org.modelcatalogue.core.util.OrderedMap
import org.springframework.validation.Errors

/*
* Enumerated Types are data types that contain a list of enumerated values
* i.e. ['politics', 'history', 'science']
* */

//TODO marshalling and unmarshalling for enumerated type as string at the moment it returns the enum as string
//but we need ext. please see enumAsStringConverter.groovy for marshalling to index

class EnumeratedType extends DataType {

    static final SUBSET_METADATA_KEY = 'http://www.modelcatalogue.org/metadata/enumerateType#subset'

    static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    String enumAsString = ""

    static transients = ['enumerations']

    static constraints = {
        name unique: false
        enumAsString nullable: true, maxSize: 10000
    }

    /**
     * Sets the map containing the enum values.
     *
     * The map is encoded and stored in {@link #enumAsString} field.
     *
     * @param map the map containing the enum values
     */
    void setEnumerations(Map<String, String> map) {
        enumAsString = mapToString(OrderedMap.fromJsonMap(map))
    }

    /**
     * Returns the map containing the enum values.
     *
     * The map is decoded from {@link #enumAsString} field.
     *
     * @return the map containing the enum values
     */
    Map<String, String> getEnumerations() {
        stringToMap(enumAsString)
    }
	
	 class EnumBean{
		String name
		String code 
	}
	/**
	 * This method could be optimized 
	 * 
	 * @return  a list with beans (name,code)
	 */
	List getEnumerationsAsBeans(){
		def results=new ArrayList()
		def map =stringToMap(enumAsString)
		for (String key in map.keySet()) {
			results << new EnumBean(name:map.get(key),code:key)
		}
		return results
		
	}

    boolean isEnumKey(Object x) {
        if (!x) {
            return true
        }
        if (!enumerations.keySet().contains(x.toString())) {
            return false
        }
        return true
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, status: ${status}, modelCatalogueId: ${modelCatalogueId},  dataModel: ${dataModel?.name} (${dataModel?.combinedVersion}), enumerations: ${enumerations}]"
    }

    /**
     * Finds all items which contains given key as key in the enumeration.
     * @param key key to be looked for
     * @return list of items which contains given key as key in the enumeration
     */
    static List<EnumeratedType> findAllByEnumeratedKey(String key) {
        findAllByEnumAsStringIlike("%${quote(key)}%").findAll {
            it.enumerations.containsKey(key)
        }
    }

    /**
     * Finds all items which contains given value as value in the enumeration.
     * @param value value to be looked for
     * @return list of items which contains given value as value in the enumeration
     */
    static List<EnumeratedType> findAllByEnumeratedValue(String value) {
        findAllByEnumAsStringIlike("%${quote(value)}%").findAll {
            it.enumerations.containsValue(value)
        }
    }

    static String mapToString(Map<String, String> map) {
        if (!map) return ""
        map.collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')
    }

    static Map<String, String> stringToMap(String s) {
        if (!s) return ImmutableMap.of()
        Map<String, String> ret = [:]
        s.split(/\|/).each { String part ->
            if (!part) return
            String[] pair = part.split("(?<!\\\\):")
            if (pair.length > 2) throw new IllegalArgumentException("Wrong enumerated value '$part' in encoded enumeration '$s'")
            if (pair.length == 1) {
                ret[unquote(pair[0])] = ''
            } else {
                ret[unquote(pair[0])] = unquote(pair[1])
            }
        }
        return ImmutableMap.copyOf(ret)
    }

    static String quote(String s) {
        if (s == null) return ""
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

    static String unquote(String s) {
        if (s == null) return ""
        String ret = s
        QUOTED_CHARS.reverseEach { original, pattern ->
            ret = ret.replace(pattern, original)
        }
        ret
    }

    String prettyPrint() {
        enumerations.collect { key, value -> "$key: $value" }.join('\n')
    }

    @Override
    void beforeDraftPersisted() {
        if (!enumerations) {
            enumerations = ['default' : '']
        }
    }

    @Override
    protected boolean canInherit(CatalogueElement child, String propertyName, Map<String, String> metadata) {
        if (propertyName == 'enumAsString') {
            return true // handled per enum value
        }
        return super.canInherit(child, propertyName, metadata)
    }

    @Override
    List<String> getInheritedAssociationsNames() {
        return ['enumAsString']
    }

    @Override
    protected boolean isInherited(CatalogueElement child, String propertyName, Map<String, String> metadata, boolean persistent) {
        return super.isInherited(child, propertyName, metadata, persistent)
    }

    @Override
    protected void afterPropertyInherited(String s, Map<String, String> metadata) {
        super.afterPropertyInherited(s, metadata)
        ext[SUBSET_METADATA_KEY] = metadata[SUBSET_METADATA_KEY]
    }

    @Override
    protected void afterInheritedPropertyRemoved(String s, Map<String, String> metadata) {
        super.afterInheritedPropertyRemoved(s, metadata)
        ext.remove(SUBSET_METADATA_KEY)
    }

    @Override
    protected Object getValueToBeInherited(CatalogueElement child, String propertyName, Map<String, String> metadata, boolean persistent) {
        if (propertyName != 'enumAsString') {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        List<String> subsetKeys = parseSubsetKeys(metadata)

        if (!subsetKeys) {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        Map<String, String> subset = new LinkedHashMap<String, String>((child as EnumeratedType).enumerations)
        Map<String, String> enumerations = new LinkedHashMap<String, String>(persistent ? stringToMap(getPersistentValue('enumAsString')) : this.enumerations)

        for (String key in subsetKeys) {
            subset[key] = enumerations[key]
        }

        return mapToString(subset)
    }

    static List<String> parseSubsetKeys(Map<String, String> metadata) {
        String value = metadata[SUBSET_METADATA_KEY]
        if (!value) {
            return []
        }
        return value.split(/\s*,\s*/)*.replaceAll(/\\,/,',')
    }
}
