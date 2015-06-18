<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 26/02/15
  Time: 13:13
--%>

<%@ page import="org.modelcatalogue.core.util.CDN; grails.util.BuildScope; org.modelcatalogue.core.EnumeratedType; org.modelcatalogue.core.DataType; org.modelcatalogue.core.MeasurementUnit; org.modelcatalogue.core.ValueDomain; org.modelcatalogue.core.DataElement; org.modelcatalogue.core.Model; grails.util.Environment;grails.util.GrailsNameUtils" contentType="text/html;charset=UTF-8" defaultCodec="none" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Classification Summary Report</title>
    <g:if test="${CDN.preferred}">
        <!-- CDNs -->
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/4.2.0/css/font-awesome.min.css">
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
                        <g:render template="recModel" model="${[models: model, index:0, valueDomains: valueDomains]}" />
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
                <g:each status="i" in="${valueDomains}" var="valueDomain">
                    <div>
                        <g:if test="${valueDomain.classifications[0]}"> <span class="pull-right">${valueDomain.classifications[0].name}</span></g:if>
                        <h7 id="${valueDomain.id}"><strong>${valueDomain.name}</strong></h7>
                        <p>${valueDomain.description}</p>
                        <g:if test="${valueDomain.dataType instanceof EnumeratedType}">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th width="200">Code</th>
                                    <th width="200">Description</th>
                                </tr>
                                </thead>
                                <g:each in="${valueDomain.dataType?.enumerations}" var="key, value">
                                    <tr><td>${key}</td><td>${value}</td></tr>
                                </g:each>
                            </table>
                        </g:if>
                        <g:else>

                            <g:if test="${valueDomain.dataType?.name}">(${valueDomain.dataType?.name})</g:if>
                            <g:if test="${valueDomain.dataType?.description}">(${valueDomain.dataType?.description})</g:if>
                            <g:if test="${valueDomain.rule}"><p> Format: <code>${valueDomain.rule}</code></p></g:if>
                            <g:if test="${valueDomain.unitOfMeasure?.name}">(${valueDomain.unitOfMeasure?.name})</g:if>
                            <g:if test="${valueDomain.unitOfMeasure?.symbol}">(${valueDomain.unitOfMeasure?.symbol})</g:if>
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