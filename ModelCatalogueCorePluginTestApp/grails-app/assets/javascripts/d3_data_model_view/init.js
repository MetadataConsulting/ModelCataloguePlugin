//= require validator-js/validator.min.js
//= require_self

// dimensions of page
var m = [20, 120, 20, 120],
  w = 1280 + 6000 - m[1] - m[3],
  h = 800 - m[0] - m[2],
  i = 0, // node ids
  root;

// layout generator
var tree = d3.layout.tree()
  .size([h, w]);

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


function parseModelToJS(jsonString) {
  jsonString=jsonString.replace(/\"/g,'"');
  //jsonString=jsonString.replace(/&quot;/g, '"');
  jsonString=jsonString.replace(/&#92;n/g, ' '); // i.e. \n
  jsonString=validator.unescape(jsonString);h // unescape e.g. &amp;
  var jsonObject=$.parseJSON(jsonString);
  return jsonObject
}

/** return Upper Case first letter
 * @param str
 * @returns {string}
 */
function ucFirst(str /*: string */) {
  return str.charAt(0) .toUpperCase() + str.substr(1)
}

/**
 * Return HTML for info panel on the right from node data
 * @param d node data
 * @returns {string}
 */
function info(d) { // d is data with fields name, type, angularLink, etc.
  return "<b>Name: "  + "<a href='" + d.angularLink +  "' target='_blank'>" + d.name + "</a>" + "<br/>" +
    " <i>(Click to see Advanced View)</i>" + "</b>" + "<br/>" +
    "Type: " + ucFirst(d.type)


}

/**
 * Initialize
 * @param json
 */
function initD3(json) {
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
  root.children.forEach(toggleAll);
  // toggle(root.children[1]);
  // toggle(root.children[1].children[2]);
  // toggle(root.children[9]);
  // toggle(root.children[9].children[0]);

  update(root);
};

var coloursMap = {
  "dataModel": "blueviolet",
  "dataClass": "blue",
  "dataElement": "gold"
}



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
    .data(nodeLayoutData, function(d) { return d.id || (d.id = ++i); });



  // ENTER any new nodes at the parent's previous position.
  var nodeEnter = svgNodes.enter().append("svg:g")
    .attr("class", "node")
    .attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
    .on("click", function(d) {
      toggle(d);
      $('#d3-info-element').html(info(d));
      update(d); });

  // add circles for each node
  nodeEnter.append("svg:circle")
    .attr("r", 1e-6)
    .style("fill", function(d) { return !d.children ? coloursMap[d.type] /*"lightsteelblue"*/ : "#fff"; })
    // Tooltip:
    .on("mouseover", function(d) {
        div.transition()
          .duration(0)
          .style("opacity", .9);
        div	.html(d.name)
          .style("left", (d3.event.pageX) + "px")
          .style("top", (d3.event.pageY - 28) + "px");
    })
    .on("mouseout", function(d) {
          div.transition()
            .duration(500)
            .style("opacity", 0);
    });

  var maxStringLength = 10;

  // Text/link for each node
  nodeEnter.append("svg:a")
    .attr("xlink:href", function(d) {return d.angularLink})
    .attr("target", "_blank")
    .append("svg:text")
    .attr("x", function(d) { return d.children || d._children ? -(radius + 5): radius + 5; })
    .attr("dy", ".35em")
    .attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
    .text(function(d) {
      return (d.name.length >= maxStringLength && (d.children || d._children)) ? d.name.substring(0,maxStringLength) + "..." : d.name;  })
    .style("fill-opacity", 1e-6)
    .style("font", "15px sans-serif");
    // .append("rect")
    // .attr("x", 0)
    // .attr("y", 0)
    // .attr("height", 100)
    // .attr("width", 200)
    // .style("fill", "lightgreen")
    // .attr("rx", 10)
    // .attr("ry", 10);

  // nodeEnter




  // TRANSITION nodes to their new position.
  var nodeUpdate = svgNodes.transition()
    .duration(duration)
    .attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

  nodeUpdate.select("circle")
    .attr("r", radius)
    .style("stroke", function(d) { return d._children ? unopenedNodeBorderColour: coloursMap[d.type]; })
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
