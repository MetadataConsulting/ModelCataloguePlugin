<%@ page import="org.modelcatalogue.core.actions.ActionState;org.modelcatalogue.core.util.MetadataDomainEntity" %>
<html>
<head>
    <title><g:message code="mappingsuggestions.title" default="Mapping Suggestions"/></title>
    <meta name="layout" content="main" />
    <asset:javascript src="mdx.js"/>
</head>
<body>

<div class="container">
    <div class="page-header">
        <h1><g:link controller="batch" action="all"><g:message code="batch.title" default="Mapping Batches"/></g:link> &rarr; <g:message code="batch.title" default="Mapping"/></h1>
    </div>
<g:form controller="mappingSuggestions" method="GET" action="index">
    <g:hiddenField name="batchId" value="${batchId}"/>
    <ol class="filter">
        <li>
            <g:each var="state" in="${ActionState.values()}">
                <label>
                    <g:render template="actionState" model="[actionState: state]"/>
                </label>

                <g:checkBox name="state"
                            value="${state}"
                            checked="${filter.stateList.contains(state)}"  />
            </g:each>
        </li>
        <li>
            <label><g:message code="mappingsuggestions.max" default="Results per page"/></label>
            <g:select name="max" from="[5, 10, 20, 50, 100, 200]" value="${filter.max}"/>
        </li>
        <li>
            <input type="hidden" name="score" value="0"/>
            %{--<label><g:message code="mappingsuggestions.score" default="Score higher than"/></label>--}%
            %{--<g:select name="score" from="${0..100}" value="${filter.score}"/>--}%
        </li>
        <li>
            <input type="hidden" name="term"/>
            %{--<label><g:message code="mappingsuggestions.term" default="code or name contains"/></label>--}%
            %{--<g:textField name="term" value="${filter.term}"/>--}%
        </li>
        <li>
            <input type="submit" class="btn btn-default" value="${g.message(code:'mappingsuggestions.filter', default: 'Filter')}" />
        </li>
    </ol>
    </g:form>
    <g:form controller="mappingSuggestions">
        <g:hiddenField name="batchId" value="${batchId}"/>
        <table>
        <thead>
        <tr>
        <tr>
            <th rowspan="2"><g:render template="selectAllMappingSuggestionIds"/></th>
            <th colspan="2" class="align-center">${sourceName}</th>
            <th colspan="2" class="align-center">${destinationName}</th>
            <th rowspan="2"><g:message code="mappingsuggestions.score" default="Score"/></th>
            <th rowspan="2"><g:message code="mappingsuggestions.association" default="Association"/></th>
        </tr>
        <tr>
            <th><g:message code="mappingsuggestions.id" default="ID"/></th>
            <th><g:message code="mappingsuggestions.name" default="Name"/></th>
            <th><g:message code="mappingsuggestions.id" default="ID"/></th>
            <th><g:message code="mappingsuggestions.name" default="Name"/></th>
        </tr>
        </thead>
        <tbody>
        <g:each var="mappingSuggestionInstance" in="${mappingSuggestionList}">
            <tr>
                <td><g:checkBox name="mappingSuggestionIds" value="${mappingSuggestionInstance.mappingSuggestionId}" checked="false"  /></td>
                <td>${mappingSuggestionInstance.source.code}</td>
                <td>
                    <g:if test="${mappingSuggestionInstance.source}">
                        <a class="target"
                           data-api="${MetadataDomainEntity.linkAjax(destinationId, mappingSuggestionInstance.source.metadataDomainEntity)}"
                           href="${MetadataDomainEntity.link(sourceId, mappingSuggestionInstance.source.metadataDomainEntity)}">${mappingSuggestionInstance.source.name}</a>
                    </g:if>
                </td>
                <td><g:if test="${mappingSuggestionInstance.destination}">${mappingSuggestionInstance.destination.code}</g:if></td>
                <td>
                    <g:if test="${mappingSuggestionInstance.destination}">
                        <a class="target"
                           data-api="${MetadataDomainEntity.linkAjax(destinationId, mappingSuggestionInstance.destination.metadataDomainEntity)}"
                           href="${MetadataDomainEntity.link(destinationId, mappingSuggestionInstance.destination.metadataDomainEntity)}">${mappingSuggestionInstance.destination.name}</a>
                    </g:if>
                </td>
                <td>${mappingSuggestionInstance.score}</td>
                <td><g:render template="actionState" model="[actionState: mappingSuggestionInstance.state]"/></td>
            </tr>
        </g:each>
        </tbody>
    </table>
        <div>
            <g:actionSubmit class="btn btn-default" value="${g.message(code:'mappingsuggestions.approve', default: 'Approve')}" action="approve" />
            <g:actionSubmit class="btn btn-default" value="${g.message(code:'mappingsuggestions.reject', default: 'Reject')}" action="reject" />
        </div>
</g:form>

    <div class="pagination">
        <g:paginate controller="mappingSuggestions"
                    action="index"
                    total="${pagination.total}"
                    offset="${pagination.offset}"
                    max="${pagination.max}"
                    params="[batchId: batchId, state: filter.stateList]"
        />
        <g:message code="pagination.legend" args="[pagination.offset, Math.min(pagination.offset + pagination.max, pagination.total), pagination.total]" default="Displaying ${pagination.offset}-${Math.min(pagination.offset + pagination.max, pagination.total)} of ${pagination.total}"/>
    </div>
</div>
</body>
</html>
