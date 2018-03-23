<html>
<head>
    <title><g:message code="dataModel.permissions" default="Data Model Permissions"/></title>
    <meta name='layout' content='main'/>
</head>
<body>
<ol class="breadcrumb">
    <li class="breadcrumb-item"><g:link controller="dataModelPermission" action="index"><g:message code="dataModel.permissions" default="Data Model Permissions"/></g:link></li>
    <li class="breadcrumb-item active">${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</li>
</ol>
<div class="panel-body">
    <div class="page-header">
        <h1>
            <span>${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</span>
        </h1>
    </div>

<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />
<table class="table">
    <thead>
    <tr>
        <th><g:message code="dataModel.permissions.username" default="Username"/></th>
        <th><g:message code="dataModel.permissions.permission" default="ACL Permission"/></th>
        <th><g:message code="dataModel.permissions.actions" default="Actions"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each var="userAndPermissionList" in="${userAndPermissionListList}">
        <tr>
          <td><b>${userAndPermissionList.username}</b></td>
            <g:each var="permission" in="${userAndPermissionList.permissionList}">
                <td><sec:permissionAsString permission="${permission}"/></td>
                <td>
                    <g:form action="revoke" controller="dataModelPermission" method="post">
                        <g:hiddenField name="username" value="${userAndPermissionList.username}" />
                        <g:hiddenField name="id" value="${dataModel.id}" />
                        <g:hiddenField name="permission" value="${sec.permissionAsString(permission: permission).toLowerCase()}" />
                        <g:actionSubmit name="revoke" action="revoke" class="btn btn-danger" value="${message(code: 'delete', default: 'Delete')}"/>
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
    <g:actionSubmit name="grant" class="btn btn-default" value="${message(code: 'grant', default: 'Grant')}"/>
</g:form>
</div>
</body>
</html>
