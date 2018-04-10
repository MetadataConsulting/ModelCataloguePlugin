<html>
<head>
    <title><g:message code="reindexCatalogue.title" default="Reindex Catalogue"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="reindexCatalogue.title" default="Reindex Catalogue"/></h1>
    </div>

    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />

    <h2>Do you want to reindex catalogue?</h2>
    <form action="/api/modelCatalogue/core/search/reindex" method="post">
        <p>Whole catalogue will be reindexed. This may take a long time and it can have negative impact on the performance.</p>
        <input type="submit" value="Reindex catalogue" class="btn btn-default"/>
         <g:hiddenField name="soft" value="true" />
    </form>

</div><!-- /.panel-body -->
</body>
</html>
