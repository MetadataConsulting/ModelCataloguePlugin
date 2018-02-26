<html>
<head>
    <title><g:message code="user.apiKey" default="Api Key"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel panel-default">
    <div class="panel-heading">
        <g:form controller="apiKey" action="reset">
            <input type="submit" value="Regenerate API Key"/>
        </g:form>
    </div><!-- /.panel-heading -->
</div><!-- /.panel-default -->

<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />

<div class="panel-body">
    <h1><g:message code="user.apiKey" default="Api Key"/>: ${apiKey}</h1>
</div><!-- /.panel-body -->
</body>
</html>
