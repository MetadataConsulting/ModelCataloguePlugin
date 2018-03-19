<%@ page import="org.modelcatalogue.core.actions.ActionState;" %>
<g:if test="${actionState == ActionState.PERFORMED}">
    <g:message code="actionState.peformed" default="PERFORMED"/>
</g:if>
<g:elseif test="${actionState == ActionState.PERFORMING}">
    <g:message code="actionState.peforming" default="PERFORMING"/>
</g:elseif>
<g:elseif test="${actionState == ActionState.DISMISSED}">
    <g:message code="actionState.dismissed" default="DISMISSED"/>
</g:elseif>
<g:else>
    ${actionState}
</g:else>
