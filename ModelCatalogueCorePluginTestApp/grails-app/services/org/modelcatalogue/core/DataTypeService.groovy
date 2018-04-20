package org.modelcatalogue.core

import grails.transaction.Transactional
import org.modelcatalogue.core.util.HibernateHelper
import org.modelcatalogue.core.util.lists.ListWithTotalAndType
import org.modelcatalogue.core.util.lists.Lists

@Transactional
class DataTypeService {

    static transactional = false

    def sessionFactory

    ListWithTotalAndType<DataType> findAllDataTypesInModel(Map params, DataModel model) {

        long modelId = model.id
        long hierarchyType = RelationshipType.hierarchyType.id
        long containmentType = RelationshipType.containmentType.id


        Closure queryClosure = {

            def max = it?.max
            def offset = it?.offset
            boolean itemsQuery = it instanceof Map

            String query = """select
                          ce.*,
                          dt.rule,
                          et.enum_as_string,
                          rt.data_class_id,
                          pt.measurement_unit_id,
                          case
                        when rt.id is not null then :referenceTypeDiscriminator
                        when pt.id is not null then :primitiveTypeDiscriminator
                        when et.id is not null then :enumeratedTypeDiscriminator
                        when dt.id is not null then :dataTypeDiscriminator
                        end as clazz_
                        from
                        data_type dt inner join catalogue_element ce on dt.id = ce.id
                        left outer join primitive_type pt on pt.id = ce.id
                        left outer join reference_type rt on rt.id = ce.id
                        left outer join enumerated_type et on et.id=ce.id 
                        WHERE
                                    ce.data_model_id = :modelId
                                    or dt.id in (
                                        select distinct dt.id
                                        from data_element de
                                    join data_type dt on de.data_type_id = dt.id
                                        where find_in_set(de.id, GetAllDestinations(GetTopLevelDataClasses(:modelId, :hierarchytypeId), :hierarchytypeId, :containmentTypeId))
                                    )
                                    ORDER BY ce.name

                        """

            final session = sessionFactory.currentSession

            // Create native SQL query.
            final sqlQuery = session.createSQLQuery(query)

            // Use Groovy with() method to invoke multiple methods
            // on the sqlQuery object.
            final results = sqlQuery.with {
                // Set domain class as entity.
                // Properties in domain class id, name, level will
                // be automatically filled.
                addEntity(DataType)

                // Set value for parameter startId.
                setLong('modelId', modelId)
                setLong('hierarchytypeId', hierarchyType)
                setLong('containmentTypeId', containmentType)

                setLong('referenceTypeDiscriminator', HibernateHelper.getDiscriminatorValue(sessionFactory, ReferenceType))
                setLong('primitiveTypeDiscriminator', HibernateHelper.getDiscriminatorValue(sessionFactory, PrimitiveType))
                setLong('enumeratedTypeDiscriminator', HibernateHelper.getDiscriminatorValue(sessionFactory, EnumeratedType))
                setLong('dataTypeDiscriminator', HibernateHelper.getDiscriminatorValue(sessionFactory, DataType))

                if (max) {
                    setMaxResults(max as Integer)
                }

                if (offset) {
                    setFirstResult(offset as Integer)
                }

                // Get all results.
                if (itemsQuery) {
                    return list()
                }
                return list().size()
            }

            results
        }

        Lists.lazy(params, DataType, queryClosure, queryClosure)


    }
}
