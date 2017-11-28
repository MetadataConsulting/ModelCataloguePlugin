package org.modelcatalogue.core.diff

import static com.google.common.base.Preconditions.checkNotNull
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Sets
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.EnumeratedType
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.enumeration.Enumeration
import org.modelcatalogue.core.enumeration.Enumerations
import org.modelcatalogue.core.util.HibernateHelper

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
        differentiate(self, other, null, null)
    }

    ImmutableMultimap<String, Diff> differentiate(CatalogueElement self, CatalogueElement other, DataClass parentClass) {
        differentiate(self, other, parentClass, null, null)
    }

    ImmutableMultimap<String, Diff> differentiate(CatalogueElement self, CatalogueElement other, DataClass parentClass, DataElement parentElement) {

        //if there isn't another catalogue element or isn't a self of they are the same - return no change
        if (!other || !self || self == other) {
            return ImmutableMultimap.of(Diff.keyForSelf(self?.latestVersionId ?: self?.id ?: other?.latestVersionId ?: other?.id), Diff.createEntityChange(self, other, parentClass, parentElement))
        }

        //instantiate the multimpa builder
        ImmutableMultimap.Builder<String, Diff> builder = ImmutableMultimap.builder()

        //check self and other aren't null
        GrailsDomainClass selfClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(self).name), "No such domain class ${HibernateHelper.getEntityClass(self).name}") as GrailsDomainClass
        GrailsDomainClass otherClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(other).name), "No such domain class ${HibernateHelper.getEntityClass(other).name}") as GrailsDomainClass

        //for each property of the class, check if anything has changed

        for (GrailsDomainClassProperty property in selfClass.properties) {
            //ignore the properties in the ignore list
            //ignore the transient properties
            //ignore the relationship properties i.e. associated
            if (
                !(property.name in IGNORED)
                && property.persistent
                && (!property.association || property.manyToOne || property.oneToOne)
                && (selfClass == otherClass || otherClass.hasPersistentProperty(property.name))
            ) {
                //compare the properties to see if anything has changed
                Object selfValue = self[property.name]
                Object otherValue = other[property.name]

                //if the properties is a reference type i.e. the datatype property in a dataelement class

                if (CatalogueElement.isAssignableFrom(property.referencedPropertyType)) {

                    //get the reference types so that you can compare them

                    CatalogueElement selfElement = (CatalogueElement) selfValue
                    CatalogueElement otherElement = (CatalogueElement) otherValue

                    //compare the reference types, if they are different return a diff

                    if ((selfElement?.latestVersionId ?: selfElement?.id) != (otherElement?.latestVersionId ?: otherElement?.id)) {
                        builder.put(Diff.keyForProperty(property.name), Diff.createPropertyChange(property.name, self, selfValue, otherValue, parentClass, parentElement))
                    }
                } else {

                    //else if the properties are simple properties and are different then return the dif

                    if (selfValue != otherValue) {
                        builder.put(Diff.keyForProperty(property.name), Diff.createPropertyChange(property.name, self, selfValue, otherValue, parentClass, parentElement))
                    }
                }

            }
        }

