<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><g:message code="batch.create" default="Generate Suggestions"/></title>
    <meta name="layout" content="main" />
</head>
<body>
    <div class="container">
        <div class="page-header">
            <h1><g:message code="batch.create" default="Generate Suggestions"/></h1>
        </div>

        <g:if test="${flash.error}">
            <div class="alert alert-danger">
                <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
                <p>${flash.error}</p>
            </div>
        </g:if>
        <g:form controller="batch" method="POST" action="generateSuggestions">
            <ol>
                <li>
                    <label><g:message code="batch.dataModel.source" default="Data Model 1"/></label>
                    <g:select from="${dataModelList}"
                        name="dataModel1ID"
                        optionValue="name"
                        optionKey="id"
                        value="${batchCreateViewModel.dataModel1}"
                    />
                </li>
                <li>
                    <label><g:message code="batch.dataModel.destination" default="Data Model 2"/></label>
                    <g:select from="${dataModelList}"
                              name="dataModel2ID"
                              optionValue="name"
                              optionKey="id"
                              value="${batchCreateViewModel.dataModel2}"
                    />
                </li>
                <li>
                    <label><g:message code="batch.match.type" default="Select a type of optimization"/></label>
                    <g:select from="${optimizationTypeList}"
                              name='optimizationType'
                              value="${batchCreateViewModel.optimizationType}"
                              valueMessagePrefix="optimizationType"
                    />
                </li>
                <li>
                    <label><g:message code="batch.minSearchScore" default="Min Search Score"/></label>
                    <g:select name='minScore'
                              from="${0..100}"
                              value="${batchCreateViewModel.minScore}"
                    />
                </li>
                <li>
                    <input type="submit" class="btn btn-primary" value="${g.message(code:"batch.generateSuggestions.submit",default:"Generate")}" />
                    <g:link class="btn btn-default" action="all" controller="batch"><g:message code="default.cancel" default="Cancel"/></g:link>
                </li>
            </ol>
        </g:form>
    </div>

</body>
</html>