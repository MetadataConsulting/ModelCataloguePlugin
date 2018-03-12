<html>
<head>
    <title><g:message code="dataImport.obo" default="Import OBO"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="dataImport.obo" default="Import OBO"/></h1>
    </div>
    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />

    <g:uploadForm url='/api/modelCatalogue/core/dataArchitect/imports/upload'>

        <fieldset>
            <ol>
                <li>
                    <label for="modelName"><g:message code="dataImport.obo.modelName" default="Ontology Model Name"/></label>
                    <g:textField name="modelName" id="modelName" placeholder="${g.message(code: 'dataImport.modelName.placeholder', default: 'Leave blank to use filename')}"/>
                </li>
                <li>
                    <label for="file"><g:message code="dataImport.file" default="File"/></label>
                    <input required="true" type="file" name="file" />
                </li>
                <li>
                    <input type="submit" class="btn btn-default" value="${g.message(code: 'dataImport.submit', default: 'Import')}"/>
                </li>
            </ol>
        </fieldset>
    </g:uploadForm>

</div><!-- /.panel-body -->
</body>
</html>
