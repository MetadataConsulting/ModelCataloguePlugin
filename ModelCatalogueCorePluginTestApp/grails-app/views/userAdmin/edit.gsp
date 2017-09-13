<html>
<%@ page import="grails.util.Holders" %>

<sec:ifNotSwitched>
	<sec:ifAllGranted roles='ROLE_SWITCH_USER'>
	<g:if test='${user.username}'>
	<g:set var='canRunAs' value='${true}'/>
	</g:if>
	</sec:ifAllGranted>
</sec:ifNotSwitched>

<head>
	<meta name='layout' content='springSecurityUI'/>
	<g:set var="entityName" value="${message(code: 'user.label', default: 'User')}"/>
	<title><g:message code="default.edit.label" args="[entityName]"/></title>
</head>

<body>

<h3><g:message code="default.edit.label" args="[entityName]"/></h3>

<g:form action="update" name='userEditForm' class="button-style">
<g:hiddenField name="id" value="${user?.id}"/>
<g:hiddenField name="version" value="${user?.version}"/>

<%
def tabData = []
tabData << [name: 'userinfo', icon: 'icon_user', messageCode: 'spring.security.ui.user.info']
tabData << [name: 'roles',    icon: 'icon_role', messageCode: 'spring.security.ui.user.roles']
boolean isOpenId = Holders.pluginManager.hasGrailsPlugin('springSecurityOpenid')
if (isOpenId) {
	tabData << [name: 'openIds', icon: 'icon_role', messageCode: 'spring.security.ui.user.openIds']
}
%>

<s2ui:tabs elementId='tabs' height='375' data="${tabData}">

	<s2ui:tab name='userinfo' height='275'>
		<table>
		<tbody>

			<s2ui:textFieldRow name='username' labelCode='user.username.label' bean="${user}"
                            labelCodeDefault='Username' value="${user?.username}"/>

			<s2ui:passwordFieldRow name='password' labelCode='user.password.label' bean="${user}"
                                labelCodeDefault='Password' value="${user?.password}"/>

			<s2ui:textFieldRow name='email' labelCode='user.username.email' bean="${user}"
							   labelCodeDefault='Email' value="${user?.email}"/>

			<s2ui:checkboxRow name='enabled' labelCode='user.enabled.label' bean="${user}"
                           labelCodeDefault='Enabled' value="${user?.enabled}"/>

			<s2ui:checkboxRow name='accountExpired' labelCode='user.accountExpired.label' bean="${user}"
                           labelCodeDefault='Account Expired' value="${user?.accountExpired}"/>

			<s2ui:checkboxRow name='accountLocked' labelCode='user.accountLocked.label' bean="${user}"
                           labelCodeDefault='Account Locked' value="${user?.accountLocked}"/>

			<s2ui:checkboxRow name='passwordExpired' labelCode='user.passwordExpired.label' bean="${user}"
                           labelCodeDefault='Password Expired' value="${user?.passwordExpired}"/>
		</tbody>
		</table>
	</s2ui:tab>

	<s2ui:tab name='roles' height='auto'>

		<h2>General Roles</h2>
		<p>These roles determine general access, such as whether you can view a list of all the draft models in the application or administer users.</p>

		<g:each var="entry" in="${generalRoles}">
		<div>
			<g:checkBox name="${entry.key.authority}" value="${entry.value}"/>
			<g:link controller='role' action='edit' id='${entry.key.id}'>${entry.key.authority.encodeAsHTML()}</g:link>
		</div>
		</g:each>

		</br>

		<h2>Specific Roles</h2>
		<p>These roles are specific to a model - a user could be a curator on one model but not have access to view the elements of another model</p>
		<p>NOTE: by aware of imports - if a model imports another model you should give the users ROLE_USER access, otherwise they may not be able to view some elements</p>
		<table>
			<tr>
				<th>&nbsp;</th>
				<g:each var="roles" in="${specificRoles}">
					<th>${roles.authority}</th>
				</g:each>
			</tr>

			<g:each var="model" in="${userRoles}">
				<tr>
					<th>${model?.value?.name}</th>

					<g:each var="role" in="${model.value?.roleList}">
						<td>
							<g:checkBox name="${model?.key}-${role.key?.authority}" value="${role.value}"/>
						</td>
					</g:each>
				</tr>
			</g:each>

		</table>
	</s2ui:tab>


	<g:if test='${isOpenId}'>
	<s2ui:tab name='openIds' height='275'>
	<g:if test='${user?.openIds}'>
		<ul>
		<g:each var="openId" in="${user.openIds}">
		<li>${openId.url}</li>
		</g:each>
		</ul>
	</g:if>
	<g:else>
	No OpenIDs registered
	</g:else>
	</s2ui:tab>
	</g:if>

</s2ui:tabs>

<div style='float:left; margin-top: 10px;'>
<s2ui:submitButton elementId='update' form='userEditForm' messageCode='default.button.update.label'/>

<g:if test='${user}'>
<s2ui:deleteButton />
</g:if>

<g:if test='${canRunAs}'>
<a id="runAsButton">${message(code:'spring.security.ui.runas.submit')}</a>
</g:if>

</div>

</g:form>

<g:if test='${user}'>
<s2ui:deleteButtonForm instanceId='${user.id}'/>
</g:if>

<g:if test='${canRunAs}'>
	<form name='runAsForm' action='${request.contextPath}/j_spring_security_switch_user' method='POST'>
		<g:hiddenField name='j_username' value="${user.username}"/>
		<input type='submit' class='s2ui_hidden_button' />
	</form>
</g:if>

<script>
$(document).ready(function() {
	$('#username').focus();

	<s2ui:initCheckboxes/>

	$("#runAsButton").button();
	$('#runAsButton').bind('click', function() {
	   document.forms.runAsForm.submit();
	});
});
</script>

</body>
</html>
