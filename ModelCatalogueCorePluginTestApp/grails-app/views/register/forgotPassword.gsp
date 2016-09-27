<html>

<head>
<title><g:message code='spring.security.ui.forgotPassword.title'/></title>
<meta name='layout' content='register'/>
</head>

<body>

<g:if test='${emailSent}'>
    <div class="col-md-6 col-md-offset-3">
	    <div class="alert alert-info">
		    <g:message code='spring.security.ui.forgotPassword.sent'/>
        </div>
	</div>
</g:if>
<g:else>
	<div class="col-md-6 col-md-offset-3">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title"><g:message code='spring.security.ui.forgotPassword.header' default="Forgot Password"/></h3>
			</div>

			<div class="panel-body">
                <div class="alert alert-info">
                    <g:message code='spring.security.ui.forgotPassword.description'></g:message>
                </div>
				<g:form action="forgotPassword" method="post" autocomplete="off" class="form-horizontal">
					<div class="form-group ">
						<label for='username-new' class="control-label col-sm-3"><g:message
								code="user.username.or.email.label" default="Username or Email"/>:</label>

						<div class="col-sm-9">
							<input type='text' class='form-control' name='username' id="username-new" />
						</div>
					</div>

					<div class="form-group">
						<div class="col-sm-offset-3 col-sm-9">
							<button type="submit" class="btn btn-primary"><i
									class="glyphicon glyphicon-repeat"></i> <g:message
									code="springSecurity.registration.create.button" default="Reset My Password"/></button>
						</div>
					</div>
				</g:form>
			</div>
		</div>
	</div>
</g:else>

<script>
$(document).ready(function() {
	$('#username').focus();
});
</script>

</body>
</html>
