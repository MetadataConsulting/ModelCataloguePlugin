*<%@ page import="org.modelcatalogue.core.EnumeratedType" %>

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
        <g:each  in="${dataElements.get(model.id)}" var="relationship" >
            <tr >
                <td width="200">${relationship.destination.name}</td>
                <td width="400">${relationship.destination.description}</td>
                <td width="400">${relationship.destination.id}</td>
            </tr>
  </g:each>
        </tbody>
    </table>


  <g:each  in="${dataElements.get(model.id)}" var="relationship" >
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title text-center"><span class="badge pull-left">${relationship?.destination?.id}</span>${relationship?.destination?.name} <small><span class="badge pull-right">v${model.versionNumber}</span><span class="label label-info pull-right">${model.status}</span></small></h3>
            </div>
            <div class="panel-body">
                <p>${relationship.destination.description}</p>
                <g:if test="${relationship?.destination?.valueDomain}">
                    <div class="panel panel-default">
                        <g:each in="${relationship.destination?.valueDomain?.classifications}" var="cls">
                            <span class="pull-right badge">${cls.name}</span>
                        </g:each>
                        <p><strong>${relationship?.destination?.valueDomain?.name}</strong>
                            <g:if test="${relationship?.destination?.valueDomain?.description!=relationship?.destination?.description}">
                                - ${relationship?.destination?.valueDomain?.description}
                            </g:if>
                        </p>
                        <g:if test="${relationship.destination?.valueDomain?.dataType instanceof EnumeratedType}">
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
                </g:if>
            </div>
        </div>
</g:each>
