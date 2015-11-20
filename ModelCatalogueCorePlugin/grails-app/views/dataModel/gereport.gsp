<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 26/02/15
  Time: 13:13
--%>

<%@ page import="org.modelcatalogue.core.PrimitiveType; org.modelcatalogue.core.util.CDN; grails.util.BuildScope; org.modelcatalogue.core.EnumeratedType; org.modelcatalogue.core.DataType; org.modelcatalogue.core.MeasurementUnit; org.modelcatalogue.core.DataElement; org.modelcatalogue.core.DataClass; grails.util.Environment;grails.util.GrailsNameUtils" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Classification Summary Report</title>
    <g:if test="${CDN.preferred}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.4.0/css/font-awesome.min.css">
    </g:if>
    <g:else>
        <asset:stylesheet href="bootstrap/dist/css/bootstrap.css"/>
        <asset:stylesheet href="font-awesome/css/font-awesome"/>
        <asset:stylesheet href="modelcatalogue.css"/>
    </g:else>
</head>
<body>
<div>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
            <br/>
            <br/>
            <br/>
                <g:each status="i" in="${models}" var="model">
                    <g:if test="!${model.childOf}">
                        <g:render template="recModel" model="${[models: model, index:0, dataTypes: dataTypes]}" />
                    </g:if>
                </g:each>
            </div>
        </div>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <div class="panel-heading"><h1>VALUE DOMAINS DETAIL</h1></div>
            <br/>
            <br/>
            <br/>
                <g:each status="i" in="${dataTypes}" var="dataType">
                    <div>
                        <g:if test="${dataType.classifications[0]}"> <span class="pull-right">${dataType.classifications[0].name}</span></g:if>
                        <h7 id="${dataType.id}"><strong>${dataType.name}</strong></h7>
                        <p>${dataType.description}</p>
                        <g:if test="${dataType instanceof EnumeratedType}">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th width="200">Code</th>
                                    <th width="200">Description</th>
                                </tr>
                                </thead>
                                <g:each in="${dataType.enumerations}" var="key, value">
                                    <tr><td>${key}</td><td>${value}</td></tr>
                                </g:each>
                            </table>
                        </g:if>
                        <g:else>

                            <g:if test="${dataType.name}">(${dataType.name})</g:if>
                            <g:if test="${dataType.description}">(${dataType.description})</g:if>
                            <g:if test="${dataType.rule}"><p> Format: <code>${dataType.rule}</code></p></g:if>
                            <g:if test="${dataType instanceof org.modelcatalogue.core.PrimitiveType && dataType.measurementUnit?.name}">(${dataType.measurementUnit?.name})</g:if>
                            <g:if test="${dataType instanceof org.modelcatalogue.core.PrimitiveType && dataType.measurementUnit?.symbol}">(${dataType.measurementUnit?.symbol})</g:if>
                        </g:else>
                    </div>
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