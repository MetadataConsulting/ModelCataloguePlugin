<%@ page import="org.modelcatalogue.core.audit.ChangeType; java.text.SimpleDateFormat" %>
<html>
<head>
    <title><g:message code="changes.title" default="Activity"/></title>
    <meta name='layout' content='main'/>
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="changes.title" default="Activity"/></h1>
    </div>
    <g:render template="/templates/flashmessage" />
    <g:render template="/templates/flasherror" />
    <table class="table">
        <thead>
        <tr>
            <th><g:message code="changes.type" default="Type"/></th>
            <th><g:message code="changes.created" default="Created"/></th>
            <th><g:message code="changes.author" default="Author"/></th>
            <th><g:message code="changes.change" default="Change"/></th>
        </thead>
        <tbody>

        </tbody>
        <g:each var="change" in="${changesList}">
            <tr>
                <td>
                    <span class="fa fa-fw fa-book fa-fw text-primary"></span>
                    <span class="fa fa-fw fa-list-alt fa-fw text-info"></span>
                    <span class="fa fa-edit fa-fw text-info"></span></span>
                    <span class="fa fa-link fa-fw text-success"></span>
                    <span class="fa fa-plus fa-fw text-success"></span>

                    <span class="fa fa-list-ul fa-fw text-success"></span>
                    <span class="fa fa-plus fa-fw text-success"></span>

                    <span class="fa fa-th-list fa-fw text-danger"></span>
                    <span class="fa fa-remove fa-fw text-danger"></span>

                </td>
                <td>${new SimpleDateFormat('dd-MM-yyyy HH:mm').format(change.dateCreated)}</td>
                <td>${change.authorId}</td>
                <td>
                    <g:if test="${change.type == ChangeType.RELATIONSHIP_METADATA_CREATED}">
                        <g:message code="change.type.relationshipMetadataCreated" default=""/>
                    </g:if>
                    <g:elseif test="${change.type == ChangeType.ELEMENT_DEPRECATED}">
                        <g:message code="change.type.elementDeprecated" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.ELEMENT_DELETED}">
                        <g:message code="change.type.elementDeleted" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.EXTERNAL_UPDATE}">
                        <g:message code="change.type.externalUpdate" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.METADATA_CREATED}">
                        <g:message code="change.type.metadataCreated" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.METADATA_UPDATED}">
                        <g:message code="change.type.metadataUpdated" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.RELATIONSHIP_METADATA_UPDATED}">
                        <g:message code="change.type.relationshipMetadataUpdated" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.METADATA_DELETED}">
                        <g:message code="change.type.metadataDeleted" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.NEW_VERSION_CREATED}">
                        <g:message code="change.type.newVersionCreated" default=""/>
                    </g:elseif>
                    <g:elseif test="${change.type == ChangeType.RELATIONSHIP_METADATA_DELETED}">
                        <g:message code="change.type.relationshipMetadataDeleted" default=""/>
                    </g:elseif>

                    <a href="${g.createLink(uri: "/#/catalogue/change/${change.id}")}"><span class="fa fa-fw fa-link">${change.id}</span></a>
                    <br/>
                    ${change.type}
                    <br/>
                    ${change.changedId}</td>
            </tr>
        </g:each>
    </table>
</div>
</body>
</html>