<html>
<head>
    <title><g:message code="dataMode.permissions" default="Data Model Permissions"/></title>
    <meta name='layout' content='springSecurityUI'/>
</head>
<body>
<div class="list">
    <ul>
        <g:each var="dataModel" in="${rowList}">
            <li><g:link controller="dataModelPermission" action="show" id="${dataModel.id}">${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</g:link> </li>
        </g:each>
    </ul>
</div>
</body>
</html>
