<table>
    <thead>
    <tr>
        <th><g:message code="dataModel.label" default="Data Model" /></th>

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="name" title="${message(code: 'dataElement.name.label', default: 'Name')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="modelCatalogueId" title="${message(code: 'dataElement.modelCatalogueId.label', default: 'Model Catalogue Id')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="status" title="${message(code: 'dataElement.status.label', default: 'Status')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="lastUpdated" title="${message(code: 'dataElement.lastUpdated.label', default: 'Last Updated')}" />
    </tr>
    </thead>
    <tbody>
    <g:each var="catalogueElement" in="${catalogueElementList}">
        <g:render template="dataElementViewRow" model="[catalogueElement: catalogueElement]"/>
    </g:each>
    </tbody>
</table>