<%@ page import="org.modelcatalogue.core.view.CatalogueElementViewModel; org.modelcatalogue.core.view.DataModelViewModel; org.modelcatalogue.core.util.MetadataDomain; org.modelcatalogue.core.dashboard.DashboardDropdown; org.modelcatalogue.core.util.PublishedStatus;" %>
<html>
<head>
    <title><g:message code="dashboard.title" default="Dashboard"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel panel-default">
    <div class="panel-heading">
        <g:form controller="dashboard" action="index" method="GET">
            <div>
                <div class="input-group">
                    <g:textField class="form-control" id="search" name="search" value="${search}" aria-label="..."/>
                    <div class="input-group-btn">
                        <button class="btn btn-default" id="filter-btn" type="submit" value="${g.message(code:'datamodel.filter', default: 'Filter')}"><i class="fas fa-search"></i></button>
                    </div>
                </div>
                <div class="input-group">
                    <g:select name="dataModelId" noSelection="${['null':'Select One...']}" from="${dataModelList}" optionKey="id" optionValue="name" value="${dataModelId}"/>
                    <g:select name="metadataDomain" from="${metadataDomainList}" value="${metadataDomain}"/>
                    <g:select name="status" from="${DashboardDropdown.values()}" value="${status}"/>
                    <input type="submit" id="filter-btn" class="btn btn-default" value="${g.message(code:'datamodel.filter', default: 'Filter')}" />
                </div><!-- /input-group -->

            </div><!-- /input-group -->
        </g:form>
    </div><!-- /.panel-heading -->
</div><!-- /.panel-default -->

<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />

<div class="panel-body">
<g:if test="${catalogueElementList}">
    <b><g:message code="pagination.total" default="Total: {0}" args="[total]"/></b>
    <div class="pagination">
        <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset,
                                                     metadataDomain: metadataDomain,
                                                     dataModelId: dataModelId,
                                                     status: status,
                                                     search: search,
                                                     order: sortQuery?.order,
                                                     sort: sortQuery?.sort,
                                                     offset: paginationQuery?.offset]}"/>
    </div>
    <g:if test="${catalogueElementList.first() instanceof DataModelViewModel}">
        <g:render template="dataModelViewTable"/>
    </g:if>
    <g:elseif test="${catalogueElementList.first() instanceof CatalogueElementViewModel}">
        <g:render template="catalogueElementViewTable"/>
    </g:elseif>
    <b><g:message code="pagination.total" default="Total: {0}" args="[total]"/></b>
    <div class="pagination">
        <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset,
                                                     metadataDomain: metadataDomain,
                                                     dataModelId: dataModelId,
                                                     status: status,
                                                     search: search,
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
