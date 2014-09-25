package org.modelcatalogue.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* Enumerated Types are data types that contain a list of enumerated values
* i.e. ['politics', 'history', 'science']
* Enumerated Types are used by Value Domains (please see ValueDomain)
* i.e. ValueDomain subjects uses EnumeratedType enumerations ['politics', 'history', 'science']
* */

//TODO marshalling and unmarshalling for enumerated type as string at the moment it returns the enum as string
//but we need ext. please see enumAsStringConverter.groovy for marshalling to index

class EnumeratedType extends DataType {

    private static final QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    String enumAsString

    static transients = ['enumerations']

    static constraints = {
        name unique:false
        enumAsString nullable: false, /*unique:true,*/ maxSize: 10000, validator: { encodedVal, obj ->
            Map<String, String> val = stringToMap(encodedVal)
            if (!val) return true
            if (val.size() < 1) return false
           // if (EnumeratedType.findByEnumAsString(encodedVal)) return false
            return true
        }
    }

    //WIP gormElasticSearch will support aliases in the future for now we will use searchable

    static searchable = {
        name boost:5
        enumAsString converter: EnumAsStringConverter
        except = ['relatedValueDomains', 'incomingRelationships', 'outgoingRelationships']
    }


    /**
     * Sets the map containing the enum values.
     *
     * The map is encoded and stored in {@link #enumAsString} field.
     *
     * @param map the map containing the enum values
     */
    void setEnumerations(Map<String, String> map) {
        enumAsString = mapToString(map)
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

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}, enumerations: ${enumerations}]"
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

    private static String mapToString(Map<String, String> map) {
        if (map == null) return null
        map.sort() collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')
    }

    private static Map<String, String> stringToMap(String s) {
        if (s == null) return null
        Map<String, String> ret = [:]
        s.split(/\|/).each { String part ->
            if (!part) return
            String[] pair = part.split("(?<!\\\\):")
            if (pair.length != 2) throw new IllegalArgumentException("Wrong enumerated value '$part' in encoded enumeration '$s'")
            ret[unquote(pair[0])] = unquote(pair[1])
        }
        return ret
    }

    static String quote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.each { original, replacement ->
            ret = ret.replace(original, replacement)
        }
        ret
    }

    static String unquote(String s) {
        if (s == null) return null
        String ret = s
        QUOTED_CHARS.reverseEach { original, pattern ->
            ret = ret.replace(pattern, original)
        }
        ret
    }

}
