<%@ page import="org.modelcatalogue.core.dashboard.DashboardDropdown; org.modelcatalogue.core.util.PublishedStatus; org.modelcatalogue.core.DataModel" %>
<html>
<head>
    <title><g:message code="dashboard.title" default="Data Models"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel panel-default">
<div class="panel-heading">
    <g:form controller="dashboard" action="index" method="GET">
        <div>
            <div class="input-group">
                <g:textField class="form-control" name="search" value="${search}" aria-label="..."/>
                <div class="input-group-btn">
                    <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${status} <span class="caret"></span></button>
                    <ul class="dropdown-menu dropdown-menu-right">
                        <li><input type="submit" class="form-control btn btn-link" name="status" value="${DashboardDropdown.ACTIVE}"></li>
                        <li role="separator" class="divider"></li>
                        <li><input type="submit" class="form-control btn btn-link" name="status" value="${DashboardDropdown.FINALIZED}"></li>
                        <li><input type="submit" class="form-control btn btn-link" name="status" value="${DashboardDropdown.DRAFT}"></li>
                        <li role="separator" class="divider"></li>
                        <li><input type="submit" class="form-control btn btn-link" name="status" value="${DashboardDropdown.DEPRECATED}"></li>
                    </ul>
                </div><!-- /btn-group -->

            </div><!-- /input-group -->
            <input type="submit" class="btn btn-default" value="${g.message(code:'datamodel.filter', default: 'Filter')}" />
        </div><!-- /input-group -->
        </div>
    </g:form>
</div>

<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />

<div class="panel-body">
<g:if test="${models}">
    <table>
        <thead>
        <tr>

            <g:sortableColumn params="${[status: status, search: search]}" property="name" title="${message(code: 'dataModel.name.label', default: 'Name')}" />

            <g:sortableColumn params="${[status: status, search: search]}" property="semanticVersion" title="${message(code: 'model.semanticVersion.label', default: 'Semantic Version')}" />

            <g:sortableColumn params="${[status: status, search: search]}" property="status" title="${message(code: 'dataModel.status.label', default: 'Status')}" />

            <g:sortableColumn params="${[status: status, search: search]}" property="lastUpdated" title="${message(code: 'dataModel.lastUpdated.label', default: 'Last Updated')}" />

            <th><g:message params="${[status: status, search: search]}" code="model.asset" default="Asset"/></th>

        </tr>
        </thead>
        <tbody>
        <g:each var="model" in="${models}">
            <tr>
                <td>
                    <a href="/#/${model.id}/dataModel/${model.id}">${model.name}</a>
                </td>
                <td>${model.semanticVersion}</td>
                <td>${model.status}</td>
                <td>${model.lastUpdated}</td>
                <td>
                    <g:if test="${model.assets}">
                        <table>
                            <g:each var="asset" in="${model.assets}">
                                <g:if test="${asset.publishedStatus == PublishedStatus.PUBLISHED}">
                                    <tr>
                                        <td>
                                            <a href="/#/${model.id}/asset/${asset.id}">${asset.name}</a>
                                        </td>
                                    </tr>
                                </g:if>
                            </g:each>
                        </table>
                    </g:if>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

    <div class="pagination">
        <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset, status: status, search: search, order: sortQuery?.order, sort: sortQuery?.sort, offset: paginationQuery?.offset]}"/>

    </div>
    </g:if>
    <g:else>
        <h1><g:message code="datamodel.notFound" default="Data Models not found"/></h1>
    </g:else>
</div>

</body>
</html>
