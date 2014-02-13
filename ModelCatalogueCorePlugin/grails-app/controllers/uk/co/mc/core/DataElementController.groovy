package uk.co.mc.core

import grails.converters.JSON
import grails.rest.RestfulController
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
        def list = listAllResources(params)
        def model=  [
                success:    true,
                total:      DataElement.count(),
                size:       list.size(),
                list:       list
        ]
        respond model
    }

    /**
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

    def tester(){

        def rt = new RelationshipType(name:"Synonym",
                sourceToDestination: "SynonymousWith",
                destinationToSource: "SynonymousWith",
                sourceClass: DataElement,
                destinationClass: DataElement).save()

        def de1 = new DataElement(id: 1, name: "One", description: "First data element").save()
        def de2 = new DataElement(id: 2, name: "Two", description: "Second data element").save()


        def rel = Relationship.link(de1, de2, rt).save()

        def de3 = new DataElement(id:3, name: "Three",
                description: "Third data element").save()


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
