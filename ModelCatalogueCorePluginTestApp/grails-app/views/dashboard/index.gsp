<%@ page import="org.modelcatalogue.core.util.PublishedStatus; org.modelcatalogue.core.DataModel" %>
<html>
<head>
    <title><g:message code="dashboard.title" default="Data Models"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel panel-default">
    <div class="panel-heading">

        <g:form url="[action:'index']" method="get" >
        <div>
            <div class="input-group">
                <g:textField class="form-control" name="search" value="${params.search}" aria-label="..."/>
                <div class="input-group-btn">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${params.status} <span class="caret"></span></button>
                <ul class="dropdown-menu dropdown-menu-right">
                    <li><input type="submit" class="form-control btn btn-link" name="status" value="active"></li>
                    <li role="separator" class="divider"></li>
                    <li><input type="submit" class="form-control btn btn-link" name="status" value="finalized"></li>
                    <li><input type="submit" class="form-control btn btn-link" name="status" value="draft"></li>
                    <li role="separator" class="divider"></li>
                    <li><input type="submit" class="form-control btn btn-link" name="status" value="deprecated"></li>
                </ul>
                </div><!-- /btn-group -->
                <div class="input-group-btn">
                <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">${params.elementType} <span class="caret"></span></button>
                <ul class="dropdown-menu dropdown-menu-right">
                    <li><button type="submit" class="form-control btn btn-link" name="elementType" value="data model">data model</button></li>
                    <li role="separator" class="divider"></li>
                    <li><button type="submit" class="form-control btn btn-link" name="elementType" value="data class">data class</button></li>
                    <li><button type="submit" class="form-control btn btn-link" name="elementType" value="data element">data element</button></li>
                    <li><button type="submit" class="form-control btn btn-link" name="elementType" value="data type">data type</button></li>
                    <li><button type="submit" class="form-control btn btn-link" name="elementType" value="all">all</button></li>
                </ul>
                </div><!-- /btn-group -->
            </div><!-- /input-group -->
        </div><!-- /input-group -->
        </div>

        </g:form>

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

    <div class="panel-body">
    <g:if test="${params.elementType=="data model"}">
            <table>
                <thead>
                <tr>

                    <g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="name" title="${message(code: 'dataModel.name.label', default: 'Name')}" />

                    <g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="semanticVersion" title="${message(code: 'model.semanticVersion.label', default: 'Semantic Version')}" />

                    <g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="status" title="${message(code: 'dataModel.status.label', default: 'Status')}" />

                    <g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="lastUpdated" title="${message(code: 'dataModel.lastUpdated.label', default: 'Last Updated')}" />

                    <th><g:message params="${[status:params?.status, search:params?.search]}" code="model.asset" default="Asset"/></th>

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

        <div class="pagination">
            <g:paginate total="${total ?: 0}" params="${[status:params?.status, search:params?.search, elementType: params?.elementType]}"/>
        </div>

    </g:if>

    <g:if test="${params.elementType!="data model"}">

        <table>
            <thead>
            <tr>

                <g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="name" title="${message(code: 'dataModel.name.label', default: 'Name')}" />

                %{--<g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="semanticVersion" title="${message(code: 'model.semanticVersion.label', default: 'Semantic Version')}" />--}%

                %{--<g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="status" title="${message(code: 'dataModel.status.label', default: 'Status')}" />--}%

                %{--<g:sortableColumn params="${[status:params?.status, search:params?.search]}" property="lastUpdated" title="${message(code: 'dataModel.lastUpdated.label', default: 'Last Updated')}" />--}%

                %{--<th><g:message params="${[status:params?.status, search:params?.search]}" code="model.asset" default="Asset"/></th>--}%

            </tr>
            </thead>
            <tbody>
            <g:each var="model" in="${models}">
                <tr>
                    <td>
                        <a href="/#/${model.id}/dataModel/${model.id}">${model.name}</a>
                    </td>
                    %{--<td>${model.semanticVersion}</td>--}%
                    %{--<td>${model.status}</td>--}%
                    %{--<td>${model.lastUpdated}</td>--}%
                    %{--<td>--}%
                        %{--<g:if test="${model.assets}">--}%
                            %{--<table>--}%
                                %{--<g:each var="asset" in="${model.assets}">--}%
                                    %{--<g:if test="${asset.publishedStatus == org.modelcatalogue.core.util.PublishedStatus.PUBLISHED}">--}%
                                        %{--<tr>--}%
                                            %{--<td>--}%
                                                %{--<a href="/#/${model.id}/asset/${asset.id}">${asset.name}</a>--}%
                                            %{--</td>--}%
                                        %{--</tr>--}%
                                    %{--</g:if>--}%
                                %{--</g:each>--}%
                            %{--</table>--}%
                        %{--</g:if>--}%
                    %{--</td>--}%
                </tr>
            </g:each>
            </tbody>
        </table>


    </g:if>


    </div>

</div>
</body>
</html>
