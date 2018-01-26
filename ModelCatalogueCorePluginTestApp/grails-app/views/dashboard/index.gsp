<%@ page import="org.modelcatalogue.core.util.PublishedStatus; org.modelcatalogue.core.DataModel" %>
<html>
<head>
    <title><g:message code="dashboard.title" default="Data Models"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="container">
    <div class="page-header">
        <h1><g:message code="dashboard.title" default="Data Models"/></h1>
    </div>

    <g:if test="${flash.message}">
        <div class="alert alert-info">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <p>${flash.message}</p>
        </div>
    </g:if>

    <g:if test="${flash.error}">
        <div class="alert alert-danger">
            <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
            <p>${flash.error}</p>
        </div>
    </g:if>

    <g:if test="${models}">
            <table>
                <thead>
                <tr>
                    <th><g:message code="model.name" default="Name"/></th>
                    <th><g:message code="model.semanticVersion" default="Semantic Version"/></th>
                    <th><g:message code="model.status" default="Status"/></th>
                    <th><g:message code="model.lastUpdated" default="Last Updated"/></th>
                    <th><g:message code="model.schemas" default="Asset"/></th>
                </tr>
                </thead>
                <tbody>
                <g:each var="model" in="${models}">
                    <tr>
                        <td>
                            <a href="/#/${model.id}/dataModel/${model.id}">${model.name}</a>
                        </td>
                        <td>${model.semanticVersion}</td>
                        <td>${model.status}</td>
                        <td>${model.lastUpdated}</td>
                        <td>
                            <g:if test="${model.assets}">
                                <table>
                                <g:each var="asset" in="${model.assets}">
                                    <g:if test="${asset.publishedStatus == org.modelcatalogue.core.util.PublishedStatus.PUBLISHED}">
                                    <tr>
                                    <td>
                                        <a href="/#/${model.id}/asset/${asset.id}">${asset.name}</a>
                                    </td>
                                    <td>${asset.description}</td>
                                    </tr>
                                    </g:if>
                                </g:each>
                                </table>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

    </g:if>

</div>
</body>
</html>
