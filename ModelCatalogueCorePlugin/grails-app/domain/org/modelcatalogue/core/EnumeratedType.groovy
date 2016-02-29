package org.modelcatalogue.core

import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.enumeration.LegacyEnumerations
import org.modelcatalogue.core.util.OrderedMap

/*
* Enumerated Types are data types that contain a list of enumerated values
* i.e. ['politics', 'history', 'science']
* */

//TODO marshalling and unmarshalling for enumerated type as string at the moment it returns the enum as string
//but we need ext. please see enumAsStringConverter.groovy for marshalling to index

class EnumeratedType extends DataType {

    static final SUBSET_METADATA_KEY = 'http://www.modelcatalogue.org/metadata/enumerateType#subset'


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
     *
     * @param map the map containing the enum values
     */
    void setEnumerations(Map<String, String> map) {
        enumAsString = Enumerations.from(map).toJsonString()
    }

    /**
     * Returns the map containing the enum values.
     *
     * The map is decoded from {@link #enumAsString} field.
     *
     * @return the map containing the enum values
     */
    Enumerations getEnumerations() {
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
        // TODO: make it work with the ids
        if (propertyName != 'enumAsString') {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        List<String> subsetKeys = parseSubsetKeys(metadata)

        if (!subsetKeys) {
            return super.getValueToBeInherited(child, propertyName, metadata, persistent)
        }

        Map<String, String> subset = new LinkedHashMap<String, String>((child as EnumeratedType).enumerations)
        Map<String, String> enumerations = new LinkedHashMap<String, String>(persistent ? Enumerations.from(getPersistentValue('enumAsString')) : this.enumerations)

        for (String key in subsetKeys) {
            subset[key] = enumerations[key]
        }

        return Enumerations.from(subset).toJsonString()
    }

    static List<String> parseSubsetKeys(Map<String, String> metadata) {
        String value = metadata[SUBSET_METADATA_KEY]
        if (!value) {
            return []
        }
        return value.split(/\s*,\s*/)*.replaceAll(/\\,/,',')
    }
}
