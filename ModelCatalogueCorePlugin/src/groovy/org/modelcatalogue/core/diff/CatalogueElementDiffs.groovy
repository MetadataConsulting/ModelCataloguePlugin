package org.modelcatalogue.core.diff

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Sets
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.HibernateHelper

import static com.google.common.base.Preconditions.checkNotNull

class CatalogueElementDiffs {

    private static final ImmutableList<String> IGNORED = ImmutableList.of(
        'status',
        'version',
        'versionCreated',
        'id',
        'semanticVersion',
        'dateCreated',
        'versionNumber',
        'lastUpdated',
        'ext',
        'dataModel',
        'enumAsString'
    )
    public static final String ENUMERATIONS_OBJECT = 'enumerationsObject'

    GrailsApplication grailsApplication

    CatalogueElementDiffs(GrailsApplication grailsApplication) {
        this.grailsApplication = grailsApplication
    }

    ImmutableMultimap<String, Diff> differentiate(CatalogueElement self, CatalogueElement other) {
        ImmutableMultimap.Builder<String, Diff> builder = ImmutableMultimap.builder()

        GrailsDomainClass selfClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(self).name), "No such domain class ${HibernateHelper.getEntityClass(self).name}") as GrailsDomainClass
        GrailsDomainClass otherClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(other).name), "No such domain class ${HibernateHelper.getEntityClass(other).name}") as GrailsDomainClass

        for (GrailsDomainClassProperty property in selfClass.properties) {
            if (
                !(property.name in IGNORED)
                && property.persistent
                && (!property.association || property.manyToOne || property.oneToOne)
                && (selfClass == otherClass || otherClass.hasPersistentProperty(property.name))
            ) {
                Object selfValue = self[property.name]
                Object otherValue = other[property.name]
                if (selfValue != otherValue) {
                    builder.put(Diff.keyForProperty(property.name), Diff.createPropertyChange(property.name, selfValue, otherValue))
                }
            }
        }

        if (selfClass.fullName == EnumeratedType.name) {
            Enumerations selfEnumerations = self.getProperty(ENUMERATIONS_OBJECT) as Enumerations
            if (otherClass.fullName == EnumeratedType.name) {
                Enumerations otherEnumerations = other.getProperty(ENUMERATIONS_OBJECT) as Enumerations
                for (Enumeration enumeration in selfEnumerations) {
                    Enumeration otherEnumeration = otherEnumerations.getEnumerationByKey(enumeration.key)
                    if (enumeration.value != otherEnumeration?.value || enumeration.deprecated != otherEnumeration?.deprecated) {
                        builder.put(Diff.keyForEnumeration(enumeration.key), Diff.createEnumerationChange(enumeration.key, enumeration, otherEnumeration))
                    }
                }

                Set<String> missingEnumerations = Sets.difference(otherEnumerations.keySet(), selfEnumerations.keySet())

                for (String key in missingEnumerations) {
                    builder.put(Diff.keyForEnumeration(key), Diff.createEnumerationChange(key, null, otherEnumerations.getEnumerationByKey(key)))
                }
            }
        }

        for(Map.Entry<String, String> extension in self.ext) {
            String selfValue = extension.value
            String otherValue = other.ext[extension.key]

            if (selfValue != otherValue) {
                builder.put(Diff.keyForExtension(extension.key), Diff.createExtensionChange(extension.key, selfValue, otherValue))
            }
        }

        Set<String> missingExtensions = Sets.difference(other.ext.keySet(), self.ext.keySet())

        for(String key in missingExtensions) {
            builder.put(Diff.keyForExtension(key), Diff.createExtensionChange(key, null, other.ext[key]))
        }

        ImmutableMap<String, Relationship> selfRelationships = collectRelationships(self)
        ImmutableMap<String, Relationship> otherRelationships = collectRelationships(other)

        for(Map.Entry<String, Relationship> rel in selfRelationships) {
            Relationship selfRelationship = rel.value
            Relationship otherRelationship = otherRelationships[rel.key]

            if (!otherRelationship) {
                builder.put(rel.key, Diff.createMissingRelationship(rel.value))
                continue
            }
            for(Map.Entry<String, String> extension in selfRelationship.ext) {
                String selfValue = extension.value
                String otherValue = otherRelationship.ext[extension.key]

                if (selfValue != otherValue) {
                    String changeKey = Diff.keyForRelationshipExtension(selfRelationship, extension.key)
                    builder.put(changeKey, Diff.createRelationshipMetadataChange(selfRelationship, extension.key, selfValue, otherValue))
                }
            }

            Set<String> missingRelationshipExtensions = Sets.difference(otherRelationship.ext.keySet(), selfRelationship.ext.keySet())

            for(String key in missingRelationshipExtensions) {
                builder.put(Diff.keyForRelationshipExtension(otherRelationship, key), Diff.createRelationshipMetadataChange(otherRelationship, key, null, otherRelationship.ext[key]))
            }
        }

        Set<String> missingRelationships = Sets.difference(otherRelationships.keySet(), selfRelationships.keySet())

        for(String key in missingRelationships) {
            builder.put(key, Diff.createNewRelationship(otherRelationships[key]))
        }

        return builder.build()
    }

    private static ImmutableMap<String, Relationship> collectRelationships(CatalogueElement self) {
        ImmutableMap.Builder<String, Relationship> selfRelationshipsBuilder = ImmutableMap.builder()

        for (Relationship relationship in self.outgoingRelationships) {
            selfRelationshipsBuilder.put(Diff.keyForRelationship(relationship), relationship)
        }

        selfRelationshipsBuilder.build()
    }


}
