<%--
  Created by IntelliJ IDEA.
  User: james
  Date: 16/05/2018
  Time: 17:11
--%>
<%@ page import="grails.converters.JSON" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
  <asset:stylesheet href="d3_data_model_view/style.css"/>
  <asset:javascript  src="d3_data_model_view/d3.js"/>
  <asset:javascript  src="d3_data_model_view/d3.layout.js"/>
  <style type="text/css">


  .node circle {
    cursor: pointer;
    fill: #fff;
    stroke: steelblue;
    stroke-width: 1.5px;
  }

  .node text {
    font-size: 11px;
  }

  path.link {
    fill: none;
    stroke: #ccc;
    stroke-width: 1.5px;
  }

  </style>
  <meta name="layout" content="main"/>
</head>
<body>
<div><div id="body" class="column column-left">
  <!--<div id="footer">
    d3.layout.tree
    <div class="hint">click or option-click to expand or collapse</div>
  </div>-->
</div><div id="d3-info" class="column column-right">
  <div class="border-bottom"><h1><u>Data Model:</u></h1><br/>
    <div id="d3-info-data-model"/>
  </div>
  <div class="border-bottom"><h1><u>Element:</u></h1><br/>
    <div id="d3-info-element"/>
  </div>
  <div id="d3-info-messages"><h1><u>Messages:</u></h1><br/></div>

</div></div>

<asset:javascript  src="d3_data_model_view/init.js"/>
<script type="text/javascript">

  if (${modelFound}) {
    %{--if (${modelTooLarge}) {--}%
      %{--$('#d3-info-messages').append("Model ${dataModelId} too large to load fully. Click on the link to see the full view.")--}%
    %{--}--}%

    initD3(parseModelToJS("${dataModelJson as JSON}")); // do this anyways even if model is too large; it will just load the one node.

  }
  else {
    $('#d3-info-messages').append("Model ${dataModelId} not found. Either it doesn't exist or you are not authorized to view it.")
  }
</script>
</body>
</html>
