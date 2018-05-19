// @flow
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
    <div>

      <div id="body" class="column column-left">
      <!--<div id="footer">
        d3.layout.tree
        <div class="hint">click or option-click to expand or collapse</div>
      </div>-->
      </div>

      <div id="d3-info" class="column column-right">

        <div class="info-box">
          <h1><u>Data Model:</u></h1><br/>
          <div id="d3-info-data-model"></div>
        </div>

        <div class="info-box">
          <h2><u>Element:</u></h2><br/>
          <div id="d3-info-element"></div>
        </div>

      <div class="info-box">
        <h2><u>Messages:</u></h2><br/>
        <ul id="d3-info-messages">

        </ul>
      </div>

      </div>
    </div>

    <asset:javascript  src="d3_data_model_view/init.js"/>
    <script type="text/javascript">

      /** methods from init.js:
       * initD3
       * writeMessage
      */

      serverUrl = "${grailsApplication.config.grails.serverURL}" // for init.js to access


      if (${modelFound}) {
        initD3(parseModelToJS("${dataModelJson as JSON}"));
      }
      else {
        writeMessage("Model ${dataModelId} not found.<br/>"  +
          "Either it doesn't exist or you are not authorized to view it.")
      }


    </script>
  </body>
</html>
