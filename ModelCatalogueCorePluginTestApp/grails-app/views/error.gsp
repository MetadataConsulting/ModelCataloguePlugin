<%@ page import="grails.util.Environment" %>
<!DOCTYPE html>
<html>
	<head>
		<g:set var="security" bean="modelCatalogueSecurityService"/>
		<title><g:if env="development">Grails Runtime Exception</g:if><g:else>Error</g:else></title>
		<meta name="layout" content="register">
		<g:if test="${Environment.DEVELOPMENT == Environment.current}"><asset:stylesheet src="errors.css"/></g:if>
	</head>
	<body>
		<g:if test="${Environment.DEVELOPMENT == Environment.current}">
			<g:renderException exception="${exception}" />
		</g:if>
		<g:else>
            <div class="col-md-6 col-md-offset-3">
                <div class="alert alert-danger">An error has occurred</div>
            </div>
		</g:else>
	</body>
</html>
