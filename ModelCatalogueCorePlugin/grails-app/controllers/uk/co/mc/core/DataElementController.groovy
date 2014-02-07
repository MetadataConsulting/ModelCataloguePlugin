package uk.co.mc.core

import grails.converters.JSON
import grails.rest.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class DataElementController extends RestfulController<DataElement>{

    static responseFormats = ['json', 'xml']

    DataElementController() {
        super(DataElement)
    }

    @Override
    def index(Integer max) {

        params.max = Math.min(max ?: 10, 100)

        def list = DataElement.list(params);

        def model=  [
                success:    true,
                total:      DataElement.count(),
                size:       list.size(),
                list:       list
        ]

        respond model;

    }


    @Override
    def show()
    {
        def model

        DataElement element= DataElement.get(params.id);

        if(!element){
            model = [
                    errors: [[message: "data element no found"]]
            ]

        }else{
            model=[
                    success: true,
                    instance: element
            ]

        }

        render model as JSON
    }

/**
 * Updates a resource for the given id
 * @param id
 */
//    @Transactional
//    @Override
//    def update() {
//
//        def model
//
//        if(handleReadOnly()) {
//            model = [
//                    errors: [[message: "data element read only"]]
//            ]
//            return model
//        }
//
//        DataElement dataElementInstance = DataElement.get(params.id)
//        if (dataElementInstance == null) {
//            model = [
//                    errors: [[message: "data element no found"]]
//            ]
//            return model
//        }
//
//        dataElementInstance.properties = getParametersToBind()
//
//        if (dataElementInstance.hasErrors()) {
//            model = [
//               errors: [dataElementInstance.errors]
//            ]
//            return model
//        }
//
//        dataElementInstance.save flush:true
//
//        request.withFormat {
//            '*'{
//                response.addHeader(HttpHeaders.LOCATION,
//                        g.createLink(
//                                resource: this.controllerName, action: 'show',id: instance.id, absolute: true,
//                                namespace: hasProperty('namespace') ? this.namespace : null ))
//                respond dataElementInstance, [status: OK]
//            }
//        }
//    }


}
