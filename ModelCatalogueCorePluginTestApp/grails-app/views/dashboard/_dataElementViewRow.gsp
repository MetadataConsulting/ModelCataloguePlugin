<tr>
    <td>
        <g:render template="dataModelLink" model="[dataModel: catalogueElement.dataModel]"/>
    </td>
    <td>
        <a href="/#/${catalogueElement.dataModel.id}/dataElement/${catalogueElement.id}">${catalogueElement.name}</a>
    </td>
    <td>${catalogueElement.modelCatalogueId}</td>
    <td>${catalogueElement.status}</td>
    <td>${catalogueElement.lastUpdated}</td>
</tr>