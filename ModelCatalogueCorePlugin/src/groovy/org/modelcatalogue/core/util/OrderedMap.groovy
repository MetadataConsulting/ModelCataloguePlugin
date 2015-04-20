package org.modelcatalogue.core.util

class OrderedMap {

    /**
     * Prepares the map to be passed as JSON to the frontend.
     *
     * This method will return the same map if it's already ready for frontend.
     * 
     * @param map map to be prepared for frontend
     * @return map ready for frontend
     */
    static Map<String, Object> toJsonMap(Map<String, String> map) {
        if (!map) {
            return [type: 'orderedMap', values: []]
        }
        if (map.type == 'orderedMap') {
            return map
        }
        [type: 'orderedMap', values: map.collect { key, value -> [key: key, value: value]}]
    }


    static Map<String, String> fromJsonMap(Map<String, Object> jsonOrMap) {
        if (!jsonOrMap) {
            return [:]
        }
        if (jsonOrMap.type == 'orderedMap') {
            if (jsonOrMap.values instanceof List) {
                Map<String, String> newVal = [:]
                for (Map<String, Object> item in (jsonOrMap.values as List<Map<String, Object>>)) {
                    newVal[item.key as String] = item.value as String
                }
                return newVal
            }
            return [:]
        }
        return jsonOrMap as Map<String, String>
    }
}
