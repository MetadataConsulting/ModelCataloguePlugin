<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 26/11/14
  Time: 16:37
--%>

<%@ page import="org.modelcatalogue.core.EnumeratedType; org.modelcatalogue.core.DataType; org.modelcatalogue.core.MeasurementUnit; org.modelcatalogue.core.ValueDomain; org.modelcatalogue.core.DataElement; org.modelcatalogue.core.Model; grails.util.Environment;grails.util.GrailsNameUtils" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>Classification Summary Report</title>
    <g:if test="${Environment.current in [Environment.PRODUCTION, Environment.TEST, Environment.CUSTOM]}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">

        <!-- code -->
    %{--<asset:stylesheet href="modelcatalogue.css"/>--}%
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        <asset:stylesheet href="modelcatalogue.css"/>
    %{--<asset:stylesheet href="modelcatalogue.css"/>--}%
    </g:else>

</head>

<body>
<div>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <h3>Genomics England: Clinical Dataset</h3>

            </br>
            </br>
            </br>

                <p><span class="label label-info pull-right">${classification.status}</span>  </p>
                <h1 class="text-center">${classification.name} <span class="badge">v${classification.versionNumber}</span></h1>

                <p>${classification.description}</p>
            </br>
            </br>
            </br>
                <h2>Contents:</h2>

                <h3>1. Essential Sample Metadata</h3>
                <h3>2. Core Data</h3>
                <h3>3. Additional Data</h3>
            </br>
            </br>
            </br>
            </br>
            </br>
            </br>
            </br>
            </br>
            </br>

            </br>
            </br></br>
            </br>
            </br>

            </br>
            </br></br>
            </br>
            </br>

            </br>
            </br></br>
            </br>
            </br>

            </br>
            </br></br>
            </br>
            </br>

            </br>
            </br>
            </br>
            </br>
            </br>
            </br>




                <g:each status="i" in="${classification.classifies.findAll{it in Model}}" var="model">
                    <g:if test="!${model.childOf}">

                    <h3>${i+1}. ${model.name}  <small><span class="badge">${model.id}</span><span class="badge pull-right">v${model.versionNumber}</span> <span class="label label-info pull-right">${model.status}</span></small></h3>
                    <p>${model.description}</p>
                    <g:render template="recModel" model="${[models: model.parentOf, index:i+1]}" />
                    </g:if>
                %{--<div class="table-responsive">--}%
                        %{--<table class="table table-striped">--}%
                        %{--<thead>--}%
                        %{--<tr>--}%
                            %{--<td class="col-md-8">Version</td>--}%
                            %{--<td class="col-md-12">${it.versionNumber}</td>--}%
                        %{--</tr>--}%
                        %{--<tr>--}%
                            %{--<td class="col-md-8">Last Updated</td>--}%
                            %{--<td class="col-md-12"><g:formatDate date="${it.lastUpdated}" style="medium"/></td>--}%
                        %{--</tr>--}%
                        %{--<!-- proper changelog -->--}%
                        %{--</thead>--}%
                    %{--</table>--}%
                    %{--</div>--}%

                    %{--<g:if test="${it.contains}">--}%
                        %{--<h4>Contained Data Elements</h4>--}%
                        %{--<table class="table table-striped">--}%
                            %{--<thead>--}%
                            %{--<tr>--}%
                                %{--<th width="200">Name</th>--}%
                                %{--<th width="400">Description</th>--}%
                                %{--<th width="200">Datatype</th>--}%
                                %{--<th width="400">Allowed Valued</th>--}%
                                %{--<th width="30">Min Repeat</th>--}%
                                %{--<th width="30">Min Repeat</th>--}%
                            %{--</tr>--}%
                            %{--</thead>--}%
                            %{--<tbody>--}%
                            %{--<mc:relationships element="${it}" var="relationship" direction="outgoing" type="containment">--}%
                                %{--<tr >--}%
                                    %{--<td width="200">${relationship.destination.name}</td>--}%
                                    %{--<td width="400">${relationship.destination.description}</td>--}%
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
                                %{--</tr>--}%
                            %{--</mc:relationships>--}%
                            %{--</tbody>--}%
                        %{--</table>--}%
                    %{--</g:if>--}%
                </g:each>

            </div>
        </div>
    </div>
</div>
%{--<script type="application/javascript">--}%
%{--window.print()--}%
%{--</script>--}%
</body>
</html>