<%@ page import="org.modelcatalogue.core.util.MetadataDomainEntity" %>
<tr>
    <td>
        <g:render template="dataModelLink" model="[dataModel: catalogueElement.dataModel]"/>
    </td>
    <td>
        <a href="${MetadataDomainEntity.link(catalogueElement.dataModel.id as Long, new MetadataDomainEntity(id: catalogueElement.id, domain: catalogueElement.domain), serverUrl as String)}">${catalogueElement.name}</a>
    </td>
    <td>${catalogueElement.modelCatalogueId}</td>
    <td>${catalogueElement.status}</td>
    <td>${catalogueElement.lastUpdated}</td>
</tr>