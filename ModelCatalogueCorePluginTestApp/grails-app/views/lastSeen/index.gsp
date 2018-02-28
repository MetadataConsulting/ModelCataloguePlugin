<html>
<head>
    <title><g:message code="user.activity" default="User Activity"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="user.activity" default="User Activity"/></h1>
    </div>

    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />

    <g:if test="${userAuthenticationList}">
        <b><g:message code="pagination.total" default="Total: {0}" args="[total]"/></b>
        <div class="pagination">
            <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset,
                                                         order: sortQuery?.order,
                                                         sort: sortQuery?.sort,
                                                         offset: paginationQuery?.offset]}"/>
        </div>
        <table>
            <thead>
                <tr>
                    <th><g:message code="userAuthentication.username" default="Username"/></th>
                    <th><g:message code="userAuthentication.authenticationDate" default="Authentication Date"/></th>
                </tr>
            </thead>
            <tbody>
                <g:each var="userAuthentication" in="${userAuthenticationList}">
                    <tr><td>${userAuthentication.username}</td><td>${userAuthentication.authenticationDate}</td></tr>
                </g:each>
            </tbody>
        </table>

        <b><g:message code="pagination.total" default="Total: {0}" args="[total]"/></b>
        <div class="pagination">
            <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset,
                                                         order: sortQuery?.order,
                                                         sort: sortQuery?.sort,
                                                         offset: paginationQuery?.offset]}"/>
        </div>
    </g:if>
    <g:else>
        <h1><g:message code="results.notFound" default="No results found"/></h1>
    </g:else>
</div><!-- /.panel-body -->
</body>
</html>
