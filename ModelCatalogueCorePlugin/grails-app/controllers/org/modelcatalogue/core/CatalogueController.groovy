package org.modelcatalogue.core

import org.springframework.http.HttpStatus

class CatalogueController  {

   def xref() {
       String resource  = params.resource
       Long id          = params.long('id')
       Integer version  = params.int('version')

       CatalogueElement element

       if (version) {
           Long lastVersion = id
           element = PublishedElement.where {
               versionNumber == version && latestVersionId == lastVersion
           }.get()
       } else {
           element = CatalogueElement.get(id)
       }
       if (!element) {
           render status: HttpStatus.NOT_FOUND
           return
       }

       redirect controller: resource, action: 'show', id: element.id
   }

}
