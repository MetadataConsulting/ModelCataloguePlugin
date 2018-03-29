<%@ page import="org.modelcatalogue.core.mappingsuggestions.MatchAgainst; org.modelcatalogue.core.actions.ActionState" %>
<html>
<head>
    <title><g:message code="batch.title" default="Mapping Batches"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="container">
    <div class="page-header">
        <g:link class="btn btn-default pull-right" action="create" controller="batch"><g:message code="batch.create" default="Generate Mappings"/></g:link>
        <h1><g:message code="batch.title" default="Mapping Batches"/></h1>
    </div>

    <g:if test="${flash.message}">
        <div class="alert alert-info">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <p>${flash.message}</p>
        </div>
    </g:if>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <p>${flash.error}</p>
        </div>
    </g:if>

    <g:if test="${batchList}">

        <g:form controller="batch" method="POST" action="archive" >
            <table>
                <thead>
                <tr>
                    <th></th>
                    <th><g:message code="batch.lastUpdated" default="Last Updated"/></th>
                    <th><g:message code="batch.name" default="Name"/></th>
                    <th><g:message code="batch.actions.pending" default="Pending"/></th>
                    <th><g:message code="batch.actions.running" default="Running"/></th>
                    <th><g:message code="batch.actions.performed" default="Performed"/></th>
                    <th><g:message code="batch.actions.failed" default="Failed"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="batch" in="${batchList}">
                    <tr>
                        <td>
                            <g:checkBox name="batchIds" value="${batch.id}" checked="false"  />
                        </td>
                        <td>${batch.lastUpdated}</td>
                        <td>
                            <g:link controller="mappingSuggestions" action="index" params="[batchId: batch.id]">${batch.name}</g:link>
                        </td>
                        <td>${batch.actionStateCount[ActionState.PENDING] ?: 0}</td>
                        <td>${batch.actionStateCount[ActionState.PERFORMING] ?: 0}</td>
                        <td>${batch.actionStateCount[ActionState.PERFORMED] ?: 0}</td>
                        <td>${batch.actionStateCount[ActionState.FAILED] ?: 0}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div>
                <input type="submit" class="btn btn-default" value="${g.message(code:'batch.archive', default: 'Archive')}" />
            </div>
        </g:form>

    </g:if>

    <hr/>
    <p><small><g:message code="mappingsuggestions.matchAgainst" args="[matchAgainst == MatchAgainst.ELASTIC_SEARCH ? 'Elastic Search' : 'Database']" default="Mapping Suggestion configured to use {0}"/></small></p>

</div>
</body>
</html>