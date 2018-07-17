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

var initD3 = (function() { // initD3 is an object holding functions exposed at the bottom.

  // JQuery Resizable plugin for column splitter
  $('#column-left').resizable({
    handleSelector: ".splitter",
    resizeHeight: false
  })

  //// D3 Setup stuff

  // dimensions of page
  var m = [20, 120, 20, 120],
    w = 1280 + 6000 - m[1] - m[3],
    h = 800 - m[0] - m[2] - 300,
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

  var pageParams = {

    /**
     * Max width of tree. Used in paginated requests for children.
     * @type {number}
     */
    maxPageSize: 20,

  }

  var labelParams = {
    /**
     * max string length for text/label next to node. Should be positive integer.
     * @type {number}
     */
    maxStringLength: 20,

    labelFontSize: 10,

    /**
     * Max number of lines for node label. Should be at least 1.
     * @type {number}
     */
    maxNodeLabelLines: 2, // most space available when maxPageSize is 10 and there are four next/previous nodes and height is h as set at the top:
    // var m = [20, 120, 20, 120],
    // w = 1280 + 6000 - m[1] - m[3],
    // h = 800 - m[0] - m[2],
    initialLineYOffset: 5,
    nodeLabelLineSeparation: 10,

    /**
     * Spacing of the paging nodes above and below parent node
     * @type {number}
     */
    pageNodeSpacing: 30
  }

  var nodeRectParams = {
    dx: -(labelParams.maxStringLength * 7),
    dy: -5,
    rx: 6,
    ry: 6,
    width: 0,
    height: labelParams.maxNodeLabelLines * 20
  }
  nodeRectParams.width = -nodeRectParams.dx

  function rectangleHeight(numLines) {
    var lineHeight = 5 // not sure if this is actually the line height. Not sure about this whole algorithm really
    return -(nodeRectParams.dy) // top
      + labelParams.initialLineYOffset // space to top of first line
      + numLines * labelParams.nodeLabelLineSeparation // rest of the lines
    - 8
       // bottom
  }




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

  //// Types:

  var CONTENT_TYPE = {
    CATALOGUE_ELEMENT: "CATALOGUE_ELEMENT",
    PAGE: "PAGE",
    HEADER: "HEADER"
  };

  /*::
    type ReceivedD3JSON = { // what will be received from the server
      name: string,
      id: number,
      description: string,

      angularLink: string,
      type: string,

      status: string,
      dateCreated: string,
      lastUpdated: string,
      metadata: {[string]: string},

      children: ?Array<ReceivedD3JSON>,
      loadedChildren: boolean,
      loading: boolean,

      relationships: {[string]: Relation[]},

      enumerations: ?{[string]: string},
      rule: ?string,
      measurementUnitName: ?string,
      measurementUnitSymbol: ?string,
      referenceName: ?string,
      referenceAngularLink: ?string
    }

    type Node = CENode | PageNode | HeaderNode
    // the primary recursive type

    type Link = {
      source: Node,
      target: Node
    }

    // Disjoint union:
    type CENode = {nodeContentType: "CATALOGUE_ELEMENT"} & D3JSON & CatalogueElementData & {currentPaginationParams: ?PaginationParams} & ChildrenPaged

    type PageNode = {nodeContentType: "PAGE" } & D3JSON & PaginationParams & {nodeWhoseChildrenArePaged: CENode, direction: Direction}

    type HeaderNode = {nodeContentType: "HEADER"} & Named & D3JSON & {type: string}

    type Named = {name: string}

    type D3JSON = {

      loadedChildren: boolean,
      loading: boolean,
      parent: ?CENode,
      children: ?Array<CENode>,
      _children: ?Array<CENode>,
      nodeId: ?number,


      x: number,
      y: number,
      x0: number,
      y0: number
    }

    type Direction = "UP" | "DOWN"

    type ChildrenPaged = {
      header: HeaderNode,
      pagesUp: Array<PageNode>,

      pagesDown: Array<PageNode>
    }

    type PaginationParams = {
      offset: number,
      max: number,
      total: number
    }

    type CatalogueElementData = Named & {
      id: number,
      description: string,

      angularLink: string,
      type: string,

      status: string,
      dateCreated: string,
      lastUpdated: string,
      metadata: {[string]: string},

      relationships: {[string]: Relation[]},

      enumerations: ?{[string]: string},
      rule: ?string,
      measurementUnitName: ?string,
      measurementUnitSymbol: ?string,
      referenceName: ?string,
      referenceAngularLink: ?string

    }



    type Relation = {
      name: string,
      angularLink: string
    }
   */

  function singlePage(d /*: PaginationParams */) /*: boolean */ {
    return (d.total <= (d.max - d.offset))
  }

  /**
   * Returns true iff the PaginationParams indicate that there are more than one page.
   * @param d
   * @returns {boolean}
   */
  function paged(d /*: PaginationParams */) /*: boolean */ {
    return !singlePage(d)
  }

  function applyDefaultD3JSON(x) /*: D3JSON */ {

    x.loadedChildren = false;
    x.loading = false;
    x.parent = null;
    x.children = null;
    x._children = null;
    x.x = 0;
    x.y = 0;
    x.x0 = 0;
    x.y0 = 0;
    x.nodeId = null;
    return x;
  }

  var DIRECTIONS = {
    UP: "UP",
    DOWN: "DOWN"
  }


  /**
   * Constructor for PageNodes.
   * A PageNode is a node containing information for a paged request.
   * Assuming request with offset and max returns paginated List[offset..offset+max)
   * Where List = List[0..total)
   * @param prevLink
   * @param offset
   * @param max
   * @constructor
   */
  function MakePageNode(
                  offset /*: number */,
                  max /*: number */,
                  total /*: number */,
                  nodeWhoseChildrenArePaged /*: CENode */,
                  direction /*: Direction */) /*: PageNode */{

    this.nodeContentType = CONTENT_TYPE.PAGE

    applyDefaultD3JSON(this)

    this.offset = offset;
    this.max = max;
    this.total = total;
    this.nodeWhoseChildrenArePaged = nodeWhoseChildrenArePaged;
    this.direction = direction;


    return this;

  }


  /**
   * Constructor for HeaderNodes.
   * @param name
   * @returns {MakeHeaderNode}
   * @constructor
   */
  function MakeHeaderNode(name /*: string */, type /*: string */) /*: HeaderNode */ {
    this.nodeContentType = CONTENT_TYPE.HEADER

    applyDefaultD3JSON(this)

    this.name = name
    this.type = type

    return this;
  }

  /**
   * Given handlers for each case (CENode, PageNode), return a handler for a Node
   * @param catalogueElementDataHandler
   * @param pageNodeHandler
   * @param nextLinkDataHandler
   * @returns {handler}
   */
  function nodeHandler/*::<T>*/(
    catalogueElementDataHandler /*: CENode => T */,
    pageNodeHandler /*: PageNode => T */,
    headerNodeHandler /*: HeaderNode => T */
  ) /*: Node => T */ {

    function handler(d /*: Node */) /*: T */ {
      switch (d.nodeContentType) {
        case CONTENT_TYPE.CATALOGUE_ELEMENT:
          if (catalogueElementDataHandler) {
            return catalogueElementDataHandler(d);
          }
          else {
            throw new Error("handler not provided for case CatalogueElementData");
          }

        case CONTENT_TYPE.PAGE:
          if (pageNodeHandler) {
            return pageNodeHandler(d);
          }
          else {
            throw new Error("handler not provided for case Page");
          }

        case CONTENT_TYPE.HEADER:
          if (headerNodeHandler) {
            return headerNodeHandler(d);
          }
          else {
            throw new Error("handler not provided for case Header")
          }


        default:
          throw new Error("Case not found for nodeContentType: " + d.nodeContentType + " of D3JSON " + d)
      }
    }

    return handler
  }


  /**
   * K combinator ("Constant")
   * k(a) returns a function that ignores its argument, b, and returns a
   * k(a)(b) = a
   *
   * More specifically for this use case, k(a) is a handler that can ignore its argument (a Node) and return a
   *
   * @param x
   * @returns {Function}
   */
  function k/*::<A, B>*/(x /*: A */) /*: B => A*/ {
    return function(y /*: B */) {
      return x
    }
  }

  /**
   * Identity function
   * @param x
   * @returns {*}
   */
  function id/*::<T>*/(x /*: T */) /*: T */ {
    return x
  }

  /**
   * Type conversion function
   * @param d
   * @returns {{name, id, description, angularLink: *, type, status, dateCreated, lastUpdated, metadata, loadedChildren: boolean, loading: boolean, parent: null, relationships, enumerations: *, rule, measurementUnitName: *, measurementUnitSymbol: *, referenceName: *, referenceAngularLink: *, children: null, _children: null, x: number, y: number, x0: number, y0: 0; nodeContentType: string}}
   */
  function receiveD3JSON(d /*: ReceivedD3JSON */) /*: CENode */ {
    return {
      nodeContentType: CONTENT_TYPE.CATALOGUE_ELEMENT,

      name: d.name,
      id: d.id,
      description: d.description,
      angularLink: d.angularLink,
      type: d.type,
      status: d.status,
      dateCreated: d.dateCreated,
      lastUpdated: d.lastUpdated,
      metadata: d.metadata,

      relationships: d.relationships,
      enumerations: d.enumerations,
      rule: d.rule,
      measurementUnitName: d.measurementUnitName,
      measurementUnitSymbol: d.measurementUnitSymbol,
      referenceName: d.referenceName,
      referenceAngularLink: d.referenceAngularLink,


      children: d.children ? _.map(d.children, receiveD3JSON) : null, // recurse into children
      loadedChildren: d.loadedChildren,
      loading: d.loading,
      parent: null,
      _children: null,
      nodeId: null,

      x: 0,
      y: 0,
      x0: 0,
      y0: 0,

      currentPaginationParams: null,

      pagesUp: [],
      pagesDown: [],
      header: new MakeHeaderNode(d.name, d.type)


    }
  }




  //// naming functions
  /** return string with first letter upper-cased
   * @param str
   * @returns {string}
   */
  function ucFirst(str /*: string */) {
    return str.charAt(0) .toUpperCase() + str.substr(1)
  }


  function typeAndName(d /*: D3JSON & CatalogueElementData */) {
    return "<b>" + ucFirst(d.type) + " \"" + d.name + "\"</b>"
  }

  //// Info functions

  /**
   * Fill info panels with appropriate HTML generated from D3JSON fields
   * @param d node data
   * @returns {string}
   */
  function info(d /*: CENode */) /*: void */ {


    /**
     * Return HTML for enumeration or metadata of rhe form from Map<String, String>
     * @param map
     * @returns {string}
     */
    function displayMap(map /*:{[string]: string} */, processKey /*: string => string */) /*: string */ {
      var ret = "<ul>"


      Object.keys(map).forEach(function(key) {
        ret = ret + "<li><u>" + processKey(key) + "</u>: " + map[key] + "</li>"
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


    $("#d3-info-name-description").html(
      "<b>Name: "  + "<a href='" + d.angularLink +  "' target='_blank'>" + d.name + "</a>" + "<br/>" +
      " <i>(Click to see Advanced View)</i>" + "</b>" + "<br/>" +
      "<u>Type:</u> " + ucFirst(d.type) + "<br/>" +
      (d.description? "<u>Description:</u> " + d.description + "<br/>" : "") +
      (d.rule ? "<u>Rule:</u> " + d.rule + "<br/>" : "") +
      (d.measurementUnitName ?
        "<u>Measurement Unit:</u> " + d.measurementUnitName + " " +
          (d.measurementUnitSymbol ? "(" + d.measurementUnitSymbol + ")" : "")
        + "<br/>" : "") +
      (d.referenceName ?
        "<u>References:</u> " +
          (d.referenceAngularLink ? "<a href='" + d.referenceAngularLink +  "' target='_blank'>" : "")
        + d.referenceName + "</a>" + "<br/>" : "") +
      (d.enumerations ? "<u>Enumerations:</u> <br/>" + displayMap(d.enumerations, id): ""))

    /**
     * Find the bit of the key after the first hash.
     * @param key
     */
    function stripAndUppercase(key /*: string */) /*: string */ {
      var matchResults = key.match(/#(.*)/)
      if (matchResults && matchResults.length >= 2) {
        return ucFirst(matchResults[1])
      }
      else {
        return key
      }
    }

    $("#d3-info-metadata").html(
      "<u>Status:</u> " + d.status + "<br/>" +
      "<u>Date Created:</u> " + d.dateCreated + "<br/>" +
      "<u>Date Updated:</u> " + d.lastUpdated + "<br/>" +
      (d.metadata ? "<u>Metadata:</u> <br/>" + displayMap(d.metadata, stripAndUppercase) + "<br/>" : "")

    )

    $("#d3-info-relationships").html((d.relationships ? displayRelationships(d.relationships): "No Relationships"))

  }



  function writeMessage(text, type) {
    console.log(text)
    type = type || 'info' // info by default
    return $.notify({
      'message': (new Date().toLocaleString()) + " " + text
    }, {
      'delay': 200,
      'type': type,
        'placement': {
          'from': "bottom",
          'align': "left"
      }
    })
  }

  //// path functions

  /**
   * Path from root to d
   * @param d
   * @returns {*|*[]}
   */
  function pathFromRoot(d /*: D3JSON */) /*: D3JSON[] */ {
    var currentNode /*: ?D3JSON */ = d
    var pathToRoot = []

    while (currentNode && currentNode.parent) {
      pathToRoot.push(currentNode)
      currentNode = currentNode.parent ? currentNode.parent : {parent: null} // flow doesn't notice that the while condition guarantees d.parent exists, so this more immediate check makes it typecheck

    }
    // !d.parent, so d is root
    pathToRoot.push(currentNode)

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

  //// Init, Update and Toggle


  var dataTypeColour = "lime"
  // node colours
  var coloursMap /*: {[string] : string} */ = {
    "dataModel": "plum",
    "dataClass": "skyblue",
    "dataElement": "gold",
    "dataType": dataTypeColour,
    "enumeratedType": "darkorange",
    "primitiveType": "red",
    "referenceType": "seashell"
  }


  /**
   * node circle radius
   * @type {number}
   */
  var radius = 7
  var unopenedNodeBorderColour = "orangered"

  /**
   * Initialize
   * @param json
   */
  function initD3(json /*: ReceivedD3JSON */) {

    root = receiveD3JSON(json);
    root.x0 = h / 2;
    root.y0 = 0;
    info(root)
    $('#advanced-view-link').attr('href', root.angularLink)

    update(root);
  };

  /**
   * traverse func tree applies func to each node of the tree, depth-first.
   * @param func
   * @returns {recursivelyApplyFunc}
   */
  function eachTree(func /*: CENode => void */ ) /*: CENode => void */ {
    function recursivelyApplyFunc(tree /*: CENode */) {
      func(tree)
      _.each(tree.children, recursivelyApplyFunc)
    }

    return recursivelyApplyFunc
  }


  /**
   * Update a node (source), usually after it has been toggled.
   * @param source
   */
  function update(source /*: Node */) {


    /**
     * First n elements of arr, [] if arr is empty
     * @param arr
     * @param n
     * @returns {Array}
     */
    function myFirst(arr, n) {
      return (arr.length === 0) ? [] : _.first(arr, n)
    }


    /**
     * Splits name into array of lines, made of possibly truncated words, each less than maxStringLength long.
     * @param d
     * @returns {Array}
     */
    function splitName(d /*: Named */) /*: string[] */ {
      var words = d.name.split(/\s+/) // initial splitting

      var truncatedWords = []

      // split words longer than maxStringLength into parts
      var i = 0
      while (i < words.length) {
        // loop invariant: words[0..i) has been put into truncatedWords
        // truncatedWords has only strings of length <= maxStringLength

        var word = words[i] // word to split

        while (word.length > labelParams.maxStringLength) {
          // loop invariant: word is always the remainder of words[i] that has not been pushed into truncatedWords yet
          // the following is fine even if maxStringLength-1 is greater than the length of word

          truncatedWords.push(word.substring(0,labelParams.maxStringLength - 1) + '-')
          word = word.substring(labelParams.maxStringLength - 1) // make word the remainder
        }

        truncatedWords.push(word) // the remainder, when word.length <= maxStringLength. Could be the empty string but that is not a problem
        i++
      }

      // now words[0..words.length) has been put into truncatedWords
      // and truncatedWords has only strings of length <= maxStringLength



      var result = []
      var n = 0
      if (labelParams.maxStringLength > 0) {
        while (truncatedWords.length > 0) {
          // words is the remaining array of words

          var wordsPerLine = 0
          var line = myFirst(truncatedWords, wordsPerLine).join(' ')

          while (line.length < labelParams.maxStringLength && wordsPerLine < truncatedWords.length) {
            // loop invariant: line == myFirst(truncatedWords, wordsPerLine).join(' ')
            wordsPerLine++
            line = myFirst(truncatedWords, wordsPerLine).join(' ')
          }

          if (line.length > labelParams.maxStringLength) { // loop could terminate due to being over the max string length
            wordsPerLine = wordsPerLine - 1
          }

          // wordsPerLine will be at least 1 because the inner while loop must run at least once. Unless maxStringLength == 0
          // Thus the outer loop will terminate because truncatedWords gets smaller on each loop.
          result.push(truncatedWords.splice(0, wordsPerLine).join(' '))
          n++
        }
      }



      return result
    }

    function nodeRectangleHeight(d /*: CENode */) {
      return rectangleHeight(Math.min(splitName(d).length, labelParams.maxNodeLabelLines));
    }


    var duration = d3.event && d3.event.altKey ? 5000 : 500;



    // Compute the new tree layout.
    var nodeLayoutData = tree.nodes(root).reverse();


    // Normalize for fixed-depth.
    nodeLayoutData.forEach(function(d) { d.y = d.depth * 180; });

    /**
     * Add PageNodes and HeaderNodes to the layout
     */
    eachTree(function(d /*: CENode */) {
      if (d.currentPaginationParams && paged(d.currentPaginationParams)) {

        function pagesUp(sign /*: number */) /*: boolean */ {
          return (sign < 0)
        }
        function pagesDown(sign /*: number */) /*: boolean */ {
          return !pagesUp(sign)
        }

        _.each([{pages: d.pagesUp, sign: -1},
            {pages: d.pagesDown, sign: 1}],
          function(o /*: {pages: Array<PageNode>, sign: number } */) {

            var pages = o.pages;
            var sign = o.sign;

            var n = pages.length
            var i = 0
            while (i<n) {
              var page /*: PageNode */ = pages[i]
              page.x = d.x + sign * ((i + 1) * labelParams.pageNodeSpacing) + ((pagesDown(sign)) ? nodeRectangleHeight(d) - 10 : 0)
              // because the tree is rotated, x is actually the vertical. Increasing x is going down.
              page.y = d.y - 10
              nodeLayoutData.push(page)
              i++
            }

            if (pagesUp(sign)) { // not single page, so display header

              var header /*: HeaderNode */ = d.header
              header.x = d.x + sign * ((i + 1) * labelParams.pageNodeSpacing) // using i from the while loop above
              header.y = d.y - 10
              nodeLayoutData.push(header)

            }

          })
      }

    })(root)

    // Update the nodes with the data.
    var svgNodes = vis.selectAll("g.node")
      .data(nodeLayoutData, function(d) { return d.nodeId || (d.nodeId = ++i); });



    /**
     * Node Enter block
     */
    (function() {
      /*::
        type ChildrenData = {
            children: Array<ReceivedD3JSON>,
            canAccessDataModel: boolean,
            caseHandled: boolean,
            paginationParametersFound: boolean,
            total: number
        }
      */


      /**
       * Event handler for node click.
       * Toggles showing children or not.
       * Loads children if not loaded.
       * @param d
       */
      function onNodeClick(d /*: Node */) /*: void */ {

        /**
         * Collapse the siblings of d back into the parent.
         * @param d
         */
        function collapseSiblings(d /*: Node */) {
          var parent = d.parent
          if (parent) {
            parent.children = _.filter(parent.children, function(d2 /*: CENode */) {
              return d2 === d
            })
            parent.loadedChildren = false
          }
        }


        var path = pathFromRoot(d)
        console.log("Path from root: " + namesFromPath(path).toString())
        console.log("Angular pathstring: " + angularTreeviewPathString(path))

        if (d.nodeContentType === CONTENT_TYPE.CATALOGUE_ELEMENT) {
          info((d /*: CENode */)); // display info
        }



        if (d.parent && d.parent.children.length > 1) {
          collapseSiblings(d)
        }

        if (d.loadedChildren && !d.loading) {
          toggle(d);
          update(d);
        }

        else {

          if (!d.loading) { // i.e. !d.loadedChildren

            function requestChildrenBaseUrl(d /*: CENode */) /*: string */ {
              return serverUrl + "/dataModel/basicViewChildrenData/" + d.type + "/" + d.id
            }

            /**
             * Returns a CENode handler that requests the children of the node from offset to max
             * @param offset
             * @param max
             */
            function ceNodeChildLoadingHandler(offset /*: number */, max /*: number */) /*: CENode => void */ {

              return function(d /*: CENode */) /*: void */ {
                // writeMessage("Loading children for " + typeAndName(d) + "...")
                d.loading = true // try to prevent double-loading, although race conditions may still result if you click fast enough. Not really a completely well-thought-out concurrency thing.
                $(".loader").removeClass("hidden")
                $.ajax({
                  url: requestChildrenBaseUrl(d) + "?offset=" + offset + "&max=" + max
                  // TODO: Make this paginated
                }).then(function(data /*: ChildrenData */) {

                  d.loadedChildren = true;
                  d.loading = false;
                  $(".loader").addClass("hidden")
                  // end loading

                  if (data.canAccessDataModel && data.caseHandled) {

                    d.children = null;
                    d._children = null;
                    if (data.children.length > 0) {

                      d._children = [];
                      d._children = (_.map(data.children, receiveD3JSON) /*: Array<CENode> */);

                      if (d._children != null && d._children !== undefined) {

                        d.pagesUp = []
                        d.pagesDown = []

                        var total = data.total
                        var actualLengthOfResults = data.children.length

                        var numberOfPages = (total/pageParams.maxPageSize)

                        var pageMultiples = [];

                        (function() {
                          var i = 0;
                          while (Math.pow(2,i) < numberOfPages) {
                            pageMultiples.push(Math.pow(2,i))
                            i++
                          }
                        })();

                        (function (){
                          var i = 0
                          var n = pageMultiples.length
                          while (i< n) {
                            if (i > 0 && pageMultiples[i] === pageMultiples[i-1]) {
                              pageMultiples[i]++
                            }
                            i++
                          }
                        })();

                        d.currentPaginationParams = {
                          offset: offset,
                          max: actualLengthOfResults,
                          total: total
                        }; // current pagination parameters of returned results


                        if (offset > 0) {


                          _.each(pageMultiples, function(multiple) {

                            var newPrevOffset = Math.max(0, offset- multiple * pageParams.maxPageSize)

                            if (newPrevOffset > 0) {
                              d.pagesUp.push(
                                new MakePageNode(
                                  newPrevOffset,
                                  Math.min(pageParams.maxPageSize, total - newPrevOffset),
                                  total,
                                  d,
                                  DIRECTIONS.UP

                                ));
                            }

                          })

                          d.pagesUp.push(
                            // Put a node at the start
                            new MakePageNode(
                              0,
                              Math.min(pageParams.maxPageSize, total),
                              total,
                              d,
                              DIRECTIONS.UP

                            ));


                        }

                        _.each(pageMultiples, function(multiple) {

                          var newNextOffset = offset + multiple * pageParams.maxPageSize
                          if (newNextOffset + pageParams.maxPageSize < total) {
                            // if the new page won't reach the end,
                            // put a link in.
                            d.pagesDown.push(
                              new MakePageNode(
                                newNextOffset,
                                pageParams.maxPageSize,
                                total,
                                d,
                                DIRECTIONS.DOWN
                              ));
                          }

                        })

                        if (offset + actualLengthOfResults < total) {
                          // if current page hasn't reached end
                          // put a node for the end of the results
                          var endMax = Math.min(pageParams.maxPageSize, total)
                          d.pagesDown.push( // End Node
                            new MakePageNode(
                              total - endMax,
                              endMax,
                              total,
                              d,
                              DIRECTIONS.DOWN
                            ));
                        }
                      }


                    }
                    // writeMessage("Loading children for " + typeAndName(d) + " succeeded!", 'success')
                    toggle(d);
                    update(d);
                  }

                  else {
                    if (!data.canAccessDataModel) {
                      writeMessage("You do not have access to the data model of " + typeAndName(d) + " (you may have been logged out) or it does not exist.", 'danger')
                      // TODO: If you can't access the data model because you've been logged out, it's more appropriate that d.loadedChildren remains false, so the user can try to load the children again once logged in. But we need a more fine-grained response from the controller to differentiate the reason for inability to access.
                    }
                    else if (!data.paginationParametersFound) {
                      writeMessage("You did not send the appropriate pagination parameters max and offset in your request.", 'danger')
                    }
                    else { // !data.caseHandled
                      writeMessage("Loading children is not handled for this case.", 'danger')
                    }

                  }


                }, function(jqXHR, textStatus, errorThrown) { // request failure
                  writeMessage("Loading children for " + typeAndName(d) + " failed with error message: " + errorThrown, 'danger')
                  d.loading = false
                  $(".loader").addClass("hidden")
                  // end loading
                })
              }
            }

            nodeHandler(

              function(d /*: CENode */) /*: void */ {

                if (d.currentPaginationParams != null) {
                  var cPP = d.currentPaginationParams
                  ceNodeChildLoadingHandler(cPP.offset, cPP.max)(d)
                }
                else {
                  (ceNodeChildLoadingHandler(0, pageParams.maxPageSize) /*: CENode => void */)(d)
                }

              }, // handles CENode

              function(d /*: PageNode */) /*: void */ { // handles PageNode by calling the appropriate handler on its parent

                ceNodeChildLoadingHandler(d.offset, d.max)(d.nodeWhoseChildrenArePaged)

              },

              function(d /*: HeaderNode */) /*: void */ {}

            )(d)
          }

        }

      }

      var currentNode = svgNodes.filter(function(d) {return d.nodeId === source.nodeId})

      // ENTER any new nodes at the parent's previous position.
      // the g element includes the circle AND the text next to it (the name). So event listeners registered here will apply to both circle and text.
      var nodeEnter = svgNodes.enter().append("svg:g")
        .attr("class", "node")
        .attr("transform", function(d /*: Node */) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
        .on("click", (onNodeClick /*: Node => void */))
        .on('mousedown', function(d /*: Node */) {

          d3.event.stopImmediatePropagation(); // to stop panning
        });

      /**
       * Whether the node is to have a rectangle
       * @param d
       * @returns {*}
       * @constructor
       */
      function useRectangleForNode(d /*: Node */) {
        return nodeHandler(
          (k(true) /*: CENode => boolean */),
          (k(false) /*: PageNode => boolean */),
          (k(true) /*: HeaderNode => boolean */)
        )(d)
      }

      /**
       * Whether the node is to have a circle
       * @param d
       * @returns {*}
       * @constructor
       */
      function useCircleForNode(d /*: Node */) {
        return nodeHandler(
          (k(false) /*: CENode => boolean */),
          (k(true) /*: PageNode => boolean */),
          (k(false) /*: HeaderNode => boolean */)
        )(d)
      }

      currentNode.select("rect")
        .style("fill", (nodeHandler(
        function(d /*: CENode */) { if (d.currentPaginationParams && paged(d.currentPaginationParams)) {
          return '#fff'
        } else {
          return coloursMap[d.type]
        }},
        (k("#fff") /*: PageNode => string */),
        (function(d /*: HeaderNode */) {return coloursMap[d.type] } /*: HeaderNode => string */)
      ) /*: Node => string */))

      // add rectangles for CENodes
      nodeEnter.filter(useRectangleForNode).append("svg:rect")
        .attr("width", nodeRectParams.width)
        .attr("height", nodeRectangleHeight)//nodeRectParams.height)
        .attr('x', nodeRectParams.dx)
        .attr('y', nodeRectParams.dy)
        .attr('rx', nodeRectParams.rx)
        .attr('ry', nodeRectParams.ry)
        .style("fill", (nodeHandler(
          function(d /*: CENode */) { return coloursMap[d.type]  },
          (k("#fff") /*: PageNode => string */),
          (function(d /*: HeaderNode */) {return coloursMap[d.type] } /*: HeaderNode => string */)
        ) /*: Node => string */))
        .attr("stroke-width", 1)
        .attr("stroke", "#808080")

      // add circles for PageNodes
      nodeEnter.filter(useCircleForNode).append("svg:circle")
        .attr("r", 1e-6)
        .style("fill", (nodeHandler(
          function(d /*: CENode */) { return !d.children ? coloursMap[d.type] /*"lightsteelblue"*/ : "#fff"; },
          (k("#fff") /*: PageNode => string */),
          (k("#fff") /*: HeaderNode => string */)
        ) /*: Node => string */));





      /**
       * Node labelling block
       */
      (function(){


        /**
         * X-Offset of node label
         * @param d
         * @returns {*}
         */
        function nodeLabelXOffset(d /*: Node */) /*: number */ {
          if (useRectangleForNode(d)) {
            return nodeRectParams.dx + 5
          }
          else {
            return - (radius + 5)
          }
        }

        /**
         * Text anchor:
         * Whether text is left (start) or right (end) justified
         * @param d
         * @returns {*}
         */
        function startOrEnd(d /*: Node */) /*: string */ {
          if (useRectangleForNode(d)) {
            return "start"
          }
          else {
            return "end"
          }
        }

        // Text/link for each node
        var textD3Selection = nodeEnter.append("svg:text")
          .attr("dy", ".35em")
          // text with possibly shortened name

          .attr("x", nodeLabelXOffset)
          .attr("text-anchor", startOrEnd)
          // .text(shortenedNodeText)

          .style("fill-opacity", 1e-6)
          .style("font", labelParams.labelFontSize + "px sans-serif")

        /**
         * lineText(i) is a handler that takes a node satisfying the Named interface and returns the correct string for
         * line i of the name.
         * @param i
         * @returns {Function}
         */
        function lineText(i /*: number */) /*: Named => string */ {
          return function(d /*: Named */) /*: string */ {
            var line = splitName(d)[i];
            if ((i === labelParams.maxNodeLabelLines - 1 && !!(splitName(d)[i+1]))) {
              // if this is the last line to display but there are still more lines that can't be displayed
              var overflow = line.length + 3 - labelParams.maxStringLength // overflow from adding ellipsis
              if (overflow > 0) {
                return line.slice(0,-(overflow)) + "...";
              }
              else {
                return line + "...";
              }

            }
            else {
              return line;
            }
          }
        }

        function printParams(d /*: PaginationParams */, printTotal /*: boolean */) /*: string */ {
          return "Elements [" + (d.offset + 1) + "-" + (d.offset + d.max - 1 + 1) + "]" + (printTotal ?  " / " + d.total : "")
        }

        /**
         * Add text (label) next to node, in lines
         * @param textD3Selection
         */
        function addLabelLines(textD3Selection) {
          var currentSelection = textD3Selection
          var i /*: number */ = 0 // line number
          while (i < labelParams.maxNodeLabelLines) {
            var dy = (i === 0) ? labelParams.initialLineYOffset : labelParams.nodeLabelLineSeparation
            currentSelection = currentSelection.append('svg:tspan')
              .attr("x", (nodeLabelXOffset /*: Node => number */))
              .attr('dy', dy)
              .text((nodeHandler(
                function(d /*: CENode */) {
                  if (d.currentPaginationParams != null && paged(d.currentPaginationParams)) {
                    var cPP = d.currentPaginationParams
                    return (i === 0) ? printParams(cPP, true)
                                      : ""
                  }
                  else {
                    return lineText(i)(d)
                  }

                },

                (i === 0) ? function(d /*: PageNode */) /*: string */ {
                  // d.offset + d.max - 1 is the index of the last element that would be displayed for that page.
                  // I add 1 to the indexes to "start counting from 1" for the user's sake. Since indexes start counting from 0.
                  return printParams(d, false) // line 0
                } : k(""),
                lineText(i)
              ) /*: Node => string */))
            i++
          }

        }

        addLabelLines(textD3Selection)

        var currentNodeText = currentNode.select("text")
        currentNodeText.html(null)

        addLabelLines(currentNodeText)

        // Font-Awesome Up/Down arrow for pagination nodes
        nodeEnter.append('text')
          .attr('text-anchor', 'middle')
          .attr('dominant-baseline', 'central')
          .attr('font-family', 'FontAwesome')
          .attr('font-size', function(d) { return (d.size-5) +'em'} )
          .text(nodeHandler(
            function(d /*: CENode */) {return ""},
            function(d /*: PageNode */) { return (d.direction === DIRECTIONS.UP) ? '\uf062' : '\uf063' },
            function(d /*: HeaderNode */) { return ""})

          );


      })();

    })();



    /**
     * Transition block
     */
    (function() {

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
      nodeUpdate.select('text').filter(function(d) {return d.nodeId === source.nodeId}).style("color", "red")




      // TRANSITION EXITING nodes to the parent's new position.
      var nodeExit = svgNodes.exit().transition()
        .duration(duration)
        .attr("transform", function(d /*: Node */) { return "translate(" + source.y + "," + source.x + ")"; })
        .remove();


      nodeExit.select("rect")
        .attr("width", 1e-6)
        .attr("height", 1e-6)

      nodeExit.select("circle")
        .attr("r", 1e-6);

      nodeExit.select("text")
        .style("fill-opacity", 1e-6);

    })();



    /**
     * Update links block
     */
    (function() {
      // Update the LINKS…
      var link = vis.selectAll("path.link")
        .data(tree.links(nodeLayoutData), function(d) { return d.target.nodeId; });

      // ENTER any new links at the parent's previous position.
      link.enter().insert("svg:path", "g")
        .attr("class", "link")
        .attr("d", function(l /*: Link */) {
          var o = {x: source.x0, y: source.y0};
          return diagonal({source: o, target: o});
        })
        .transition()
        .duration(duration)
        .attr("d", linkDiagonalAccountingForRectangles);

      function linkDiagonalAccountingForRectangles(l /*: Link */) {
        return nodeHandler(
          function(d /*: CENode */) {
            var linkDataAccountingForRectangle = {
              source: l.source,
              target: {x: l.target.x,
                       y: l.target.y + nodeRectParams.dx}}
            return diagonal(linkDataAccountingForRectangle)
          },
          function(d /*: PageNode */) {
            return diagonal(l)
          }
        )(l.target)
      }

      // Transition links to their new position.
      link.transition()
        .duration(duration)
        .attr("d", linkDiagonalAccountingForRectangles);

      // Transition exiting links to the parent's new position.
      link.exit().transition()
        .duration(duration)
        .attr("d", function(d /*: Node */) {
          var o = {x: source.x, y: source.y};
          return diagonal({source: o, target: o});
        })
        .remove();
    })();


    // Stash the old positions for transition.
    nodeLayoutData.forEach(function(d /*: Node */) {
      d.x0 = d.x;
      d.y0 = d.y;
    });
  }

  // Toggle children.
  function toggle(d /*: D3JSON */) {
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


