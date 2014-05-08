package org.modelcatalogue.core

import org.modelcatalogue.core.util.ListWrapper
import org.modelcatalogue.core.util.ValueDomains

class DataTypeController<T> extends AbstractCatalogueElementController<DataType> {

    DataTypeController() {
        super(DataType)
    }

    DataTypeController(Class<? extends DataType> resource) {
        super(resource)
    }


    def valueDomains(Integer max){
        setSafeMax(max)
        DataType dataType = queryForResource(params.id)
        if (!dataType) {
            notFound()
            return
        }

        int total = dataType.relatedValueDomains.size()
        def list = sortOffsetMaxMinList(dataType.relatedValueDomains, params)
        def links = ListWrapper.nextAndPreviousLinks(params, "/${resourceName}/${params.id}/valueDomain", total)

        respondWithReports new ValueDomains(
                items: list,
                previous: links.previous,
                next: links.next,
                total: total,
                offset: params.int('offset') ?: 0,
                page: params.int('max') ?: 0
        )
    }


    protected static List<ValueDomain> sortOffsetMaxMinList(Collection<ValueDomain> valueDomains, Map params){

        valueDomains = valueDomains.toList()

        if (params.sort) {
            if (params.order=="desc") {
                valueDomains.sort{it.getProperty(params.sort)}.reverse()
            }else{
                valueDomains.sort{it.getProperty(params.sort)}
            }
        }

        if(params.offset || params.max){
            Integer offset = (params.offset)? params.offset.toInteger() : 0
            Integer max = valueDomains.size()
            if(offset >= max -1){offset = max - 2}
            if(params.max){
                if((params.max + offset) < max){
                    max = params.max + offset -1
                }else{
                    max = max - 1
                }
            }else{
                max = max -1
            }

            valueDomains = valueDomains[offset..max]
        }

        valueDomains
    }


}
