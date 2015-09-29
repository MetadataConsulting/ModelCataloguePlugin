<html>

<head>
<title><g:message code='spring.security.ui.resetPassword.title'/></title>
<meta name='layout' content='register'/>
</head>

<body>

<div class="col-md-6 col-md-offset-3">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><g:message code="spring.security.ui.resetPassword.header"
											   default="Reset Password"/></h3>
		</div>

		<div class="panel-body">
			<g:hasErrors bean="${command}">
				<g:each in="${command.errors.allErrors}" var="error">
					<div class='alert alert-danger'><g:message error="${error}"/></div>
				</g:each>
			</g:hasErrors>

			<g:form action="resetPassword" method="post" autocomplete="off" class="form-horizontal">
				<div class="alert alert-info">
					<g:message code='spring.security.ui.resetPassword.description'></g:message>
				</div>

                <g:hiddenField name='t' value='${token}'/>

				<div class="form-group">
					<label for='password' class="control-label col-sm-3"><g:message code="user.password.label" default="Password"/>:</label>

					<div class="col-sm-9">
						<input type='password' class='form-control' name='password' id="password"/>
					</div>
				</div>

				<div class="form-group">
					<label for='password2' class="control-label col-sm-3"><g:message code="user.password2.label"
																					 default="Password (Again)"/>:</label>

					<div class="col-sm-9">
						<input type='password' class='form-control' name='password2' id="password2" />
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-3 col-sm-9">
						<button type="submit" class="btn btn-primary"><i
								class="glyphicon glyphicon-repeat"></i> <g:message
								code="spring.security.ui.resetPassword.submit" default="Reset Password"/></button>
					</div>
				</div>
			</g:form>
		</div>
	</div>
</div>


<script>
$(document).ready(function() {
	$('#password').focus();
});
</script>

</body>
</html>
