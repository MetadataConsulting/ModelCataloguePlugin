        <%@ page import="org.modelcatalogue.core.util.CDN; grails.util.BuildScope; org.modelcatalogue.core.EnumeratedType; org.modelcatalogue.core.DataType; org.modelcatalogue.core.MeasurementUnit; org.modelcatalogue.core.ValueDomain; org.modelcatalogue.core.DataElement; org.modelcatalogue.core.Model; grails.util.Environment;grails.util.GrailsNameUtils" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <title>Model Catalogue Demo App</title>
    <g:if test="${CDN.preferred}}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">

        <!-- code -->
        %{--<asset:stylesheet href="modelcatalogue.css"/>--}%
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        %{--<asset:stylesheet href="modelcatalogue.css"/>--}%
    </g:else>
    <style type="text/css">
        td, .preserve-new-lines {
            white-space: pre-line;
        }

        td, .break-long-words {
            word-break: break-all;
        }

        table, .fixed-columns {
            table-layout: fixed;
        }
    </style>
</head>

<body>
<div>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <h1>${classification.name}</h1>
                    <h2>Models</h2>
                    <g:each in="${classification.classifies.findAll{ it in Model}.sort { it.name }}">
                        <h3>${GrailsNameUtils.getNaturalName(it.getClass().simpleName)} ${it.name}</h3>
                        <table class="table">
                            <thead>
                                <tr>
                                    <td class="col-md-4">Version</td>
                                    <td class="col-md-8">${it.versionNumber}</td>
                                </tr>
                                <tr>
                                    <td class="col-md-4">Last Updated</td>
                                    <td class="col-md-8"><g:formatDate date="${it.lastUpdated}" style="medium"/></td>
                                </tr>
                                <!-- proper changelog -->
                            </thead>
                        </table>

                        <g:if test="${it.contains}">
                            <h4>Contained Data Elements</h4>
                            <table class="table">
                                <thead>
                                <tr>
                                    <th class="col-md-6">Name</th>
                                    <th class="col-md-3">Min Repeat</th>
                                    <th class="col-md-3">Min Repeat</th>
                                </tr>
                                </thead>
                                <tbody>
                                    <mc:relationships element="${it}" var="relationship" direction="outgoing" type="containment">
                                        <tr class="element-header-row active">
                                            <td>${relationship.destination.name}</td>
                                            <td>${relationship.ext['Min Occurs']}</td>
                                            <td>${relationship.ext['Max Occurs']}</td>
                                        </tr>
                                        <tr class="element-description-row">
                                            <td colspan="3">${relationship.destination.description}</td>
                                        </tr>
                                        <g:if test="${relationship.destination.valueDomain?.dataType}">
                                            <tr class="element-value-domain-row">
                                                <td>${relationship.destination.valueDomain.dataType.name}</td>
                                                <g:if test="${relationship.destination.valueDomain.dataType instanceof EnumeratedType}">
                                                    <td colspan="2">${relationship.destination.valueDomain.dataType.prettyPrint()}</td>
                                                </g:if>
                                                <g:else>
                                                    <td colspan="2">${relationship.destination.valueDomain.dataType.description}</td>
                                                </g:else>
                                            </tr>
                                        </g:if>
                                    </mc:relationships>
                                </tbody>
                            </table>
                        </g:if>
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