<tr>
    <td>
        <g:render template="dataModelLink" model="[dataModel: catalogueElement]"/>
    </td>
    <td>${catalogueElement.semanticVersion}</td>
    <td>${catalogueElement.status}</td>
    <td>${catalogueElement.lastUpdated}</td>
    <td>
        <g:if test="${catalogueElement.assetsList}">
            <ul>
                <g:each var="asset" in="${catalogueElement.assetsList}">
                    <li><a href="/#/${catalogueElement.id}/asset/${asset.id}">${asset.name}</a></li>
                </g:each>
            </ul>
        </g:if>
    </td>
</tr>