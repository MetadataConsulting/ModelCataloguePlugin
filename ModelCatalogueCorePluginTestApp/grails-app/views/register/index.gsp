<html>

<head>
    <meta name='layout' content='register'/>
    <title><g:message code='spring.security.ui.register.title'/></title>
</head>

<body>

<g:if test='${emailSent}'>
    <div class="col-md-6 col-md-offset-3">
        <div class="alert alert-info">
            <g:message code='spring.security.ui.register.sent'/>
        </div>
    </div>
</g:if>
<g:else>
    <div class="col-md-6 col-md-offset-3">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><g:message code="springSecurity.oauth.registration.create.legend"
                                                   default="Create a new account"/></h3>
            </div>

            <div class="panel-body">
                <g:hasErrors bean="${command}">
                    <g:each in="${command.errors.allErrors}" var="error">
                        <div class='alert alert-danger'><g:message error="${error}"/></div>
                    </g:each>
                </g:hasErrors>

                <g:form action="register" method="post" autocomplete="off" class="form-horizontal">
                    <div class="form-group ${hasErrors(bean: command, field: 'username', 'has-error')} ">
                        <label for='username-new' class="control-label col-sm-3"><g:message
                                code="user.username.label" default="Username"/>:</label>

                        <div class="col-sm-9">
                            <input type='text' class='form-control' name='username' id="username-new"
                                   value='${command?.username}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'email', 'has-error')} ">
                        <label for='email-new' class="control-label col-sm-3"><g:message code="user.email.label"
                                                                                         default="E-mail"/>:</label>

                        <div class="col-sm-9">
                            <input type='text' class='form-control' name='email' id="email-new" value='${command?.email ?: params.email}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'password', 'has-error')} ">
                        <label for='password' class="control-label col-sm-3"><g:message code="user.password.label" default="Password"/>:</label>

                        <div class="col-sm-9">
                            <input type='password' class='form-control' name='password' id="password"
                                   value='${command?.password}'/>
                        </div>
                    </div>

                    <div class="form-group ${hasErrors(bean: command, field: 'password2', 'has-error')} ">
                        <label for='password2' class="control-label col-sm-3"><g:message code="user.password2.label"
                                                                                         default="Password (Again)"/>:</label>

                        <div class="col-sm-9">
                            <input type='password' class='form-control' name='password2' id="password2"
                                   value='${command?.password2}'/>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-sm-offset-3 col-sm-9">
                            <button type="submit" class="btn btn-primary"><i
                                    class="glyphicon glyphicon-user"></i> <g:message
                                    code="springSecurity.registration.create.button" default="Create"/></button>
                        </div>
                    </div>
                </g:form>
            </div>
        </div>
    </div>
</g:else>

<script>
    $(document).ready(function () {
        $('#username').focus();
    });
</script>

</body>
</html>
