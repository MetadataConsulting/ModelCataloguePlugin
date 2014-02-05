package uk.co.mc.core

import grails.converters.JSON

class DataElementController {

    static defaultAction = "list"


    def list() {
        def list = DataElement.list(params)

        def model=  [
            success:    true,
            total:      DataElement.count(),
            size:       list.size(),
            list:       list
        ]

        render  model as JSON
    }

    def get(long id)
    {
        DataElement element= DataElement.get(id);
        def model

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


    def create(){

    }



}
