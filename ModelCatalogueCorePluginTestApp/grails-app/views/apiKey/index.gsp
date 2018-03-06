<html>
<head>
    <title><g:message code="user.apiKey" default="Api Key"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="panel panel-default">
        <div class="panel-heading">
            <g:form controller="apiKey" action="reset">
                <input id="regenerateKey" type="submit" value="Regenerate API Key"/>
            </g:form>
        </div><!-- /.panel-heading -->
    </div><!-- /.panel-default -->
    <div class="page-header">
        <h1><g:message code="user.apiKey" default="Api Key"/></h1>
    </div>


    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />

    <h2 id="apiKey">${apiKey}</h2>

</div><!-- /.panel-body -->
</body>
</html>
