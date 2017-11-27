package org.modelcatalogue.core.enumeration

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic

@Deprecated @CompileStatic class LegacyEnumerations {

    public static final Map<String, String> QUOTED_CHARS = [
            "\\": "&#92;",
            ":": "&#58;",
            "|": "&#124;",
            "%": "&#37;",
    ]

    static String mapToString(Map<String, String> map) {
        if (!map) return ""
        map.collect { key, val ->
            "${quote(key)}:${quote(val)}"
        }.join('|')
    }

    @CompileDynamic
    static Map<String, String> stringToMap(String s) {
        if (!s) return ImmutableMap.of()
        try {
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
        }catch (Exception ignored) {}

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
}
