<%@ page import="org.modelcatalogue.core.actions.ActionState;" %>
<g:if test="${actionState == ActionState.PERFORMED}">
    <g:message code="actionState.peformed" default="Accepted"/>
</g:if>
<g:elseif test="${actionState == ActionState.PERFORMING}">
    <g:message code="actionState.peforming" default="Accepting"/>
</g:elseif>
<g:elseif test="${actionState == ActionState.DISMISSED}">
    <g:message code="actionState.dismissed" default="Rejected"/>
</g:elseif>
<g:else>
    ${actionState}
</g:else>