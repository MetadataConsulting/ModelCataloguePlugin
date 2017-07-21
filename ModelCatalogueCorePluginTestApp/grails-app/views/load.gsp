<%--
  Created by IntelliJ IDEA.
  User: adam
  Date: 20/05/2017
  Time: 15:17
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>load with redirect</title>
    <script type="text/javascript">
        // similar behavior as an HTTP redirect
        window.location.replace(window.location.href.split("?")[1]);
    </script>
</head>

<body>

</body>
</html>