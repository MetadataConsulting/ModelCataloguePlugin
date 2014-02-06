(function (window) {
    window['fixtures'] = window['fixtures'] || {};
    fixtures = window['fixtures']
    fixtures.dataElementList = {"success": true, "total": 3, "size": 3, "list": [
        {"class": "uk.co.mc.core.DataElement", "id": 1, "definition": "First data element definition", "description": "First data element", "extension": {}, "incomingRelationships": null, "name": "One", "outgoingRelationships": null, "revisionNumber": 1, "status": {"enumType": "uk.co.mc.core.CatalogueElement$Status", "name": "DRAFT"}, "versionNumber": 0},
        {"class": "uk.co.mc.core.DataElement", "id": 2, "definition": "Second data element definition", "description": "Second data element", "extension": {}, "incomingRelationships": null, "name": "Two", "outgoingRelationships": null, "revisionNumber": 1, "status": {"enumType": "uk.co.mc.core.CatalogueElement$Status", "name": "DRAFT"}, "versionNumber": 0},
        {"class": "uk.co.mc.core.DataElement", "id": 3, "definition": "Third data element definition", "description": "Third data element", "extension": {}, "incomingRelationships": null, "name": "Three", "outgoingRelationships": null, "revisionNumber": 1, "status": {"enumType": "uk.co.mc.core.CatalogueElement$Status", "name": "DRAFT"}, "versionNumber": 0}
    ]}

    fixtures.dataElementGet = {
        instance:{"extension":{}, "id":1, "outgoingRelationships":null, "revisionNumber":1, "definition":"First data element definition", "status":{"name":"DRAFT", "enumType":"uk.co.mc.core.CatalogueElement$Status"}, "description":"First data element", "name":"One", "class":"uk.co.mc.core.DataElement", "versionNumber":0, "incomingRelationships":null}, "success":true
    }


})(window)