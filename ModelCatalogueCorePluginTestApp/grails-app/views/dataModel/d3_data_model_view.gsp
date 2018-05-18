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

      /*::
        type DataModelDisplayData = {
          dataModelJson: object,
          modelFound: boolean,
          modelTooLarge: boolean
        };
      */

      function writeMessage(text) {
        $('#d3-info-messages').append("<li>" + (new Date().toLocaleString()) + ": " + text + "</li>")
      }

      /**
       * load data
       * @param data: DataModelDisplayData
       * @param checks
       */
      function loadData(data /*: DataModelDisplayData */,
                        checks /*: (DataModelDisplayData) => boolean */)
              /*: undefined */ {
        checks = checks || function (data) {return true};

        if (data.modelFound) {

          if (checks(data)) {
            initD3(data.dataModelJson);
          }

        }
        else {
          writeMessage("Model ${dataModelId} not found.<br/>"  +
          "Either it doesn't exist or you are not authorized to view it.")

        }

      }

      /**
       * check data
       * @param data: DataModelDisplayData
       */
      function checkModelTooLarge(data /*: DataModelDisplayData */) /*: boolean */ {
        if (data.modelTooLarge) {
          writeMessage("Model ${dataModelId} too large to load fully. Click on the link to see the full view.")

        }
        return true // If the model is too large it will be returned as just one node, so continue rendering.

      }

      // initial load
      loadData(
        {"dataModelJson": parseModelToJS("${dataModelJson as JSON}"),
          "modelFound": ${modelFound}
        },
        null)

      writeMessage("Loading full data model from server")

      // load more
      $.ajax({

        url:  "${grailsApplication.config.grails.serverURL}/dataModel/basicViewData/${dataModelId}"

      }).then(function(data /*: DataModelDisplayData */) {

        writeMessage("Response received from server")
        loadData(data, checkModelTooLarge)

      })


    </script>
  </body>
</html>
