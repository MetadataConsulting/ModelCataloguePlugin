package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.publishing.PublishingContext

/*
* Enumerated Types are data types that contain a list of enumerated values
* i.e. ['politics', 'history', 'science']
* */

//TODO marshalling and unmarshalling for enumerated type as string at the moment it returns the enum as string
//but we need ext. please see enumAsStringConverter.groovy for marshalling to index

class EnumeratedType extends DataType {

    static final SUBSET_METADATA_KEY = 'http://www.modelcatalogue.org/metadata/enumerateType#subset'


    String enumAsString = ""

    static transients = ['enumerations', 'enumerationsObject']

    static constraints = {
        name unique: false
        enumAsString nullable: true, maxSize: 5_000_000
    }

    static mapping = {
        enumAsString type: 'text'
    }

    /**
     * Sets the map containing the enum values.
     *
     * The map is encoded and stored in {@link #enumAsString} field.
     *
     *
     * @param map the map containing the enum values
     */
    void setEnumerations(Map<String, String> map) {
        if (map instanceof Enumerations) {
            enumAsString = map.toJsonString()
            return
        }
        enumAsString = Enumerations.from(map).toJsonString()
    }

    /**
     * Returns the map containing the enum values.
     *
     * The map is decoded from {@link #enumAsString} field.
     *
     * @return the map containing the enum values
     */
    Map<String, String> getEnumerations() {
        try {
            ImmutableMap.copyOf(Enumerations.from(enumAsString) as Map<String, String>)
        }catch(e){
            log.warn(this.id + " - " + e)
        }
    }

    Enumerations getEnumerationsObject(){
        Enumerations.from(enumAsString)
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
		def map = Enumerations.from(enumAsString)
		for (String key in map.keySet()) {
			results << new EnumBean(name:map.get(key),code:key)
		}
		return results

	}

    @Override
    void afterMerge(CatalogueElement destination) {
        if (destination instanceof EnumeratedType) {
            def thisMap = getEnumerationsObject()
            def destMap = destination.getEnumerationsObject()
            SortedMap<String, String> destEnums = new TreeMap<>(destMap)
            destEnums.putAll(thisMap)
            destination.setEnumerations(destEnums)
        }
        super.afterMerge(destination)
    }

    @Override
    String getExplicitRule() {
        return "x == null || x in [${enumerations.keySet().collect{ "'${it.replace('\'', '\\\'')}'" }.join(', ')}]"
    }

    String prettyPrint() {
        enumerations.sort().collect { key, value -> "$key: $value" }.join('\n')
    }

    @Override
    void beforeDraftPersisted(PublishingContext context) {
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
    Iterable<String> getInheritedAssociationsNames() {
        Iterables.concat(super.inheritedAssociationsNames, ImmutableSet.of('enumAsString'))
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
        // TODO: make it work with the ids
        if (propertyName != 'enumAsString') {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        List<Long> subsetIds = parseSubsetIds(metadata)

        if (!subsetIds) {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        Enumerations subset = (child as EnumeratedType).enumerationsObject.copy()
        Enumerations enumerations = persistent ? Enumerations.from(getPersistentValue('enumAsString')) : this.enumerationsObject

        for (Long id in subsetIds) {
            Enumeration enumeration = enumerations.getEnumerationById(id)
            if (enumeration) {
                subset.put(enumeration.id, enumeration.key, enumeration.value)
            } else {
                subset.removeEnumerationById(id)
            }
        }

        return subset.toJsonString()
    }

    static List<Long> parseSubsetIds(Map<String, String> metadata) {
        String value = metadata[SUBSET_METADATA_KEY]
        if (!value) {
            return []
        }
        return value.split(/\s*,\s*/)*.replaceAll(/\\,/,',').collect {
            Long.parseLong(it, 10)
        }
    }
}
