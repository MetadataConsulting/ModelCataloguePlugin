<html>
<head>
    <title><g:message code="dataModel.create" default="Create Data Model"/></title>
    <meta name="layout" content="main" />
</head>
<body>
<div class="panel-body">
    <div class="page-header">
        <h1><g:message code="dataModel.create" default="Create Data Model"/></h1>
    </div>
<g:render template="/templates/flashmessage" />
<g:render template="/templates/flasherror" />

    <g:form uri='/dataModel/save'>
        <fieldset>
            <ol>
                <li>
                    <label for="name"><b><g:message code="dataModel.name" default="Name"/><span class="alert-danger">*</span></b></label>
                    <g:textField name="name" id="name" required="true" value="${name ?: ''}"/>
                    <p><small><g:message code="dataModel.name.leged" default="Please, keep the name short. It will be displayed next to the declared elements' names"/></small></p>
                </li>
                <li>
                    <label for="name"><b><g:message code="dataModel.semanticVersion" default="Semantic Version"/></b></label>
                    <g:textField name="semanticVersion" id="semanticVersion" value="${semanticVersion ?: ''}"/>
                    <p><small><g:message code="dataModel.semanticVersion.leged" default="You will be able to change semantic version when the data model will be published"/></small></p>
                </li>
                <li>
                    <label for="modelCatalogueId"><b><g:message code="dataModel.modelCatalogueId" default="Catalogue ID"/></b></label>
                    <g:textField name="modelCatalogueId" id="modelCatalogueId" value="${modelCatalogueId ?: ''}"/>
                    <p><small><g:message code="dataModel.modelCatalogueId.leged" default="e.g. external ID, namespace(leave blank for generated)"/></small></p>
                </li>
                <g:if test="${dataModelPolicyList}">
                    <li>
                        <label for="policies"><b><g:message code="dataModel.policies" default="Policies"/></b></label>
                        <ul>
                           <g:each var="dataModelPolicy" in="${dataModelPolicyList}">
                                <li>
                                    <span>${dataModelPolicy.name}</span>
                                    <g:if test="${dataModelPolicies && dataModelPolicies.contains(dataModelPolicy.id)}">
                                        <g:checkBox name="dataModelPolicies" value="${dataModelPolicy.id}" checked="true"/>
                                    </g:if>
                                    <g:else>
                                        <g:checkBox name="dataModelPolicies" value="${dataModelPolicy.id}" checked="false"/>
                                    </g:else>
                                </li>
                           </g:each>
                        </ul>
                    </li>
                </g:if>
                <li>
                    <label for="description"><b><g:message code="dataModel.description" default="Description"/></b></label>
                    <g:textArea name="description" value="${description ?: ''}"/>
                </li>
                <g:if test="${dataModelList}">
                    <li>
                        <label for="importExistingDataModels"><g:message code="dataModel.imports.existingDataModels" default="Import Existing Data Models"/></label>
                        <ul>
                            <g:each var="dataModel" in="${dataModelList}">
                                <li>
                                    <span>${dataModel.name}</span>
                                    <g:if test="${dataModels && dataModels.contains(dataModel.id)}">
                                        <g:checkBox name="dataModels" value="${dataModel.id}" checked="true"/>
                                    </g:if>
                                    <g:else>
                                        <g:checkBox name="dataModels" value="${dataModel.id}" checked="false"/>
                                    </g:else>
                                </li>
                            </g:each>
                        </ul>
                        <p><small><g:message code="dataModel.imports.existingDataModels.legend" default="Import data models already contained in the catalogue for use within your new data model. You can import only finalized data models."/></small></p>
                    </li>
                </g:if>
                <li><input type="submit" value="${g.message(code: 'dataModel.save', default: 'Save')}"/></li>
            </ol>
        </fieldset>
    </g:form>

</div><!-- /.panel-body -->
</body>
</html>
