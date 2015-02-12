<%@ page import="org.modelcatalogue.core.EnumeratedType" %>
<g:if test="${models}">
    <ul style="list-style-type:none" class="list-group">
    <g:each status="j" in="${models}" var="model">
        <li class="list-item">
            <h4>${(index)}.${(j+1)}  ${model.name} <span class="badge">${model.id}</span><small><span class="badge pull-right">v${model.versionNumber}</span><span class="label label-info pull-right">${model.status}</span></small></h3></h4>
            <p>${model.description}</p>

            <g:if test="${model.contains}">
            <h4>Data Elements</h4>
            <table class="table table-striped">
            <thead>
            <tr>
            <th width="200">Name</th>
            <th width="400">Description</th>
            <th width="400">Model Catalogue Reference</th>
            %{--<th width="200">Datatype</th>--}%
            %{--<th width="400">Allowed Valued</th>--}%
            %{--<th width="30">Min Repeat</th>--}%
            %{--<th width="30">Min Repeat</th>--}%
            </tr>
            </thead>
            <tbody>
            <mc:relationships element="${model}" var="relationship" direction="outgoing" type="containment">
            <tr >
            <td width="200">${relationship.destination.name}</td>
            <td width="400">${relationship.destination.description}</td>
            <td width="400">${relationship.destination.id}</td>
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
            </tr>
            </mc:relationships>
            </tbody>
            </table>
            </g:if>

            <g:if test="${model.contains}">
                    <mc:relationships element="${model}" var="relationship" direction="outgoing" type="containment">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title text-center"><span class="badge pull-left">${relationship?.destination?.id}</span>${relationship?.destination?.name} <small><span class="badge pull-right">v${model.versionNumber}</span><span class="label label-info pull-right">${model.status}</span></small></h3>
                            </div>
                            <div class="panel-body">
                                <p>${relationship.destination.description}</p>
                                <div class="panel panel-default">
                                    <g:each in="${relationship.destination?.valueDomain?.classifications}" var="cls">
                                        <span class="pull-right badge">${cls.name}</span>
                                    </g:each>
                                <p><strong>${relationship?.destination?.valueDomain?.name}</strong>
                                <g:if test="${relationship?.destination?.valueDomain?.description!=relationship?.destination?.description}">
                                     - ${relationship?.destination?.valueDomain?.description}
                                </g:if>
                                </p>
                                <g:if test="${relationship.destination?.valueDomain.dataType instanceof EnumeratedType}">
                                 %{--<p>${relationship.destination.valueDomain.dataType.prettyPrint()}</p>--}%
                                    <table class="table table-striped">
                                        <thead>
                                        <tr>
                                        <th width="200">Code</th>
                                        <th width="200">Description</th>
                                        </tr>
                                        </thead>
                                        <g:each in="${relationship?.destination?.valueDomain?.dataType?.enumerations}" var="key, value">

                                        <tr><td>${key}</td><td>${value}</td></tr>


                                    </g:each>
                                    </table>
                                </g:if>
                                <g:else>

                                    <g:if test="${relationship.destination?.valueDomain?.dataType?.name}">(${relationship.destination?.valueDomain?.dataType?.name})</g:if>
                                    <g:if test="${relationship.destination?.valueDomain?.dataType?.description}">(${relationship.destination?.valueDomain?.dataType?.description})</g:if>
                                    <g:if test="${relationship.destination?.valueDomain?.rule}"><p> Format: <code>${relationship.destination?.valueDomain?.rule}</code></p></g:if>
                                    <g:if test="${relationship.destination?.valueDomain?.unitOfMeasure?.name}">(${relationship.destination?.valueDomain?.unitOfMeasure?.name})</g:if>
                                    <g:if test="${relationship.destination?.valueDomain?.unitOfMeasure?.symbol}">(${relationship.destination?.valueDomain?.unitOfMeasure?.symbol})</g:if>
                                </g:else>

                        %{--<g:if test="${relationship.destination?.valueDomain?.dataType?.enumerations}">--}%
                            %{--<g:each in="${relationship.destination.valueDomain.dataType?.enumerations}" var="en">--}%
                                %{--<p>${en}</p>--}%
                             %{--</g:each>--}%
                        %{--</g:if>--}%
                                    </div>
                            </div>
                        </div>
                    </mc:relationships>
            </g:if>



        <g:if test="${model?.parentOf}"> <g:render template="recModel" model="${[models: model.parentOf, index: (index + "." + (j+1))]}"/></g:if>
        </li>
    </g:each>
    </ul>
</g:if>