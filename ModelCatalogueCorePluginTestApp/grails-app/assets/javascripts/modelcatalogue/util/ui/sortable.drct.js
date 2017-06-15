(function(window, angular) {
'use strict';
/**
 * Extremly simplified https://github.com/angular-ui/ui-sortable
 */
angular.module('mc.util.ui.sortable', [])
  .value('sortableConfig',{})
  .directive('sortable', [
    'sortableConfig', '$timeout', '$log',
    function(sortableConfig, $timeout, $log) {
      return {
        scope: {
          sortable: '='
        },
        link: function(scope, element) {
          var opts = {};

          angular.extend(opts, sortableConfig, scope.sortable);

          if (!angular.element.fn || !angular.element.fn.jquery) {
            $log.error('sortable: jQuery should be included before AngularJS!');
            return;
          }

          // Create sortable
          element.sortable(opts);
        }
      };
    }
  ]);

})(window, window.angular);