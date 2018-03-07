<html>
<head>
    <title><g:message code="dataMode.permissions" default="Data Model Permissions"/></title>
    <meta name='layout' content='main'/>
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="dataMode.permissions" default="Data Model Permissions"/></h1>
    </div>
    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />
    <table class="table">
    <g:each var="dataModel" in="${rowList}">
        <tr>
            <td>
                <g:link controller="dataModelPermission" action="show" id="${dataModel.id}">${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</g:link>
            </td>
        </tr>
    </g:each>
    </table>
</div>
</body>
</html>
