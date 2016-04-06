# Creating Exports

Export are using the data stored in the catalogue to create new artifacts such as MS Word documents, MS Excel
spreadsheets, XML or JSON files. See [Technology Stack](../frameworks/index.md) for a list of libraries you can use for building
exports.

## Transformation Class
You usually create a separate class to make the transformation from the internal data to external form. For example

[DataModelToDocxExporter](../../../ModelCatalogueCorePlugin/src/groovy/org/modelcatalogue/core/export/inventory/DataModelToDocxExporter.groovy).
The transformation class takes usually two parameters - an element to be exported and output stream to write the result
as asset. All the dependencies has to be injected as well (usually manually using the constructor injection).

```
class DataModelToDocxExporter {

    DataClassService dataClassService
    DataModel rootModel

    ...

    DataModelToDocxExporter(DataModel rootModel, DataClassService dataClassService) { ... }

    void export(OutputStream outputStream) { ... }

}

```

It's good practice to accompany the class with integration test of at least following structure as seen in
[DataModelToDocxExporterSpec](../../../ModelCatalogueCorePlugin/test/integration/org/modelcatalogue/core/export/inventory/DataModelToDocxExporterSpec.groovy):

```
class DataModelToDocxExporterSpec extends IntegrationSpec {

    // injected dependencies
    ElementService elementService
    DataModelService dataModelService
    InitCatalogueService initCatalogueService
    DataClassService dataClassService

    // temporary folder to save the result
    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def setup() {
        // starting with fresh catalogue just with the relationship types
        initCatalogueService.initDefaultRelationshipTypes()
    }

    def "export model to docx"() {
        when: "we export the model to document"
        File file = temporaryFolder.newFile("${System.currentTimeMillis()}.docx")
        DataModel model = buildTestModel()

        new DataModelToDocxExporter(DataModel.get(model.id), dataClassService).export(file.newOutputStream())

        // view the result
        FileOpener.open file

        then: "nothing wrong happens"
        noExceptionThrown()

    }

    private DataModel buildTestModel() {
        // build the test model using the builder
        DefaultCatalogueBuilder builder = new DefaultCatalogueBuilder(dataModelService, elementService)
        ...
    }

```

## Controller Action

Controller as entry point to the application so we have to make an action using the transformation class. Most
of the exports happens in background thread. See the following example:

 ```
     def exportGelSpecification() {
         // get the element to be transformed
         DataModel model = DataModel.get(params.id)

         // null guard
         if(!model) {
             response.status = 404
             return
         }

         // generally speaking, only primitives should be passes to the async closure writing the asset
         Long modelId = model.id
         // create new asset and execute a asynchoronous write
         def assetId= assetService.storeReportAsAsset(
             // data model to be linked to created asset
             model,
             // name of the asset created
             name: "${model.name} report as MS Word Document",
             // file name of the asset created
             originalFileName: "${model.name}-${model.status}-${model.version}.docx",
             // content type of the asset
             contentType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
         )  { OutputStream out ->
             // code executed asynchronously writing the created export to asset
             new DataModelToDocxExporter(DataModel.get(modelId), dataClassService).export(out)
         }

         // redirecting to the asset created
         response.setHeader("X-Asset-ID",assetId.toString())
         redirect controller: 'asset', id: assetId, action: 'show'
     }
 ```

See [Notes on ExecutorService](../executor_service.md) for details how to handle asynchronous code in controllers.

## URL Mappings
There are no implicit mappings in the model Catalogue. Every controller's action has to be added to `*UrlMappings.groovy`
file in `grails-app/conf`.

```
static mappings = {
    "/api/modelCatalogue/core/genomics/exportGelSpecification/$id" (controller: 'genomics', action: 'exportGelSpecification', method: HttpMethod.GET)
}
```

## Registering Export (Report)

There is a `reportsRegistry` bean which registers new reports. Place the configuration in the `*GrailsPlugin` class declaring you
plugin in `doWithApplicationContext` closure:

```
def doWithApplicationContext = { ctx ->

    ReportsRegistry reportsRegistry = ctx.getBean(ReportsRegistry)

    reportsRegistry.register {
        // creates new asset, user is asked for the new asset name
        creates asset
        // or just a link which is displayed in separate tab
        // creates link

        // the title displayed in the "Exports" dropdown
        title "GEL Data Specification Report"
        // or title { "$it.name Specification Report" }

        // Value to be displayed in export modal for depth of export. Uses
        // default value for export when set to null (or omitted).
        depth 3

        // Default name for export file.
        defaultName { "${it.name} report as MS Excel Document" }

        // for which type this report applies
        type DataModel
        // or testing the item type in case of list exports
        // item DataModel
        // or more generic conditions which are stacked into one logical and expression
        // when { it.semanticVersion != null }
        // and { it.releaseNotes != null }

        link controller: 'genomics', action: 'exportGelSpecification', id: true
        // or create link with closure definition
        // link {
        //      controller = 'genomics'
        //      action = 'exportGelSpecification'
        //      id = true
        // }
        // or create link by uri
        // uri "/some/uri"
        // or absolute URL
        // url "http://www.example.com"
    }

}
```

See [ReportDescriptorBuilder](../../../ModelCatalogueCorePlugin/src/groovy/org/modelcatalogue/core/reports/ReportDescriptorBuilder.groovy) for full DSL definition.
