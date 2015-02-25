<%@ page import="org.modelcatalogue.core.EnumeratedType" %>
<g:if test="${models}">
    <ul style="list-style-type:none" class="list-group">
    <g:each status="j" in="${models}" var="model">
        <li class="list-item">
            <h4>${(index)}.${(j+1)}  ${model.name} <span class="badge">${model.id}</span><small><span class="badge pull-right">v${model.versionNumber}</span><span class="label label-info pull-right">${model.status}</span></small></h3></h4>
            <p>${model.description}</p>
        <g:if test="${model?.contains}"><g:render template="recDataElements" model="${[model: model]}" /></g:if>
        <g:if test="${model?.parentOf}"> <g:render template="recModel" model="${[models: model.parentOf, index: (index + "." + (j+1))]}"/></g:if>
        </li>
    </g:each>
    </ul>
</g:if>


%{--<g:if test="${relationship.destination.valueDomain?.dataType}">--}%
%{--<td width="200">${relationship.destination.valueDomain.dataType.name}</td>--}%
%{--</g:if>--}%
%{--<g:else>--}%
%{--<td width="200">No Datatype defined</td>--}%
%{--</g:else>--}%
%{--<g:if test="${relationship.destination.valueDomain.dataType instanceof EnumeratedType}">--}%
%{--<td width="400">${relationship.destination.valueDomain.dataType.prettyPrint()}</td>--}%
%{--</g:if>--}%
%{--<g:else>--}%
%{--<td width="400">${relationship.destination.valueDomain.dataType.description}</td>--}%
%{--</g:else>--}%
%{--<td width="30">${relationship.ext['Min Occurs']}</td>--}%
%{--<td width="30">${relationship.ext['Max Occurs']}</td>--}%