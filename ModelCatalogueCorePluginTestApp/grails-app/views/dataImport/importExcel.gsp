<%@ page import="org.modelcatalogue.core.dataimport.excel.ExcelImportType" %>
<html>
<head>
    <title><g:message code="dataImport.excel" default="Import Excel"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="dataImport.excel" default="Import Excel"/></h1>
    </div>
    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />

    <g:uploadForm url='/api/modelCatalogue/core/dataArchitect/imports/upload'>
    <g:set var="excelImportTypeService" bean="excelImportTypeService"/>

        <fieldset>
            <ol>
                <li>
                    <label for="modelName"><g:message code="dataImport.modelName" default="Model Name"/></label>
                    <g:textField name="modelName" id="modelName" placeholder="${g.message(code: 'dataImport.modelName.placeholder', default: 'Leave blank to use filename')}"/>
                </li>
                <li>
                    <label for="excelImportType"><g:message code="dataImport.excel.importType" default="Select Excel Import Type"/></label>
                    <g:select name="excelImportType" from="${excelImportTypeService.filteredExcelImportTypes()*.humanReadableName}"/>
                </li>
                <li>
                    <label for="file"><g:message code="dataImport.file" default="File"/></label>
                    <input required="true" type="file" name="file" id="file"/>
                </li>
                <li>
                    <label for="excelConfigXMLFile"><g:message code="dataImport.excel.configXMLFile" default="Excel Config XML File"/></label>
                    <input type="file" name="excelConfigXMLFile" id="excelConfigXMLFile"/>
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
