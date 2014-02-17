package uk.co.mc.core

import grails.artefact.Artefact
import grails.rest.RestfulController
import grails.transaction.Transactional
import grails.util.GrailsNameUtils

@Artefact("Controller")
@Transactional(readOnly = true)
class ModelCatalogueController<T> extends RestfulController<CatalogueElement>{

     @Override
     def index(Integer max) {
         params.max = Math.min(max ?: 10, 100)
         def list = listAllResources(params)
         def nextOffset = params?.offset + params?.max
         def previousOffset = params?.offset - params?.max
         def previousLink = "/${GrailsNameUtils.getPropertyName(el.getClass())}/?max=${params.max}&offset=${previousOffset}&sort=${params.sort}&order=${params.order}"
         def nextLink =  "/${GrailsNameUtils.getPropertyName(el.getClass())}/?max=${params.max}&offset=${nextOffset}&sort=${params.sort}&order=${params.order}"
         //max=${max}&offset=${next}
         //max=${max}&offset=${offset-max}
         def model=  [
                 success:    true,
                 total:      DataElement.count(),
                 size:       list.size(),
                 list:       list,
                 next: nextLink,
                 previous: previousLink,
         ]
         respond model
     }
}

//max: 10, offset: 100, sort: "title", order: "desc"
