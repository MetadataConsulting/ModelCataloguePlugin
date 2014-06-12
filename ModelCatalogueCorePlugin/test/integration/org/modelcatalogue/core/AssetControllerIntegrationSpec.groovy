package org.modelcatalogue.core

import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.plugins.testing.GrailsMockMultipartFile
import spock.lang.Unroll

/**
 * Created by adammilward on 27/02/2014.
 */
class AssetControllerIntegrationSpec extends AbstractPublishedElementControllerIntegrationSpec {

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
        json.id                 == existing.id
        asset
        asset.id                == existing.id
        asset.originalFileName  == 'readme.txt'

        cleanup:
        if (asset && existing.id != asset.id) {
            asset.delete()
        }
        existing?.delete()
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
    String getBadXmlError(){
        "ttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttProperty [name] of class [class org.modelcatalogue.core.Asset] with value [tttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt] does not fall within the valid size range from [1] to [255]"
        //"Property [name] of class [class org.modelcatalogue.core.${resourceName.capitalize()}] cannot be null"
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
    def xmlCustomPropertyCheck(xml, item){
        super.xmlCustomPropertyCheck(xml, item)
        checkProperty(xml.modelCatalogueId, item.modelCatalogueId, "modelCatalogueId")
        checkProperty(xml.@status, item.status, "status")
        checkProperty(xml.@versionNumber, item.versionNumber, "versionNumber")
        def inputItem = item.getProperty("ext")
        inputItem.each{ key, value ->
            def extension = xml.depthFirst().find{it.name()=="extension" && it.@key == key}
            checkProperty(value, extension.toString(), "extension")
        }
        return true
    }

    @Override
    def xmlCustomPropertyCheck(inputItem, xml, outputItem){
        super.xmlCustomPropertyCheck(inputItem, xml, outputItem)
        checkProperty(xml.modelCatalogueId, inputItem.modelCatalogueId, "modelCatalogueId")
        checkProperty(xml.@status, outputItem.status, "status")
        checkProperty(xml.@versionNumber, outputItem.versionNumber, "versionNumber")
        outputItem.getProperty("ext").each{ key, value ->
            def extension = xml.depthFirst().find{it.name()=="extension" && it.@key == key}
            checkProperty(value, extension.toString(), "extension")
        }
        return true
    }

    @Override
    def customJsonPropertyCheck(item, json){
        super.customJsonPropertyCheck(item, json)
        checkStringProperty(json.modelCatalogueId , item.modelCatalogueId, "modelCatalogueId")
        checkProperty(json.status , item.status, "status")
        checkProperty(json.ext, item.ext, "extension")
        checkProperty(json.versionNumber , item.versionNumber, "versionNumber")
        return true
    }

    @Override
    def customJsonPropertyCheck(inputItem, json, outputItem){
        super.customJsonPropertyCheck(inputItem, json, outputItem)
        checkProperty(json.status , outputItem.status, "status")
        checkMapProperty(json.ext , inputItem.ext, "extension")
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
