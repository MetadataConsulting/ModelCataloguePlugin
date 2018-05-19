//= require validator-js/validator.min.js
//= require_self
// @flow

// exposed variable
var serverUrl = ""

var initFunctions = (function() {

  //// D3 Setup stuff
  // dimensions of page
  var m = [20, 120, 20, 120],
    w = 1280 + 6000 - m[1] - m[3],
    h = 800 - m[0] - m[2],
    i = 0, // node ids
    root;

  // layout generator
  var tree = d3.layout.tree()
    .size([h, w]);

  // transition stuff
  var diagonal = d3.svg.diagonal()
    .projection(function(d) { return [d.y, d.x]; });

  // visualization pane
  var vis = d3.select("#body").append("svg:svg")
    .attr("width", w + m[1] + m[3])
    .attr("height", h + m[0] + m[2])
    .append("svg:g")
    .attr("transform", "translate(" + (m[3] + 20) + "," + m[0] + ")");

  // Define the div for the tooltip
  var div = d3.select("#body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0);


  //// Link with Grails GSP

  // Parse jsonString to jsonObject
  function parseModelToJS(jsonString /*: string */) /*: Object */ {
    jsonString=jsonString.replace(/\"/g,'"');
    //jsonString=jsonString.replace(/&quot;/g, '"');
    jsonString=jsonString.replace(/&#92;n/g, ' '); // i.e. \n
    jsonString=validator.unescape(jsonString);h // unescape e.g. &amp;
    var jsonObject=$.parseJSON(jsonString);
    return jsonObject
  }

  /*::
    type D3JSON = {
      name: string,
      id: number,
      description: string,

      angularLink: string,
      type: string,

      loadedChildren: boolean,
      loading: boolean,

      enumerations: ?Object,

      children: ?Array<D3JSON>,

      _children: ?Array<D3JSON>,

      x: number,
      y: number,
      x0: number,
      y0: number
    }
   */


  //// naming functions
  /** return Upper Case first letter
   * @param str
   * @returns {string}
   */
  function ucFirst(str /*: string */) {
    return str.charAt(0) .toUpperCase() + str.substr(1)
  }


  function typeAndName(d /*: D3JSON */) {
    return "<b>" + ucFirst(d.type) + " \"" + d.name + "\"</b>"
  }

  //// Info functions

  /**
   * Return HTML for info panel on the right from node data
   * @param d node data
   * @returns {string}
   */
  function info(d /*: D3JSON */) {
    return "<b>Name: "  + "<a href='" + d.angularLink +  "' target='_blank'>" + d.name + "</a>" + "<br/>" +
      " <i>(Click to see Advanced View)</i>" + "</b>" + "<br/>" +
      "<u>Type:</u> " + ucFirst(d.type) + "<br/>" +
      (d.description? "<u>Description:</u> " + d.description + "<br/>" : "") +
      (d.enumerations ? enumerate(d.enumerations): "")
  }

  /**
   * Return HTML for enumeration from Map<String, String>
   * @param map
   * @returns {string}
   */
  function enumerate(map) {
    var ret = "<u>Enumerations:</u> <br/> <ul>"
    Object.keys(map).forEach(function(key) {
      ret = ret + "<li>" + key + ": " + map[key] + "</li>"
      console.log(key, map[key]);
    });
    return ret = ret + "</ul>"
  }

  function writeMessage(text) {
    $('#d3-info-messages').append("<li>" + (new Date().toLocaleString()) + ":<br/>" + text + "</li>")
  }


  /**
   * Initialize
   * @param json
   */
  function initD3(json /*: D3JSON */) {
    root = json;
    root.x0 = h / 2;
    root.y0 = 0;
    $('#d3-info-data-model').html(info(root));

    function toggleAll(d) {
      if (d.children) {
        d.children.forEach(toggleAll);
        toggle(d);
      }
    }

    // Initialize the display to show a few nodes.
    // toggle(root.children[1]);
    // toggle(root.children[1].children[2]);
    // toggle(root.children[9]);
    // toggle(root.children[9].children[0]);

    update(root);
  };

  // node colours
  var coloursMap = {
    "dataModel": "blueviolet",
    "dataClass": "blue",
    "dataElement": "gold",
    "dataType": "green"
  }

  /**
   * Update a node (source)
   * @param source
   */
  function update(source) {

    var duration = d3.event && d3.event.altKey ? 5000 : 500;

    var radius = 7
    var unopenedNodeBorderColour = "orangered"


    // Compute the new tree layout.
    var nodeLayoutData = tree.nodes(root).reverse();

    // Normalize for fixed-depth.
    nodeLayoutData.forEach(function(d) { d.y = d.depth * 180; });

    // Update the nodes…
    var svgNodes = vis.selectAll("g.node")
      .data(nodeLayoutData, function(d) { return d.nodeId || (d.nodeId = ++i); });

    /*::
      type ChildrenData = {
          children: Array<D3JSON>,
          canAccessDataModel: boolean,
          caseHandled: boolean
      }
    */

    // load children if not loaded
    function onNodeClick(d) {

      $('#d3-info-element').html(info(d)); // display info

      if (d.loadedChildren && !d.loading) {
        toggle(d);
        update(d);
      }

      else {

        if (!d.loading) { // i.e. !d.loadedChildren

          writeMessage("Loading children for " + typeAndName(d) + "...")
          d.loading = true // try to prevent double-loading, although race conditions may still result if you click fast enough.
          $.ajax({
            url: serverUrl + "/dataModel/basicViewChildrenData/" + d.type + "/" + d.id
          }).then(function(data /*: ChildrenData */) {

            if (data.canAccessDataModel && data.caseHandled) {
              d.children = null
              d._children = data.children;
              writeMessage("Loading children for " + typeAndName(d) + " succeeded!")
              toggle(d);
              update(d);
            }

            else {
              if (!data.canAccessDataModel) {
                writeMessage("You do not have access to the data model of " + typeAndName(d) + " or it does not exist.")
              }
              else { // !data.caseHandled
                writeMessage("Loading children is not handled for this case.")
              }

            }

            d.loadedChildren = true;
            d.loading = false;
            // end loading

          }, function(jqXHR, textStatus, errorThrown) { // request failure
            writeMessage("Loading children for " + typeAndName(d) + " failed with error message: " + errorThrown)
            d.loading = false
            // end loading
          })
        }

      }
    }

    function tooltipMouseover(d) {
      div.transition()
        .duration(0)
        .style("opacity", 1);
      div	.html("<b><h2>" + d.name + "</h2></b>")
        .style("left", (d3.event.pageX) + "px")
        .style("top", (d3.event.pageY - 28) + "px");
      infoMouseover(d)

    }

    function tooltipMouseout(d) {
      div.transition()
        .duration(500)
        .style("opacity", 0);
    }

    // display info on mouseover
    function infoMouseover(d){
      $('#d3-info-element-last-mouseover').html(info(d)); // display info
    }

    // ENTER any new nodes at the parent's previous position.
    var nodeEnter = svgNodes.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", onNodeClick);

    // add circles for each node
    nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      .style("fill", function(d) { return !d.children ? coloursMap[d.type] /*"lightsteelblue"*/ : "#fff"; })
      // Tooltip on mouseover
      .on("mouseover", tooltipMouseover)
      .on("mouseout", tooltipMouseout);

    var maxStringLength = 10;

    function shouldNameBeShortened(d /*: D3JSON */) /*: boolean */ {
      // return d.children || d._children
      return (d.type == 'dataClass' || d.type == 'dataModel' || d.type == 'dataElement')
    }

    function shortenedNodeText(d) {
      return (d.name.length >= maxStringLength && shouldNameBeShortened(d)) ? d.name.substring(0,maxStringLength) + "..." : d.name;  }

    // Text/link for each node
    nodeEnter.append("svg:a")
      .attr("xlink:href", function(d) {return d.angularLink})
      .attr("target", "_blank")

      .append("svg:text")
      .attr("dy", ".35em")
        // text with possibly shortened name

      .attr("x", function(d) { return shouldNameBeShortened(d) ? -(radius + 5): radius + 5; })
      .attr("text-anchor", function(d) {
        return shouldNameBeShortened(d) ? "end" : "start";
      })
      .text(shortenedNodeText)

      .style("fill-opacity", 1e-6)
      .style("font", "15px sans-serif")

      // mouseover to expand the name
      .on("mouseover", function(d) {
        d3.select(this).text(d.name)
        infoMouseover(d)
      })
      .on("mouseout", function(d) {
        d3.select(this).text(shortenedNodeText)
      });




    // TRANSITION nodes to their new position.
    var nodeUpdate = svgNodes.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

    nodeUpdate.select("circle")
      .attr("r", radius)
      .style("stroke", function(d) { return (d._children || !d.loadedChildren) ? unopenedNodeBorderColour: coloursMap[d.type]; })
      .style("stroke-width", 3)
      .style("fill", function(d) { return coloursMap[d.type]})
    ;

    nodeUpdate.select("text")
      .style("fill-opacity", 1);




    // TRANSITION EXITING nodes to the parent's new position.
    var nodeExit = svgNodes.exit().transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
      .remove();

    nodeExit.select("circle")
      .attr("r", 1e-6);

    nodeExit.select("text")
      .style("fill-opacity", 1e-6);




    // Update the LINKS…
    var link = vis.selectAll("path.link")
      .data(tree.links(nodeLayoutData), function(d) { return d.target.id; });

    // ENTER any new links at the parent's previous position.
    link.enter().insert("svg:path", "g")
      .attr("class", "link")
      .attr("d", function(d) {
        var o = {x: source.x0, y: source.y0};
        return diagonal({source: o, target: o});
      })
      .transition()
      .duration(duration)
      .attr("d", diagonal);

    // Transition links to their new position.
    link.transition()
      .duration(duration)
      .attr("d", diagonal);

    // Transition exiting links to the parent's new position.
    link.exit().transition()
      .duration(duration)
      .attr("d", function(d) {
        var o = {x: source.x, y: source.y};
        return diagonal({source: o, target: o});
      })
      .remove();

    // Stash the old positions for transition.
    nodeLayoutData.forEach(function(d) {
      d.x0 = d.x;
      d.y0 = d.y;
    });
  }

  // Toggle children.
  function toggle(d) {
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
  }

  return {
    "parseModelToJS": parseModelToJS,
    "initD3": initD3,
    "writeMessage": writeMessage}
})()

// exposed functions:
var parseModelToJS = initFunctions.parseModelToJS
var initD3 = initFunctions.initD3
var writeMessage = initFunctions.writeMessage
