<%@ page import="org.modelcatalogue.core.search.KeywordMatchType; org.modelcatalogue.core.dashboard.SearchCatalogueElementScope; org.modelcatalogue.core.dashboard.SearchScope; org.modelcatalogue.core.dashboard.DashboardStatusDropdown; org.modelcatalogue.core.view.CatalogueElementViewModel; org.modelcatalogue.core.view.DataModelViewModel; org.modelcatalogue.core.util.MetadataDomain; org.modelcatalogue.core.util.PublishedStatus;" %>
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
                        <button class="btn btn-default" id="search-btn" type="submit" value="${g.message(code:'datamodel.filter', default: 'Filter')}"><i class="fas fa-search"></i></button>
                    </div>
                </div>
                <div class="input-group">
                    <g:select name="keywordMatchType" from="${KeywordMatchType.values()}" value="${keywordMatchType}" optionValue="${{
                        it.name().split('_').join(' ')
                    }}"/>
                    <g:select onChange="onDataModelIdChange();" name="dataModelId" noSelection="${['null':'Select One...']}" from="${dataModelList}" optionKey="id" optionValue="name" value="${dataModelId}"/>
                    <g:select name="metadataDomain" from="${metadataDomainList}" value="${metadataDomain}" optionValue="${{
                        mdx.metadataMessage(metadataDomain: it)
                    }}"/>
                    <g:select name="status" from="${DashboardStatusDropdown.values()}" value="${status}"/>
                    <g:select name="searchScope" from="${SearchScope.values()}" value="${searchScope}" optionValue="${{
                        if ( it == SearchScope.DATAMODEL ) {
                            return "Search within Data Model"
                        } else if ( it == SearchScope.DATAMODEL_AND_IMPORTS ) {
                            return "Search within Data Model and Imports"
                        }
                    }}"/>

                    <br/><b>For every element search: </b>
                    <span style="margin-right: 15px;">
                        <g:message code="searchCatalogueElementScope.all" default="Everything"/>
                        <g:checkBox name="searchCatalogueElementScopes"
                                           value="${SearchCatalogueElementScope.ALL}"
                                           checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.ALL)}"/>
                    </span>
                    <span style="margin-right: 15px;">
                    <g:message code="searchCatalogueElementScope.name" default="Name"/> <g:checkBox name="searchCatalogueElementScopes"
                                     value="${SearchCatalogueElementScope.NAME}"
                                     checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.NAME)}"/>
                    </span>
                    <span style="margin-right: 15px;">
                    <g:message code="searchCatalogueElementScope.modelCatalogueId" default="Model Catalogue ID"/> <g:checkBox name="searchCatalogueElementScopes"
                                                   value="${SearchCatalogueElementScope.MODELCATALOGUEID}"
                                                   checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.MODELCATALOGUEID)}"/>
                    </span>
                    <span style="margin-right: 15px;">
                    <g:message code="searchCatalogueElementScope.description" default="Description"/> <g:checkBox name="searchCatalogueElementScopes"
                                            value="${SearchCatalogueElementScope.DESCRIPTION}"
                                            checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.DESCRIPTION)}"/>
                    </span>
                    <span style="margin-right: 15px;">
                    <g:message code="searchCatalogueElementScope.extensionName" default="Extension Name"/> <g:checkBox name="searchCatalogueElementScopes"
                                               value="${SearchCatalogueElementScope.EXTENSIONNAME}"
                                               checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.EXTENSIONNAME)}"/>
                    </span>
                    <span style="margin-right: 15px;">
                    <g:message code="searchCatalogueElementScope.extensionValue" default="Extension Value"/> <g:checkBox name="searchCatalogueElementScopes"
                                                value="${SearchCatalogueElementScope.EXTENSIONVALUE}"
                                                checked="${searchCatalogueElementScopes.contains(SearchCatalogueElementScope.EXTENSIONVALUE)}"/>
                    </span>
<g:javascript>
refreshFormView();
function onDataModelIdChange() {
    refreshFormView();
}
function htmlOption(name, value, selected) {
    if ( selected ) {
        return '<option value="'+value+'" selected="selected">'+name+'</option>';
    }
    return '<option value="'+value+'">'+name+'</option>';
}
function removeOption(selectId, optionName, optionValue) {
    var html = document.getElementById(selectId).innerHTML;
    var selectedOption = htmlOption(optionName, optionValue, true);
    var option = htmlOption(optionName, optionValue, false);
    if ( html.indexOf(selectedOption) != -1 ) {
        document.getElementById(selectId).innerHTML = html.replace(selectedOption, '');
    }
    if ( html.indexOf(option) != -1 ) {
        document.getElementById(selectId).innerHTML = html.replace(option, '');
    }
}

function containsOption(selectId, optionName, optionValue) {
    var html = document.getElementById(selectId).innerHTML;
    var selectedOption = htmlOption(optionName, optionValue, true);
    var option = htmlOption(optionName, optionValue, false);
    return( html.indexOf(selectedOption) != -1 || html.indexOf(option) != -1 );
}

function refreshFormView() {
    var elId = 'dataModelId';
    var metadataId = 'metadataDomain';
    var dataModelsName = 'Data Models';
    var dataModelsValue = 'DATA_MODEL';
    var dataModelId = getSelectValue(elId);
    if ( dataModelId === 'null') {
        hide('searchScope');
        if ( !containsOption(metadataId, dataModelsName, dataModelsValue) ) {
            var html = htmlOption(dataModelsName, dataModelsValue, true) + document.getElementById(metadataId).innerHTML;
            document.getElementById(metadataId).innerHTML = html;
        }
    } else {
        removeOption(metadataId, dataModelsName, dataModelsValue);
        show('searchScope');
    }
}
function hide(id) {
    document.getElementById(id).style.visibility = "hidden";
}
function show(id) {
    document.getElementById(id).style.visibility = "visible";
}
function getSelectValue(selectId) {
    return document.getElementById(selectId).value;
}
</g:javascript>
                </div><!-- /input-group -->

            </div><!-- /input-group -->
        </g:form>
    </div><!-- /.panel-heading -->
</div><!-- /.panel-default -->
<p>Time taken for search: ${timeTaken}</p>

<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />

<div class="panel-body">
<g:if test="${catalogueElementList}">
    <b><g:message code="pagination.total" default="Total: {0}" args="[total]"/></b>
    <div class="pagination">
        <g:paginate total="${total ?: 0}" params="${[max: paginationQuery?.offset,
                                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                                     metadataDomain: metadataDomain,
                                                     keywordMatchType: keywordMatchType,
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
                                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                                     metadataDomain: metadataDomain,
                                                     keywordMatchType: keywordMatchType,
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
        <% if(mdx.metadataMessage(metadataDomain: metadataDomain) != mdx.metadataMessage(metadataDomain: MetadataDomain.CATALOGUE_ELEMENT)) { %>
        <div class="alert alert-warning">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <p><g:message code="search.restricted.metadatadomain"
                          default="Note: Search is restricted to {0}"
                          args="[mdx.metadataMessage(metadataDomain: metadataDomain)]"/></p>
        </div>
        <% } %>
    </g:else>
</div><!-- /.panel-body -->
</body>
</html>
