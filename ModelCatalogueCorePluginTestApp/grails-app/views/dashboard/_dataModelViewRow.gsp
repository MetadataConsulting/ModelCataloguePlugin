<tr>
    <td>
        <g:render template="dataModelLink" model="[dataModel: catalogueElement]"/>
    </td>
    <td>${catalogueElement.semanticVersion}</td>
    <td>${catalogueElement.status}</td>
    <td>${catalogueElement.lastUpdated}</td>
    <td>
        <g:if test="${catalogueElement.assetsList}">

            <g:each var="asset" in="${catalogueElement.assetsList}">
                <a href="/#/${catalogueElement.id}/asset/${asset.id}">${asset.name}</a> &nbsp; &nbsp; <a href= "/api/modelCatalogue/core/asset/${asset.id}/download?force=true" target="_blank"><i class="fas fa-download"></i></a> </br>
            </g:each>


        </g:if>
    </td>
</tr>
