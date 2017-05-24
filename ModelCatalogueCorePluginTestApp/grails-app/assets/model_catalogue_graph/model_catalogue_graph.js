/*
This demo visualises the Model Catalogue.
*/

$(function(){

  var layoutPadding = 50;
  var aniDur = 500;
  var easing = 'linear';

  var cy;

  // get exported json from cytoscape desktop via ajax
  var graphP = $.ajax({
      url: modelCatalogueGraphUrl,
      type: 'GET',
      dataType: 'json'
  });

  // also get style via ajax
  var styleP = $.ajax({ //an asynchronous call
    url: modelCatalogueGraphStyleUrl, // wine-and-cheese-style.cycss
    type: 'GET',
    dataType: 'text'
  });
  var styleCycssResult;

  // Template for info box:
  var infoTemplate = Handlebars.compile([
    // James: Template to display node data. Different element types will have different data.
    // e.g. ValidationRules and DataTypes will have more data, though I haven't implemented this yet.
    // This template is applied to a node and the resulting html injected into the info div.


    '<ul class="list-group" id="info-list"',
    '<li class="list-group-item"><p class="ac-name">{{name}}</p></li>',

    '<li class="list-group-item"><p class="ac-more"><i class="fa fa-external-link"></i> <a target="_blank" href="{{id}}">See in Catalogue</a></p></li>',
    //'<p class="ac-node-type"><i class="fa fa-info-circle"></i> {{NodeTypeFormatted}} {{#if Type}}({{Type}}){{/if}}</p>',
    '{{#if type}}<li class="list-group-item"><p class="ac-type"><i class="fa fa-angle-double-right"></i> type: {{type}}</p></li>{{/if}}',
    '{{#if dataModel}}<li class="list-group-item"><p class="ac-dataModel"><i class="fa fa-angle-double-right"></i> dataModel: {{dataModel}}</p></li>{{/if}}',
    '{{#if description}}<li class="list-group-item"><p class="ac-description"><i class="fa fa-angle-double-right"></i> description: {{description}}</p></li>{{/if}}',
    '{{#if metadata}}<li class="list-group-item"><p class="ac-metadata"><i class="fa fa-angle-double-right"></i> metadata: {{#each metadata}} <p><i class="fa fa-angle-double-right"></i><i class="fa fa-angle-double-right"></i> {{@key}}: {{this}}</p>{{/each}}</p></li>{{/if}}',
    '</ul>'
    //'{{#if Milk}}<p class="ac-milk"><i class="fa fa-angle-double-right"></i> {{Milk}}</p>{{/if}}',
    //'{{#if Country}}<p class="ac-country"><i class="fa fa-map-marker"></i> {{Country}}</p>{{/if}}',
  ].join(''));

  // when both graph export json and style loaded, init cy
  Promise.all([ graphP, styleP ]).then(initCy);

  var allNodes = null;
  var allEles = null;
  var lastHighlighted = null; // set of nodes to be shown
  var lastUnhighlighted = null; // set of nodes to hide

  /// Layout options:
  // concentric: circular
  var concentric_layout_options = {
    name: 'concentric',

    fit: true, // whether to fit the viewport to the graph
    padding: 30, // the padding on fit
    startAngle: 3 / 2 * Math.PI, // where nodes start in radians
    sweep: undefined, // how many radians should be between the first and last node (defaults to full circle)
    clockwise: true, // whether the layout should go clockwise (true) or counterclockwise/anticlockwise (false)
    equidistant: false, // whether levels have an equal radial distance betwen them, may cause bounding box overflow
    minNodeSpacing: 3, // min spacing between outside of nodes (used for radius adjustment)
    boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
    avoidOverlap: true, // prevents node overlap, may overflow boundingBox if not enough space
    height: undefined, // height of layout area (overrides container height)
    width: undefined, // width of layout area (overrides container width)
    concentric: function( node ){ // returns numeric value for each node, placing higher nodes in levels towards the centre
    return node.degree();
    },
    levelWidth: function( nodes ){ // the variation of concentric values in each level
    return nodes.maxDegree() / 4;
    },
    animate: true,
    animationDuration: aniDur,
    animationEasing: easing,
    ready: undefined, // callback on layoutready
    stop: undefined // callback on layoutstop
  };
  // cose: Compound Spring Embedder, a force-directed graph layout
  var cose_layout_options = {
    name: 'cose',

    // Called on `layoutready`
    ready: function(){},

    // Called on `layoutstop`
    stop: function(){},

    // Whether to animate while running the layout
    animate: false,

    // The layout animates only after this many milliseconds
    // (prevents flashing on fast runs)
    animationThreshold: 100,

    // Number of iterations between consecutive screen positions update
    // (0 -> only updated on the end)
    refresh: 60,

    // Whether to fit the network view after when done
    fit: true,

    // Padding on fit
    padding: 30,

    // Constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
    boundingBox: undefined,

    // Randomize the initial positions of the nodes (true) or use existing positions (false)
    randomize: true,

    // Extra spacing between components in non-compound graphs
    componentSpacing: 100,

    // Node repulsion (non overlapping) multiplier
    nodeRepulsion: function( node ){ return 400000; },

    // Node repulsion (overlapping) multiplier
    nodeOverlap: 10,

    // Ideal edge (non nested) length
    idealEdgeLength: function( edge ){ return 10; },

    // Divisor to compute edge forces
    edgeElasticity: function( edge ){ return 100; },

    // Nesting factor (multiplier) to compute ideal edge length for nested edges
    nestingFactor: 5,

    // Gravity force (constant)
    gravity: 80,

    // Maximum number of iterations to perform
    numIter: 1000,

    // Initial temperature (maximum node displacement)
    initialTemp: 200,

    // Cooling factor (how the temperature is reduced between consecutive iterations
    coolingFactor: 0.95,

    // Lower temperature threshold (below this point the layout will end)
    minTemp: 1.0,

    // Pass a reference to weaver to use threads for calculations
    weaver: false
  };
  // dagre: a particular javascript library for directed graphs– draws medium sized graphs quickly, completely client-side
  var dagre_layout_options = {

  name: 'dagre',
  // dagre algo options, uses default value on undefined
  nodeSep: 10, // the separation between adjacent nodes in the same rank
  edgeSep: 10, // the separation between adjacent edges in the same rank
  rankSep: 10, // the separation between adjacent nodes in the same rank
  rankDir: 'TB', // 'TB' for top to bottom flow, 'LR' for left to right
  minLen: function( edge ){ return 1; }, // number of ranks to keep between the source and target of the edge
  edgeWeight: function( edge ){ return 1; }, // higher weight edges are generally made shorter and straighter than lower weight edges

  // general layout options
  fit: true, // whether to fit to viewport
  padding: 30, // fit padding
  animate: false, // whether to transition the node positions
  animationDuration: aniDur, // duration of animation in ms if enabled
  animationEasing: undefined, // easing of animation if enabled
  boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
  ready: function(){}, // on layoutready
  stop: function(){} // on layoutstop
  };
  var layout_options = cose_layout_options

  /// Showing and hiding nodes
  function getFadePromise( ele, opacity ){
    return ele.animation({
      style: { 'opacity': opacity },
      duration: aniDur
    }).play().promise();
  };
  var nodeTypeList = ["DataModel", "DataClass", "DataElement", "DataType", "MeasurementUnit", "ValidationRule"] // don't change this variable in code...
  var applyFilter =  function(){ // used on filter UI change, and on node unselect.
    cy.batch(function(){

      allNodes.forEach(function( n ){
        var nType = n.data('type');
        n.removeClass('filtered');

        nodeTypeList.forEach(function(typeFromList) {
          if (nType == typeFromList && !$('#'+typeFromList+'-checkbox').is(':checked')) {
            n.addClass('filtered');
          }
        })
      });

    });


  };


  function highlight( node ){ // called on a node when it is clicked
    var oldNhood = lastHighlighted;

    var nhood = lastHighlighted = node.closedNeighborhood();
    var others = lastUnhighlighted = cy.elements().not( nhood );

    var reset = function(){
      cy.batch(function(){
        others.addClass('hidden');
        nhood.removeClass('hidden');
        nhood.removeClass('filtered'); // don't filter out nodes in nhood

        allEles.removeClass('faded highlighted');

        nhood.addClass('highlighted');
        others.nodes().forEach(function(n){
           var p = n.data('orgPos');

           n.position({ x: p.x, y: p.y });
        });

      });

      return Promise.resolve().then(function(){
        if( isDirty() ){
          return fit();
        } else {
          return Promise.resolve();
        };
      }).then(function(){
        return Promise.delay( aniDur );
      });
    };

    var runLayout = function(){
      var p = node.data('orgPos');

      var l = nhood.filter(':visible').makeLayout({
        name: 'concentric',
        minNodeSpacing: 0.1,
        fit: false,
        animate: true,
        animationDuration: aniDur,
        animationEasing: easing,
        boundingBox: {
          x1: p.x - 1,
          x2: p.x + 1,
          y1: p.y - 1,
          y2: p.y + 1
        },
        avoidOverlap: true,
        concentric: function( ele ){
          if( ele.same( node ) ){
            return 2;
          } else {
            return 1;
          }
        },
        levelWidth: function(){ return 1; },
        padding: layoutPadding
      });

      var promise = cy.promiseOn('layoutstop');

      l.run();

      return promise;
    };

    var fit = function(){
      return cy.animation({
        fit: {
          eles: nhood.filter(':visible'),
          padding: layoutPadding
        },
        easing: easing,
        duration: aniDur
      }).play().promise();
    };

    var showOthersFaded = function(){
      return Promise.delay( 250 ).then(function(){
        cy.batch(function(){
          others.removeClass('hidden').addClass('faded');
        });
      });
    };

    return Promise.resolve()
      .then( reset )
      .then( runLayout )
      .then( fit )
      .then( showOthersFaded )
    ;

  }

  function isDirty(){ // whether a node has been selected/highlighted
    return lastHighlighted != null;
  }
  var restoreElesPositions = function( nhood ){
    return Promise.all( nhood.map(function( ele ){
      var p = ele.data('orgPos');

      return ele.animation({
        position: { x: p.x, y: p.y },
        duration: aniDur,
        easing: easing
      }).play().promise();
    }) );
  };
  function clear( opts ){ // called when nodes are unselected, that is when the background is clicked.
    if( !isDirty() ){ return Promise.resolve(); }

    opts = $.extend({

    }, opts);

    cy.stop();
    allNodes.stop();

    var nhood = lastHighlighted;
    var others = lastUnhighlighted;

    lastHighlighted = lastUnhighlighted = null;

    var hideOthers = function(){
      return Promise.delay( 125 ).then(function(){
        others.addClass('hidden');

        return Promise.delay( 125 );
      });
    };

    var showOthers = function(){
      cy.batch(function(){
        allEles.removeClass('hidden').removeClass('faded');
      });

      return Promise.delay( aniDur );
    };

    var restorePositions = function(){ // not using, using runLayout instead.
      cy.batch(function(){
        others.nodes().forEach(function( n ){
          var p = n.data('orgPos');

          n.position({ x: p.x, y: p.y });
        });
      });

      return restoreElesPositions( nhood.nodes() );
    };

    var runLayout = function(){
      cy.layout(layout_options)

      var promise = cy.promiseOn('layoutstop');
      return promise;
    };

    var resetHighlight = function(){
      nhood.removeClass('highlighted');
    };

    return Promise.resolve()
      .then( applyFilter ) // re-apply filter since connected nodes will have been shown on selection of a node
      .then( resetHighlight )


      .then( hideOthers )

      .then( runLayout ) // rather than restorePositions as it was originally
      .then( showOthers )


    ;
  }

  function showNodeInfo( node ){
    $('#info').html( infoTemplate( node.data() ) ).show();
  }

  function hideNodeInfo(){
    $('#info').hide();
  }

  /// Initialisation of cytoscape
  function initCy( then ){ // expects array of graphP, styleP which are returned asynchronously
    var loading = document.getElementById('loading');
    var expJson = then[0]; // the nodes and edges information
    var styleJson = styleCycssResult = then[1];
    initCyHelper(expJson, styleJson);
  };
  function reloadCy(jsonGraph) { // reload with a different graph
    var loading = document.getElementById('loading');
    initCyHelper(jsonGraph, styleCycssResult);
  }
  function initCyHelper(expJson, styleJson) {
    var elements = expJson.elements;
    loading.classList.add('loaded');
    elements.nodes.forEach(function(n){
           var data = n.data;

           n.data.orgPos = {
             x: n.position.x,
             y: n.position.y
           };
    });

    cy = window.cy = cytoscape({
      container: document.getElementById('cy'),
      layout: { name: 'preset', padding: layoutPadding },
      style: styleJson,
      elements: elements,
      motionBlur: true,
      selectionType: 'single',
      boxSelectionEnabled: false,
      autoungrabify: true
    });
    nodeTypeList.forEach(function(type) { // display filter checkbox if there is an element with that type.
      if (cy.$("node[type='"+type+"']").length > 0) {
        $('#filter-'+type).show();
      }
      else {
        $('#filter-'+type).hide();
      }
    })

    allNodes = cy.nodes();
    allEles = cy.elements();
    /// Events:
    cy.on('free', 'node', function( e ){
      var n = e.cyTarget;
      var p = n.position();

      n.data('orgPos', {
        x: p.x,
        y: p.y
      });
    });

    cy.on('tap', function(){
      $('#search').blur();
    });

    cy.on('select unselect', 'node', _.debounce( function(e){
      var node = cy.$('node:selected');

      if( node.nonempty() ){
        showNodeInfo( node );

        Promise.resolve().then(function(){
          return highlight( node );
        });
      } else {
        hideNodeInfo();
        clear();
      }

    }, 100 ) );

    // I'm overriding the layout with concentric.

    cy.layout( layout_options );
  }

  var lastSearch = '';



  /// stuff to do with getting model list/model from catalogue:

  var hostname = "http://localhost:8080/"
  var mcUserPass = ["admin", "admin"]; // a username-password pair.

  $('#get-model-list').on('click', function () {
    var $btn = $(this); // the button in jquery wrapper
    var originalButtonText = $btn.html();
    $btn.disable(true);
    $btn.html($btn.attr('data-loading-text'));
    var idModelMapP = $.ajax({
      url: hostname+"catalogue/getIdModelMap",
      headers: {
    "Authorization": "Basic " + btoa(mcUserPass[0] + ":" + mcUserPass[1])
},
      type: 'GET',
      dataType: 'json'
    });
    idModelMapP.then(function(idModelMap) { // in case ajax succeeded
      console.log("Succeeded with:")
      console.log(idModelMapP);
      populateModelList(idModelMap);
      $btn.disable(false);
      $btn.html(originalButtonText);
      $btn.notify("List loaded!", "success");
    }, function(failReason){ // in case ajax failed
      console.log("Failed with:");
      console.log(failReason);
      $btn.disable(false);
      $btn.html(originalButtonText);
      $btn.notify("Get Model List failed with status "+failReason.status+": "+failReason.statusText,
      "error");
    })
  });
  jQuery.fn.extend({
    disable: function(state) {
        return this.each(function() {
            var $this = $(this);
            $this.toggleClass('disabled', state);
        });
    }
  });
  var getModelAndDisplay = function(url, jQButton) {
    // get model from URL and display it as graph in cy
    // button passed in to do loading stuff
    var originalButtonText = jQButton.html();
    jQButton.disable(true);
    jQButton.html(jQButton.attr('data-loading-text'));
    var graphP = $.ajax({
      url: url,
      headers: {"Authorization": "Basic " + btoa(mcUserPass[0] + ":" + mcUserPass[1])},
      type: "GET",
      dataType: "json"
    });
    graphP.then(function(resultJson){
      reloadCy(resultJson);
      jQButton.disable(false);
      jQButton.html(originalButtonText);
      jQButton.notify(originalButtonText + " loaded!", "success");
    }, function(failReason) {
      jQButton.disable(false);
      jQButton.html(originalButtonText);
      jQButton.notify("Getting "+originalButtonText+" failed with status "+failReason.status+": "+failReason.statusText,
      "error");
    })
  }
  var populateModelList = function(idModelMap) {
    // idModelMap is an object with keys which are ids of models in catalogue, values which are user-readable names of models.
    var modelListTemplate = Handlebars.compile([
      '{{#each idModelMap}}<a href="#" class="list-group-item" id="get-model-{{@key}}" data-loading-text="<i class=\'fa fa-spinner fa-pulse fa-fw\'></i> Loading {{this}}...">{{this}}</a>{{/each}}' // using the URL as an html id for adding on click behaviour
    ].join(''));
    $('#model-list').html(modelListTemplate({idModelMap: idModelMap})).show()
    $.each(idModelMap, function (idKey, nameValue){
      $('#get-model-'+idKey).on('click', function(){
        getModelAndDisplay(hostname+"catalogue/dataModel/"+idKey+"/cytoscapeJsonExport", $(this))
    })})

  };
  /*
  initialIdModelMap = {
    '4': 'SI',
    '24': 'Java',
    '33': 'XMLSchema',
    '69': 'SI Derived Units',
    '120': 'Cancer Outcomes and Services Dataset',
    '1297': 'NHIC ACS',
    '1576': 'NHIC HEP',
    '2265': 'NHIC ICU',
    '2943': 'NHIC TRA',
    '3404': 'PDS',
    '3838': 'NHIC - Ovarian Cancer',
    '8714': 'Rare Diseases',
    '11466': 'HPO',
    '40529': 'Illumina Sample Tracking Data',
    '40575': 'Rare Disease Conditions',
    '42010': 'NHS Data Dictionary GEL Subset',
    '42350': 'Rare Disease Sample Tracking',
    '42391': 'Cancer Model',
    '42917': 'Cancer Sample Tracking GeL GMC',
    '43807': 'Genomics England Shared',
    '44468': 'Interim Rare Disease Model for Scotland and Northern Ireland'};
  populateModelList(initialIdModelMap);
  */
  // initialise from catalogue:
  (function () {
      var idModelMapP = $.ajax({
          url: hostname+"catalogue/getIdModelMap",
          headers: {
              "Authorization": "Basic " + btoa(mcUserPass[0] + ":" + mcUserPass[1])
          },
          type: 'GET',
          dataType: 'json'
      });
      idModelMapP.then(function(idModelMap) { // in case ajax succeeded
          console.log("Succeeded with:")
          console.log(idModelMapP);
          populateModelList(idModelMap);
      }, function(failReason){ // in case ajax failed
          console.log("Failed with:");
          console.log(failReason);
      })
  })();

  /// Search bar and button on event behaviour:
  $('#search').typeahead({
    minLength: 2,
    highlight: true,
  },
  {
    name: 'search-dataset',
    source: function( query, cb ){
      function matches( str, q ){
        str = (str || '').toLowerCase();
        q = (q || '').toLowerCase();

        return str.match( q );
      }

      var fields = ['name', 'NodeType', 'Country', 'Type', 'Milk'];

      function anyFieldMatches( n ){
        for( var i = 0; i < fields.length; i++ ){
          var f = fields[i];

          if( matches( n.data(f), query ) ){
            return true;
          }
        }

        return false;
      }

      function getData(n){
        var data = n.data();

        return data;
      }

      function sortByName(n1, n2){
        if( n1.data('name') < n2.data('name') ){
          return -1;
        } else if( n1.data('name') > n2.data('name') ){
          return 1;
        }

        return 0;
      }

      var res = allNodes.stdFilter( anyFieldMatches ).sort( sortByName ).map( getData );

      cb( res );
    },
    templates: {
      suggestion: infoTemplate
    }
  }).on('typeahead:selected', function(e, entry, dataset){
    var n = cy.getElementById(entry.id);

    cy.batch(function(){
      allNodes.unselect();

      n.select();
    });

    showNodeInfo( n );
  }).on('keydown keypress keyup change', _.debounce(function(e){
    var thisSearch = $('#search').val();

    if( thisSearch !== lastSearch ){
      $('.tt-dropdown-menu').scrollTop(0);

      lastSearch = thisSearch;
    }
  }, 50));

  $('#reset').on('click', function(){
    if( isDirty() ){
      clear();
    } else {
      allNodes.unselect();

      hideNodeInfo();

      cy.stop();

      cy.animation({
        fit: {
          eles: cy.elements(),
          padding: layoutPadding
        },
        duration: aniDur,
        easing: easing
      }).play();
    }
  });

  $('#filters').on('click', 'input', applyFilter);

  $('#filter').qtip({
    position: {
      my: 'top center',
      at: 'bottom center',
      adjust: {
        method: 'shift'
      },
      viewport: true
    },

    show: {
      event: 'click'
    },

    hide: {
      event: 'unfocus'
    },

    style: {
      classes: 'qtip-bootstrap qtip-filters',
      tip: {
        width: 16,
        height: 8
      }
    },

    content: $('#filters')
  });

  $('#about').qtip({
    position: {
      my: 'bottom center',
      at: 'top center',
      adjust: {
        method: 'shift'
      },
      viewport: true
    },

    show: {
      event: 'click'
    },

    hide: {
      event: 'unfocus'
    },


    style: {
      classes: 'qtip-bootstrap qtip-about',
      tip: {
        width: 16,
        height: 8
      }
    },

    content: $('#about-content')
  });
});
