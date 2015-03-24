<%@page import="org.modelcatalogue.core.DataElement"%>
<%@ page import="org.modelcatalogue.core.EnumeratedType" %>
<g:if test="${models}">
    <g:each status="j" in="${models}" var="model">
        <div id="${model.id}">
            <h${index+1}> <%=model.name%></h${index+1}>
        </div>
        <p>${model.description}</p>



        <g:if test="${model.contains}">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th width="100">Name</th>
                    <th width="200">Description</th>
                    <th width="100">Value Domain</th>
                    <th width="100">Multiplicity</th>
                    <th width="200">Same As</th>

                    %{--<th width="200">Datatype</th>--}%
                    %{--<th width="400">Allowed Valued</th>--}%
                    %{--<th width="30">Min Repeat</th>--}%
                    %{--<th width="30">Min Repeat</th>--}%
                </tr>
                </thead>
                <tbody>
                <g:each  in="${model.containsRelationships}" var="relationship" >
                  <g:if test="${relationship.destination instanceof DataElement }">
                    <%
                        if (relationship.destination.valueDomain) {
                            valueDomains << relationship.destination.valueDomain
                        }
                    %>
                    <tr>
                        <td width="100"><%=relationship.destination.name %> </br> (GE<%=relationship.destination.id %>)</td>
                        <td width="200"><%=relationship.destination.description %></td>
                        <td width="100"><a href="#${relationship.destination.valueDomain?.id}">${relationship.destination.valueDomain?.name}</a></td>
                        <td width="100">
                            <%=relationship.ext.get("Min Occurs")%>..<%=relationship.ext.get("Max Occurs") %>
                        </td>
                        <td width="200">
                            <g:each in="${relationship.destination.isSynonymFor}" var="synonym">
                                <g:if test="${synonym.classifications[0].name!="Genomics England Forms" && synonym.classifications[0].name!="Rare Diseases" && synonym.classifications[0].name!="Cancer"}">
                                    <%=synonym.name%>
                                    (<g:if test="${synonym.ext.get("Data Item No")}"><%= synonym.ext.get("Data Item No") %></g:if><g:else>GE<%= synonym.id %></g:else>) from <%=synonym.classifications[0].name%>

                                </g:if>
                            </g:each>
                        </td>
                        %{--<g:if test="${relationship.destination.valueDomain?.dataType}">--}%
                        %{--<td width="200"><%= relationship.destination.valueDomain.dataType.name %></td>--}%
                        %{--</g:if>--}%
                        %{--<g:else>--}%
                        %{--<td width="200">No Datatype defined</td>--}%
                        %{--</g:else>--}%

                        %{--<g:else>--}%
                        %{--<td width="400"><%=relationship.destination.valueDomain.dataType.description%></td>--}%
                        %{--</g:else>--}%
                        %{--<td width="30">${relationship.ext['Min Occurs']}</td>--}%
                        %{--<td width="30">${relationship.ext['Max Occurs']}</td>--}%
                    </tr>
             </g:if>
               </g:each>
                </tbody>
            </table>
         </g:if>
        <p>&nbsp;</p>
        <g:if test="${model?.parentOf}"> <g:render template="recModel" model="${[models: model.parentOf, index: index+1,valueDomains:valueDomains]}"/></g:if>
   </g:each>
</g:if>