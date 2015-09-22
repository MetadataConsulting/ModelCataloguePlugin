(function(window, angular) {
'use strict';
/**
 * Extremly simplified https://github.com/angular-ui/ui-sortable
 */
angular.module('mc.util.ui.resizable', [])
  .value('resizableConfig',{})
  .directive('resizable', [
    'resizableConfig', '$timeout', '$log', '$window',
    function(resizableConfig, $timeout, $log, $window) {
      return {
        scope: {
          resizable: '='
        },
        link: function(scope, element) {
          var opts = {};

          angular.extend(opts, resizableConfig, scope.resizable);

          if (!angular.element.fn || !angular.element.fn.jquery) {
            $log.error('resizable: jQuery should be included before AngularJS!');
            return;
          }

          // Create resizable
          element.resizable(opts);



          if (opts.mirror) {
              element.on('resizestart', function(){
                  // stores the width with padding as when setting back the padding is stripped out (setting with
                  // outerWidth does not help at all)
                  // this is probably bug in jQuery and there should be a mechanism to check if it isn't fix yet
                  jQuery(opts.mirror).data('resizestartwidth', jQuery(opts.mirror).outerWidth())
              });
              element.on('resize', function(event, ui){
                  var delta;
                  delta = ui.originalSize.width - ui.size.width;
                  if (!delta) {
                      return;
                  }
                  jQuery(opts.mirror).width(jQuery(opts.mirror).data('resizestartwidth') + delta - 1);
                  event.stopPropagation();
              });
              jQuery($window).on('resize', function(){
                  var windowWidth = jQuery($window).innerWidth(), elementWidth = element.outerWidth();
                  jQuery(opts.mirror).width(windowWidth - elementWidth - (opts.windowWidthCorrection ? opts.windowWidthCorrection : 1));
              })
          }
        }
      };
    }
  ]);

})(window, window.angular);