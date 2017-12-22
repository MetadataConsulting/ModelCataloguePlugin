<html>
<head>
    <title><g:message code="dataMode.permissions" default="Data Model Permissions"/></title>
    <meta name='layout' content='springSecurityUI'/>
</head>
<body>
<h1>
    <span>${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</span>
</h1>
<table>
    <tbody>
    <g:each var="userPermissions" in="${userPermissionsList}">
        <tr>
            <th rowspan="${userPermissions.permissionList.size()}">${userPermissions.username}</th>
            <g:each var="permission" in="${userPermissions.permissionList}">
                <td><sec:permissionAsString permission="${permission}"/></td>
                <td>
                    <g:form action="revoke" controller="dataModelPermission" method="post">
                        <g:hiddenField name="username" value="${userPermissions.username}" />
                        <g:hiddenField name="id" value="${dataModel.id}" />
                        <g:hiddenField name="permission" value="${sec.permissionAsString(permission: permission).toLowerCase()}" />
                        <g:actionSubmit name="revoke" action="revoke" value="${message(code: 'delete', default: 'Delete')}"/>
                    </g:form>
                </td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
</table>
<g:form action="grant" controller="dataModelPermission" method="post">
    <g:select name="username" from="${usernameList}"/>
    <g:select name="permission" from="${['administration', 'read']}"/>
    <g:hiddenField name="id" value="${dataModel.id}" />
    <g:actionSubmit name="grant" value="${message(code: 'grant', default: 'Grant')}"/>
</g:form>

<g:if test="${flash.error}">
    <b>${flash.error}</b>
</g:if>

</body>
</html>
