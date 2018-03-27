<table>
    <thead>
    <tr>
        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="name" title="${message(code: 'dataModel.name.label', default: 'Name')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="semanticVersion" title="${message(code: 'model.semanticVersion.label', default: 'Semantic Version')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="status" title="${message(code: 'dataModel.status.label', default: 'Status')}" />

        <g:sortableColumn params="${[metadataDomain: metadataDomain, status: status, search: search]}" property="lastUpdated" title="${message(code: 'dataModel.lastUpdated.label', default: 'Last Updated')}" />

        <th><g:message params="${[metadataDomain: metadataDomain, status: status, search: search]}" code="model.asset" default="Asset"/></th>

    </tr>
    </thead>
    <tbody>
    <g:each var="catalogueElement" in="${catalogueElementList}">
        <g:render template="dataModelViewRow" model="[catalogueElement: catalogueElement]"/>
    </g:each>
    </tbody>
</table>