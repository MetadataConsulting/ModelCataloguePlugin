<%@ page import="org.springframework.security.acls.domain.BasePermission" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<html>
<head>
    <title><g:message code="dataModel.permissions.show" default="Show Data Model Permissions"/></title>
    <meta name='layout' content='main'/>
</head>
<body>
<ol class="breadcrumb">
    <li class="breadcrumb-item"><g:link controller="dataModelPermission" action="index"><g:message code="dataModel.permissions" default="Data Model Permissions"/></g:link></li>
    <li class="breadcrumb-item active">
        <g:link controller="dataModelPermission" action="show" id="${dataModel.id}">${dataModel.name} ${dataModel.status} ${dataModel.semanticVersion}</g:link>
    </li>
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
        <th><g:message code="dataModel.permissions.permission.read" default="Read ACL Permission"/></th>
        <th><g:message code="dataModel.permissions.permission.administration" default="Administration ACL Permission"/></th>
    </tr>
    </thead>
    <tbody>
    <g:each var="userAndPermissionList" in="${userAndPermissionListList.sort { a, b -> a.username <=> b.username }}">
        <tr>
          <td>
              <b>${userAndPermissionList.username}</b>
          </td>
            <td>
                <g:render template="deletePermission" model="[dataModel: dataModel,
                                                              username: userAndPermissionList.username,
                                                              permissionList: userAndPermissionList.permissionList.findAll { it == BasePermission.READ }]"/>
            </td>
            <td>
                <g:render template="deletePermission" model="[dataModel: dataModel,
                                                              username: userAndPermissionList.username,
                                                              permissionList: userAndPermissionList.permissionList.findAll { it == BasePermission.ADMINISTRATION }]"/>
            </td>

        </tr>
    </g:each>
    </tbody>
</table>
<g:form elementId="grantForm" action="grant" controller="dataModelPermission" method="post">
    <g:select name="username" from="${usernameList}"/>
    <g:select name="permission" from="${['administration', 'read']}"/>
    <g:hiddenField name="id" value="${dataModel.id}" />
    <g:actionSubmit name="grant" class="btn btn-default" value="${message(code: 'grant', default: 'Grant')}"/>
</g:form>
</div>
</body>
</html>
