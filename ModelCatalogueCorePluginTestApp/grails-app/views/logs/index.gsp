<html>
<head>
    <title><g:message code="logs.title" default="Logs"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="logs.title" default="Logs"/></h1>
    </div>
    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />
    <h2><g:message code="logs.create.title" default="Do you want to create logs archive?"/></h2>
    <form action="/api/modelCatalogue/core/logs" method="get">
        <p><g:message code="logs.create.legend" default="New asset containing the application logs will be created and accessible to all users."/></p>
        <input type="submit" value="${g.message(code: 'logs.create', default: 'Create Log Archive')}" class="btn btn-default"/>
    </form>
</div><!-- /.panel-body -->
</body>
</html>
