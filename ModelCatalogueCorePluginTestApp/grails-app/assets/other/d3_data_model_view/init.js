//= require d3/d3.min.js
//= require validator-js/validator.min.js
//= require underscore/underscore-min.js
//= require remarkable-bootstrap-notify/dist/bootstrap-notify.min.js
//= require jquery-resizable/dist/jquery-resizable.js
//= require_self
// @flow
/**
 * D3 basicView/basic data model view
 */

// exposed variable
var serverUrl = ""

var initD3 = (function() {


  $('#column-left').resizable({
    handleSelector: ".splitter",
    resizeHeight: false
  })

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
  var vis = d3.select("#svg-body").append("svg:svg")
    .attr("width", w + m[1] + m[3])
    .attr("height", h + m[0] + m[2])
    .call(d3.behavior.zoom().on("zoom", function () {
      vis.attr("transform", "translate(" + d3.event.translate + ")" + " scale(" + d3.event.scale + ")")
    }))
    .append("svg:g")
    .attr("transform", "translate(" + (m[3] + 20) + "," + m[0] + ")");

  // Define the div for the tooltip
  var div = d3.select("#svg-body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0);


  //// Link with Grails GSP

  // Parse jsonString to jsonObject
  function parseModelToJS(jsonString /*: string */) /*: Object */ {
    jsonString=jsonString.replace(/\"/g,'"');
    //jsonString=jsonString.replace(/&quot;/g, '"');
    jsonString=jsonString.replace(/&#92;n/g, '<br/>'); // i.e. \n
    jsonString=jsonString.replace(/&#92;t/g, '   '); // i.e. \t
    jsonString=validator.unescape(jsonString); // unescape e.g. &amp;
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

      status: string,
      dateCreated: string,
      lastUpdated: string,
      metadata: {[string]: string},

      loadedChildren: boolean,
      loading: boolean,

      parent: ?D3JSON

      children: ?Array<D3JSON>,

      _children: ?Array<D3JSON>,

      relationships: {[string]: Relation[]},

      enumerations: ?{[string]: string},
      rule: string,
      measurementUnitName: string,
      measurementUnitSymbol: string,
      referenceName: string,
      referenceAngularLink: string,

      x: number,
      y: number,
      x0: number,
      y0: number
    }

    type Relation = {
      name: string,
      angularLink: string
    }
   */


  //// naming functions
  /** return string with first letter upper-cased
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
   * Fill info panels with appropriate HTML generated from D3JSON fields
   * @param d node data
   * @returns {string}
   */
  function info(d /*: D3JSON */) /*: void */ {
     $("#d3-info-name-description").html(
       "<b>Name: "  + "<a href='" + d.angularLink +  "' target='_blank'>" + d.name + "</a>" + "<br/>" +
      " <i>(Click to see Advanced View)</i>" + "</b>" + "<br/>" +
      "<u>Type:</u> " + ucFirst(d.type) + "<br/>" +
      (d.description? "<u>Description:</u> " + d.description + "<br/>" : "") +
       (d.rule ? "<u>Rule:</u> " + d.rule + "<br/>" : "") +
       (d.measurementUnitName ? "<u>Measurement Unit:</u> " + d.measurementUnitName + " (" + d.measurementUnitSymbol + ")<br/>" : "") +
       (d.referenceName ? "<u>References:</u> " + "<a href='" + d.referenceAngularLink +  "' target='_blank'>" + d.referenceName + "</a>" + "<br/>" : "") +
       (d.enumerations ? "<u>Enumerations:</u> <br/>" + displayMap(d.enumerations): ""))

      $("#d3-info-metadata").html(
        "<u>Status:</u> " + d.status + "<br/>" +
        "<u>Date Created:</u> " + d.dateCreated + "<br/>" +
        "<u>Date Updated:</u> " + d.lastUpdated + "<br/>" +
        (d.metadata ? "<u>Metadata:</u> <br/>" + displayMap(d.metadata) + "<br/>" : "")

      )

      $("#d3-info-relationships").html((d.relationships ? displayRelationships(d.relationships): "No Relationships"))
  }

  /**
   * Return HTML for enumeration or metadata of rhe form from Map<String, String>
   * @param map
   * @returns {string}
   */
  function displayMap(map /*:{[string]: string} */) /*: string */ {
    var ret = "<ul>"
    Object.keys(map).forEach(function(key) {
      ret = ret + "<li>" + key + ": " + map[key] + "</li>"
      console.log(key, map[key]);
    });
    return ret = ret + "</ul>"
  }

  /**
   * Display an object which is a map with relationship type names as keys and lists of "Relation"s as values
   * @param relationships
   * @returns {string}
   */
  function displayRelationships(relationships /*:{[string]: Relation[]} */) /*: string */ {
    var ret = ""
    Object.keys(relationships).forEach(function(relationshipTypeName /*: string */){
      var relations /*: Relation[]*/= relationships[relationshipTypeName]
      if (relations.length > 0) {
        relations.forEach(function(relation) {
          ret = ret + relationshipTypeName + ": " + "<a href='" + relation.angularLink + "' target='_blank'>" + relation.name + "</a>" + "<br/>"
        })
      }
    })
    return ret
  }


  function writeMessage(text, type) {
    console.log(text)
    // type = type || 'info' // info by default
    // return $.notify({
    //   'message': (new Date().toLocaleString()) + " " + text
    // }, {
    //   'delay': 200,
    //   'type': type,
    //     'placement': {
    //       'from': "bottom",
    //       'align': "left"
    //   }
    // })
  }

  /**
   * Initialize
   * @param json
   */
  function initD3(json /*: D3JSON */) {
    root = json;
    root.x0 = h / 2;
    root.y0 = 0;
    info(root)
    $('#advanced-view-link').attr('href', root.angularLink)


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

  var dataTypeColour = "green"
  // node colours
  var coloursMap = {
    "dataModel": "blueviolet",
    "dataClass": "blue",
    "dataElement": "gold",
    "dataType": dataTypeColour,
    "enumeratedType": "lawngreen",
    "primitiveType": "olive",
    "referenceType": "mediumseagreen"
  }


  //// path functions

  /**
   * Path from root to d
   * @param d
   * @returns {*|*[]}
   */
  function pathFromRoot(d /*: D3JSON */) /*: D3JSON[] */ {
    var currentNode = d
    var pathToRoot = []

    while (d.parent) {
      pathToRoot.push(d)
      d = d.parent
    }
    // !d.parent, so d is root
    pathToRoot.push(d)

    return pathToRoot.reverse()
  }

  /**
   * Names of nodes along path
   * @param path
   */
  function namesFromPath(path /*: D3JSON[] */ ) /*: string[] */ {
    return _.pluck(path, "name")
  }

  /**
   * String used in angular treeview to represent path.
   * Assumes the first id in the path is a data model id, followed by data class ids, followed by one data element id.
   * Not sure about data types.
   * @param path
   */
  function angularTreeviewPathString(path /*: D3JSON[] */) /*: string */ {
    var ids = _.pluck(path, "id")
    ids.splice(1,0, 'all')
    return ids.join('-')
  }

  /**
   * Update a node (source), usually after it has been toggled.
   * @param source
   */
  function update(source) {

    var duration = d3.event && d3.event.altKey ? 5000 : 500;

    /**
     * node circle radius
     * @type {number}
     */
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

    /**
     * Collapse the siblings of d back into the parent.
     * @param d
     */
    function collapseSiblings(d /*: D3JSON */) {
      var parent = d.parent
      if (parent) {
        parent.children = [d]
        parent.loadedChildren = false
      }
    }

    /**
     * Event handler for node click.
     * Toggles showing children or not.
     * Loads children if not loaded.
     * @param d
     */
    function onNodeClick(d /*: D3JSON */) /*: void */ {
      var path = pathFromRoot(d)
      console.log("Path from root: " + namesFromPath(path))
      console.log("Angular pathstring: " + angularTreeviewPathString(path))

      info(d); // display info

      if (d.children === [] || d.children === null) {
        collapseSiblings(d)
      }

      if (d.loadedChildren && !d.loading) {
        toggle(d);
        update(d);
      }

      else {

        if (!d.loading) { // i.e. !d.loadedChildren

          writeMessage("Loading children for " + typeAndName(d) + "...")
          d.loading = true // try to prevent double-loading, although race conditions may still result if you click fast enough. Not really a completely well-thought-out concurrency thing.
          $.ajax({
            url: serverUrl + "/dataModel/basicViewChildrenData/" + d.type + "/" + d.id
          }).then(function(data /*: ChildrenData */) {

            d.loadedChildren = true;
            d.loading = false;
            // end loading

            if (data.canAccessDataModel && data.caseHandled) {
              d.children = null
              d._children = null
              if (data.children.length > 0) {
                d._children = data.children;
              }
              writeMessage("Loading children for " + typeAndName(d) + " succeeded!", 'success')
              toggle(d);
              update(d);
            }

            else {
              if (!data.canAccessDataModel) {
                writeMessage("You do not have access to the data model of " + typeAndName(d) + " (you may have been logged out) or it does not exist.", 'danger')
                // TODO: If you can't access the data model because you've been logged out, it's more appropriate that d.loadedChildren remains false, so the user can try to load the children again once logged in. But we need a more fine-grained response from the controller to differentiate the reason for inability to access.
              }
              else { // !data.caseHandled
                writeMessage("Loading children is not handled for this case.", 'danger')
              }

            }


          }, function(jqXHR, textStatus, errorThrown) { // request failure
            writeMessage("Loading children for " + typeAndName(d) + " failed with error message: " + errorThrown, 'danger')
            d.loading = false
            // end loading
          })
        }

      }

    }

    /**
     * Set the tooltip on mouseover
     * @param d
     */
    function tooltipMouseover(d) {
      div.transition()
        .duration(0)
        .style("opacity", 1);
      div	.html("<b><h2>" + d.name + "</h2></b>")
        .style("left", (d3.event.pageX) + "px")
        .style("top", (d3.event.pageY - 28) + "px");
      infoMouseover(d)

    }

    /**
     * Fade out the tooltip on mouseout
     * @param d
     */
    function tooltipMouseout(d) {
      div.transition()
        .duration(500)
        .style("opacity", 0);
    }

    /**
     * display info on mouseover
     */
    function infoMouseover(d){
      // don't do anything anymore. Maybe this would be useful another time.
    }

    // ENTER any new nodes at the parent's previous position.
    // the g element includes the circle AND the text next to it (the name). So event listeners registered here will apply to both circle and text.
    var nodeEnter = svgNodes.enter().append("svg:g")
      .attr("class", "node")
      .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
      .on("click", onNodeClick)
      .on('mousedown', function(d) {

        d3.event.stopImmediatePropagation(); // to stop panning
      })



    // add circles for each node
    nodeEnter.append("svg:circle")
      .attr("r", 1e-6)
      .style("fill", function(d) { return !d.children ? coloursMap[d.type] /*"lightsteelblue"*/ : "#fff"; })
      // Tooltip on mouseover
      // .on("mouseover", tooltipMouseover)
      // .on("mouseout", tooltipMouseout);

    /**
     * max string length for text next to node
     * @type {number}
     */
    var maxStringLength = 20;

    /**
     * First n elements of arr, [] if arr is empty
     * @param arr
     * @param n
     * @returns {Array}
     */
    function myFirst(arr, n) {
      return (arr.length == 0) ? [] : _.first(arr, n)
    }


    /**
     * Max number of lines for node label. Should be at least 1.
     * @type {number}
     */
    var maxNodeLabelLines = 10

    /**
     * Splits name into array of three lines, made of whole words, each less than maxStringLength long.
     * @param d
     * @returns {Array}
     */
    function splitName(d) {
      var words = d.name.split(/\s+/) // initial splitting

      var truncatedWords = []

      // split words longer than maxStringLength into parts
      var i = 0
      while (i < words.length) {
        // loop invariant: words1[0..i) has been put into words

        var word = words[i] // word to split

        while (word.length > maxStringLength) {
          // loop invariant: word is always the remainder of words1[i] that has not been pushed into words yet
          // the following is fine even if maxStringLength-1 is greater than the length of word

          truncatedWords.push(word.substring(0,maxStringLength-1) + '-')
          word = word.substring(maxStringLength-1) // make word the remainder
        }

        truncatedWords.push(word) // the remainder. Could be the empty string but that is not a problem
        i++
        console.log(truncatedWords)
      }


      var result = []
      var n = 0
      while (n < maxNodeLabelLines) {
        // words is the remaining array of words

        var wordsPerLine = 0
        var line = myFirst(truncatedWords, wordsPerLine).join(' ')

        while (line.length < maxStringLength && wordsPerLine < truncatedWords.length) {
          // loop invariant: line == myFirst(truncatedWords, wordsPerLine).join(' ')
          wordsPerLine++
          line = myFirst(truncatedWords, wordsPerLine).join(' ')
        }

        if (line.length > maxStringLength) { // loop could terminate due to being over the max string length
          wordsPerLine = wordsPerLine - 1
        }


        result.push(truncatedWords.splice(0, wordsPerLine).join(' '))
        n++
      }


      return result
    }
    function nodeLabelXOffset(d) {
      var extra = 5; return shouldNameBeShortened(d) ? -(radius + extra): radius + extra;
    }

    // Text/link for each node
    var textD3Selection = nodeEnter.append("svg:text")
      .attr("dy", ".35em")
        // text with possibly shortened name

      .attr("x", nodeLabelXOffset)
      .attr("text-anchor", function(d) {
        return shouldNameBeShortened(d) ? "end" : "start";
      })
      // .text(shortenedNodeText)

      .style("fill-opacity", 1e-6)
      .style("font", "15px sans-serif")

    function addLabelLines(textD3Selection) {
      var currentSelection = textD3Selection
      var i = 0
      while (i < maxNodeLabelLines) {
        var dy = (i == 0) ? 5 : 20
        currentSelection = currentSelection.append('svg:tspan')
          .attr("x", nodeLabelXOffset)
          .attr('dy', dy)
          .text(function(d) { return splitName(d)[i]; })
        i++
      }

    }

    addLabelLines(textD3Selection)




    // TRANSITION nodes to their new position.
    var nodeUpdate = svgNodes.transition()
      .duration(duration)
      .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

    nodeUpdate.select("circle")
      .attr("r", radius)
      .style("stroke", function(d) {
        return (d._children || !d.loadedChildren) ? unopenedNodeBorderColour: coloursMap[d.type]; })
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
    "writeMessage": writeMessage
  }
})()
