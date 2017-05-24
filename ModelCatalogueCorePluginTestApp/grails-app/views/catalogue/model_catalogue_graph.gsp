<!-- View for cytoscape graph display. Access at e.g. /catalogue/cytoscapeGraphView?resource=dataModel&id=2943 -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, user-scalable=no" />

    <title>Model Catalogue Graph (beta) </title>

    <asset:stylesheet href="model_catalogue_graph_manifest.css"/>
</head>
<body>

    <div id="nav"> <!-- left nav bar -->
        <p><button id="get-model-list" class="btn btn-default"
                   data-loading-text="<i class='fa fa-spinner fa-pulse fa-fw'></i>
          Loading Model List...">
            <i class="fa fa-th-list"></i>   Get Model List</button></p>
        <div class ="list-group" id="model-list">
            <!-- will be populated in demo.js or on clicking get model list button -->
        </div>
    </div>
    <div class="container">
        <div id="cy"></div> <!-- element that cytoscape will display in -->

        <div id="loading">
            <span class="fa fa-refresh fa-spin"></span>
        </div>

        <div id="search-wrapper">
            <input type="text" class="form-control" id="search" placeholder="&#xf002; Search">
        </div>

        <div id="info"><!--Displays information such as type of element, dataModel, description, metadata. --></div>
        <button id="reset" class="btn btn-default"><i class="fa fa-arrows-h"></i></button>

        <button id="filter" class="btn btn-default"><i class="fa fa-filter"></i></button>
        <button id="about" class="btn btn-default"><i class="fa fa-info"></i></button>

        <div id="about-content">
            This is the Model Catalogue Graph view. It is currently in beta (loading large data sets does not work).
        </div>
        <div id="filters">
            <div class="filterset-section-title">Type</div>

            <div class="filtersets">
                <div class="filterset">
                    <span id="filter-DataModel"><input id="DataModel-checkbox" type="checkbox" checked></input><label for="DataModel-checkbox">Data Model</label></span><br/>
                    <span id="filter-DataClass"><input id="DataClass-checkbox" type="checkbox" checked></input><label for="DataClass-checkbox">Data Class</label></span><br/>
                    <span id="filter-DataElement"><input id="DataElement-checkbox" type="checkbox" checked></input><label for="DataElement-checkbox">Data Element</label></span><br/>
                    <span id="filter-DataType"><input id="DataType-checkbox" type="checkbox" checked></input><label for="DataType-checkbox">Data Type</label></span><br/>
                    <span id="filter-MeasurementUnit"><input id="MeasurementUnit-checkbox" type="checkbox" checked></input><label for="MeasurementUnit-checkbox">Measurement Unit</label></span><br/>
                    <span id="filter-ValidationRule"><input id="ValidationRule-checkbox" type="checkbox" checked></input><label for="ValidationRule-checkbox">Validation Rule</label></span><br/>

                </div>


            </div>
        </div>
    </div>

    <script>
        (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
            m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
        })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

        ga('create', 'UA-155159-12', 'auto');
        ga('send', 'pageview');

    </script>
    <script>
        <%-- prepare graph and graph style urls for javascript to get via AJAX --%>
        var modelCatalogueGraphUrl = '/catalogue/${resource}/${id}/cytoscapeJsonExport'
        var modelCatalogueGraphStyleUrl = '${assetPath(src: 'style.cycss')}'
    </script>
    <asset:javascript src="model_catalogue_graph_manifest.js"/>
    <asset:deferredScripts/>

</body>
</html>