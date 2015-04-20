package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import org.modelcatalogue.core.util.OrderedMap
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class AssetControllerIntegrationSpec extends AbstractCatalogueElementControllerIntegrationSpec {

    @Unroll
    def "expect uploaded asset will have #expectedName if params are #params"() {
        GrailsMockMultipartFile mockFile = new GrailsMockMultipartFile('asset', 'readme.txt', 'text/plain', 'some file contents'.bytes)

        controller.request.method       = 'POST'
        controller.params.name          = params.name
        controller.params.description   = params.description
        controller.response.format      = 'json'

        controller.request.addFile mockFile

        controller.upload()

        def json = controller.response.json

        expect:
        json.name               == expectedName
        json.contentType        == 'text/plain'
        json.originalFileName   == 'readme.txt'
        json.size               == mockFile.size

        when:
        Asset asset = Asset.findByName(expectedName)

        then:
        json
        json.id                == asset.id
        asset
        asset.contentType      == 'text/plain'
        asset.originalFileName == 'readme.txt'
        asset.size             == mockFile.size
        asset.md5

        cleanup:
        asset?.delete()

        where:
        expectedName    | params
        'readme.txt'    | [:]
        'blah'          | [name: 'blah']
    }

    def "existing asset is updated if already exists and the id is passed to the action"() {
        Asset existing = new Asset(name: 'existing')
        existing.save()

        GrailsMockMultipartFile mockFile = new GrailsMockMultipartFile('asset', 'readme.txt', 'text/plain', 'some file contents'.bytes)

        controller.request.method       = 'POST'
        controller.params.id            = existing.id
        controller.params.name          = 'updated'
        controller.response.format      = 'json'

        controller.request.addFile mockFile

        controller.upload()

        def json = controller.response.json

        expect:
        json.name               == 'updated'
        json.contentType        == 'text/plain'
        json.originalFileName   == 'readme.txt'

        when:
        Asset asset = Asset.findByName('updated')

        then:
        json
        json.id                 != existing.id
        asset
        asset.id                != existing.id
        asset.originalFileName  == 'readme.txt'
        asset.md5

        cleanup:
        if (existing) {
            for (Asset a in Asset.findAllByLatestVersionId(existing.latestVersionId ?: existing.id)) {
                a.delete()
            }
        }
    }

    @Override
    Map getPropertiesToEdit(){
        [name: "changedName", description: "edited description ", code: "AA123"]
    }

    @Override
    Map getNewInstance(){
       [name:"new data element", description: "the DE_author of the book", code: "12312312308"]
    }

    @Override
    Map getBadInstance(){
        [name: "t"*300, description: "asdf"]
    }

    @Override
    Class getResource() {
        Asset
    }

    @Override
    AbstractCatalogueElementController getController() {
        new AssetController()
    }

    @Override
    String getResourceName() {
        GrailsNameUtils.getLogicalPropertyName(getClass().getSimpleName(), "ControllerIntegrationSpec")
    }

    @Override
    Asset getLoadItem() {
        Asset.findByName("file")
    }

    @Override
    Asset getAnotherLoadItem() {
        Asset.findByName("file1")
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        assert item.versionCreated
        checkStringProperty(json.modelCatalogueId , item.modelCatalogueId, "modelCatalogueId")
        checkProperty(json.status , item.status, "status")
        checkProperty(OrderedMap.fromJsonMap(json.ext), item.ext, "extension")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.status , outputItem.status, "status")
        checkMapProperty(OrderedMap.fromJsonMap(json.ext) , inputItem.ext, "extension")
        checkProperty(json.versionNumber , outputItem.versionNumber, "versionNumber")
        return true
    }

    @Override
    protected getTotalRowsExported() { 7 }

    def getPaginationParameters(String baseLink) {
        [
                // no,size, max , off. tot. next                           , previous
                [1, 7, 10, 0, 7, "", ""],
                [2, 5, 5, 0, 7, "${baseLink}?max=5&offset=5", ""],
                [3, 2, 5, 5, 7, "", "${baseLink}?max=5&offset=0"],
        ]
    }

}
