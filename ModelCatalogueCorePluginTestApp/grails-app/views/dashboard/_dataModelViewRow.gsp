<tr>
    <td>
        <g:render template="dataModelLink" model="[dataModel: catalogueElement]"/>
    </td>
    <td>${catalogueElement.semanticVersion}</td>
    <td>${catalogueElement.status}</td>
    <td>${catalogueElement.lastUpdated}</td>
    <td>
        <g:if test="${catalogueElement.assetsList}">
          <g:form controller="dataModel" action="showAssetInAngular" method="GET" id="${catalogueElement.id}">
            <div>
              <div class="input-group">
                <g:select name="subResourceId" from="${catalogueElement.assetsList}" optionKey="id" optionValue="name"/>
                <input type="submit" class="asset-link btn btn-default" value="${g.message(code:'asset.go.to', default: 'Go To Asset')}" />
              </div><!-- /input-group -->
            </div><!-- /input-group -->
          </g:form>
            <%--<ul>
                <g:each var="asset" in="${catalogueElement.assetsList}">
                    <li><a href="/#/${catalogueElement.id}/asset/${asset.id}">${asset.name}</a></li>
                </g:each>
            </ul> --%>
        </g:if>
    </td>
</tr>
