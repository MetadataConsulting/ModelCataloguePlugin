package uk.co.mc.core

import grails.rest.RestfulController
import grails.util.GrailsNameUtils

class ModelCatalogueController extends RestfulController<Object>{

     @Override
     def index(Integer max) {
         params.max = Math.min(max ?: 10, 100)
         def list = listAllResources(params)
         def nextOffset = params?.offset + params?.max
         def previousOffset = 
         def previousLink = params?.offset - params?.max
         def nextLink =  "/${GrailsNameUtils.getPropertyName(el.getClass())}/"
         //max=${max}&offset=${next}
         //max=${max}&offset=${offset-max}
         def model=  [
                 success:    true,
                 total:      DataElement.count(),
                 size:       list.size(),
                 list:       list,
                 next: link,
                 previous: link,
         ]
         respond model
     }
}

//max: 10, offset: 100, sort: "title", order: "desc"
