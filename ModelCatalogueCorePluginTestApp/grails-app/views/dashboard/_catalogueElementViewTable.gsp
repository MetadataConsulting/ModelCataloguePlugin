<table>
    <thead>
    <tr>
        <th><g:message code="dataModel.label" default="Data Model" /></th>

        <g:sortableColumn params="${[max: paginationQuery?.offset,
                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                     metadataDomain: metadataDomain,
                                     keywordMatchType: keywordMatchType,
                                     dataModelId: dataModelId,
                                     status: status,
                                     search: search,
                                     order: sortQuery?.order,
                                     sort: sortQuery?.sort,
                                     offset: paginationQuery?.offset]}" property="name" title="${message(code: 'dataElement.name.label', default: 'Name')}" />

        <g:sortableColumn params="${[max: paginationQuery?.offset,
                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                     metadataDomain: metadataDomain,
                                     keywordMatchType: keywordMatchType,
                                     dataModelId: dataModelId,
                                     status: status,
                                     search: search,
                                     order: sortQuery?.order,
                                     sort: sortQuery?.sort,
                                     offset: paginationQuery?.offset]}" property="modelCatalogueId" title="${message(code: 'dataElement.modelCatalogueId.label', default: 'Model Catalogue Id')}" />

        <g:sortableColumn params="${[max: paginationQuery?.offset,
                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                     metadataDomain: metadataDomain,
                                     keywordMatchType: keywordMatchType,
                                     dataModelId: dataModelId,
                                     status: status,
                                     search: search,
                                     order: sortQuery?.order,
                                     sort: sortQuery?.sort,
                                     offset: paginationQuery?.offset]}" property="status" title="${message(code: 'dataElement.status.label', default: 'Status')}" />

        <g:sortableColumn params="${[max: paginationQuery?.offset,
                                     searchCatalogueElementScopes: searchCatalogueElementScopes,
                                     metadataDomain: metadataDomain,
                                     keywordMatchType: keywordMatchType,
                                     dataModelId: dataModelId,
                                     status: status,
                                     search: search,
                                     order: sortQuery?.order,
                                     sort: sortQuery?.sort,
                                     offset: paginationQuery?.offset]}" property="lastUpdated" title="${message(code: 'dataElement.lastUpdated.label', default: 'Last Updated')}" />
    </tr>
    </thead>
    <tbody>
    <g:each var="catalogueElement" in="${catalogueElementList}">
        <g:render template="catalogueElementViewRow" model="[catalogueElement: catalogueElement, serverUrl: serverUrl]"/>
    </g:each>
    </tbody>
</table>
