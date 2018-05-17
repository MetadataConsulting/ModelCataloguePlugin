<g:each var="permission" in="${permissionList}">
    <sec:permissionAsString permission="${permission}"/>

        <g:form action="revoke" controller="dataModelPermission" method="post">
            <g:hiddenField name="username" value="${username}" />
            <g:hiddenField name="id" value="${dataModel.id}" />
            <g:hiddenField name="permission" value="${sec.permissionAsString(permission: permission).toLowerCase()}" />
            <g:actionSubmit name="revoke" action="revoke" class="btn btn-danger" value="${message(code: 'delete', default: 'Delete')}"/>
        </g:form>
</g:each>