//if the class is enumerated then do a diff on the specific enumerations the specific

        if (selfClass.fullName == EnumeratedType.name) {
            Enumerations selfEnumerations = self.getProperty(ENUMERATIONS_OBJECT) as Enumerations
            if (otherClass.fullName == EnumeratedType.name) {
                Enumerations otherEnumerations = other.getProperty(ENUMERATIONS_OBJECT) as Enumerations
                for (Enumeration enumeration in selfEnumerations) {
                    Enumeration otherEnumeration = otherEnumerations.getEnumerationById(enumeration.id)
                    if (enumeration.key != otherEnumeration?.key || enumeration.value != otherEnumeration?.value || enumeration.deprecated != otherEnumeration?.deprecated) {
                        builder.put(Diff.keyForEnumeration(enumeration.id), Diff.createEnumerationChange(self, enumeration.id, enumeration, otherEnumeration, parentClass, parentElement))
                    }
                }

                Set<Long> missingEnumerations = Sets.difference(otherEnumerations.iterator().collect { it.id }.toSet(), selfEnumerations.iterator().collect { it.id }.toSet())

                for (Long id in missingEnumerations) {
                    builder.put(Diff.keyForEnumeration(id), Diff.createEnumerationChange(self, id, null, otherEnumerations.getEnumerationById(id), parentClass, parentElement))
                }
            }
        }

        //do a diff on the catalogue element metadata

        for(Map.Entry<String, String> extension in self.ext) {
            String selfValue = extension.value
            String otherValue = other.ext[extension.key]

            if (selfValue != otherValue) {
                builder.put(Diff.keyForExtension(extension.key), Diff.createExtensionChange(extension.key, self, selfValue, otherValue, parentClass, parentElement))
            }
        }

        Set<String> missingExtensions = Sets.difference(other.ext.keySet(), self.ext.keySet())

        for(String key in missingExtensions) {
            builder.put(Diff.keyForExtension(key), Diff.createExtensionChange(key, self, null, other.ext[key], parentClass, parentElement))
        }


        //do a diff on the relationships


        //get the relationships for self and other

        ImmutableMap<String, Relationship> selfRelationships = collectRelationships(self)
        ImmutableMap<String, Relationship> otherRelationships = collectRelationships(other)

        //iterate through the self relationships

        for(Map.Entry<String, Relationship> rel in selfRelationships) {
            Relationship selfRelationship = rel.value
            Relationship otherRelationship = otherRelationships[rel.key]

            //if there isn't an 'other' relationships create a diff

            if (!otherRelationship) {
                builder.put(rel.key, Diff.createMissingRelationship(self, rel.value, parentClass, parentElement))
                continue
            }


            //check the multiplicity of the relationships for change
            for(Map.Entry<String, String> extension in selfRelationship.ext) {
                String selfValue = extension.value
                String otherValue = otherRelationship.ext[extension.key]

                if (selfValue != otherValue) {
                    String changeKey = Diff.keyForRelationshipExtension(selfRelationship, extension.key)
                    builder.put(changeKey, Diff.createRelationshipMetadataChange(selfRelationship, extension.key, self, selfValue, otherValue, parentClass, parentElement))
                }
            }

            Set<String> missingRelationshipExtensions = Sets.difference(otherRelationship.ext.keySet(), selfRelationship.ext.keySet())

            for(String key in missingRelationshipExtensions) {
                builder.put(Diff.keyForRelationshipExtension(otherRelationship, key), Diff.createRelationshipMetadataChange(otherRelationship, key, self, null, otherRelationship.ext[key], parentClass, parentElement))
            }
        }

        Set<String> missingRelationships = Sets.difference(otherRelationships.keySet(), selfRelationships.keySet())

        for(String key in missingRelationships) {
            builder.put(key, Diff.createNewRelationship(self, otherRelationships[key], parentClass, parentElement))
        }

        return builder.build()
    }


    ImmutableMultimap<String, Diff> differentiateTopLevelClasses(CatalogueElement self, CatalogueElement other) {

        //if there isn't another catalogue element or isn't a self of they are the same - return
        if (!other || !self || self == other) {
            return ImmutableMultimap.of(Diff.keyForSelf(self?.latestVersionId ?: self?.id ?: other?.latestVersionId ?: other?.id), Diff.createEntityChange(self, other, null, null))
        }

        //instantiate the multimpa builder
        ImmutableMultimap.Builder<String, Diff> builder = ImmutableMultimap.builder()

        //check self and other aren't null
        GrailsDomainClass selfClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(self).name), "No such domain class ${HibernateHelper.getEntityClass(self).name}") as GrailsDomainClass
        GrailsDomainClass otherClass = checkNotNull(grailsApplication.getDomainClass(HibernateHelper.getEntityClass(other).name), "No such domain class ${HibernateHelper.getEntityClass(other).name}") as GrailsDomainClass


        //get the parentOf relationships for self and other

        ImmutableMap<String, Relationship> selfRelationships = collectParentOfRelationships(self)
        ImmutableMap<String, Relationship> otherRelationships = collectParentOfRelationships(other)


        for(Map.Entry<String, Relationship> rel in selfRelationships) {
            Relationship selfRelationship = rel.value
            Relationship otherRelationship = otherRelationships[rel.key]

            //this is the opposite of the differentiate method above (as we are checking for incoming relationships rather than outgoing relationships)
            //i.e. if the relationship in the other is there but not in the source it means that the source doesn't have a parent any more i.e. the rel has
            //been deleted

            if (!otherRelationship) {
                builder.put(rel.key, Diff.createNewRelationship(self, rel.value, null))
                continue
            }
        }

        Set<String> missingRelationships = Sets.difference(otherRelationships.keySet(), selfRelationships.keySet())

        //this is the opposite of the differentiate method above (as we are checking for incoming relationships rather than outgoing relationships)
        //i.e. if the relationship in the other is there but not in the source it means that the source doesn't have a parent any more i.e. the rel has
        //been deleted

        for(String key in missingRelationships) {
            builder.put(key, Diff.createMissingRelationship(self, otherRelationships[key], null, null))
        }

        return builder.build()


    }

    private static ImmutableMap<String, Relationship> collectRelationships(CatalogueElement self) {
        Map<String, Relationship> selfRelationshipsBuilder = [:]

        for (Relationship relationship in self.outgoingRelationships) {
            if (!relationship.relationshipType.system) {
                selfRelationshipsBuilder.put(Diff.keyForRelationship(relationship), relationship)
            }
        }

        ImmutableMap.copyOf(selfRelationshipsBuilder)
    }

    private static ImmutableMap<String, Relationship> collectParentOfRelationships(DataClass self) {
        Map<String, Relationship> selfRelationshipsBuilder = [:]

        for (Relationship relationship in self.getIncomingRelationshipsByType(RelationshipType.hierarchyType)) {
            if (!relationship.relationshipType.system) {
                selfRelationshipsBuilder.put(Diff.keyForRelationship(relationship), relationship)
            }
        }

        ImmutableMap.copyOf(selfRelationshipsBuilder)
    }




}
