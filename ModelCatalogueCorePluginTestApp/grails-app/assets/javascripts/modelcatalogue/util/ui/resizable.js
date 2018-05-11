(function (window, angular) {
    'use strict';

    /**
     * Extremly simplified https://github.com/angular-ui/ui-sortable
     */
    angular.module('mc.util.ui.resizable', [])
        .value('resizableConfig', {})
        .directive('resizable', [
            'resizableConfig', '$timeout', '$log', '$window', '$rootScope',
            function (resizableConfig, $timeout, $log, $window, $rootScope) {
                return {
                    scope: {
                        resizable: '='
                    },
                    link: function (scope, element) {
                        var opts = {}, setOption, recalculateWidths, getAbsoluteWidth, breakIfNeeded;

                        getAbsoluteWidth = function (widthInPercents, parentElement) {
                            return parentElement.innerWidth() * widthInPercents / 100
                        };

                        setOption = function (opts, option, value) {
                            // always set the value in opts for better accessibility
                            opts[option] = value;
                            // in case the resizable is already initialized, update the option directly
                            if (element.resizable('instance')) {
                                element.resizable('option', option, value);
                            }
                        };

                        recalculateWidths = function (opts) {
                            if (opts.minWidthPct) {
                                setOption(opts, 'minWidth', getAbsoluteWidth(opts.minWidthPct, element.parent()));
                            }

                            if (opts.maxWidthPct) {
                                setOption(opts, 'maxWidth', getAbsoluteWidth(opts.maxWidthPct, element.parent()));
                            }
                        };

                        breakIfNeeded = function(opts) {
                            var windowWidth = jQuery($window).width();

                            if (!opts.breakWidth || !opts.mirror) {
                                element.removeClass('narrow-screen');
                                return false;
                            }
                            if (windowWidth > opts.breakWidth) {
                                element.removeClass('narrow-screen');
                                return false;
                            }

                            jQuery(opts.mirror).width(windowWidth - (opts.windowWidthCorrection ? opts.windowWidthCorrection : 1));
                            element.width(windowWidth - (opts.windowWidthCorrection ? opts.windowWidthCorrection : 1));
                            element.addClass('narrow-screen');
                            return true;
                        };

                        angular.extend(opts, resizableConfig, scope.resizable);

                        if (!angular.element.fn || !angular.element.fn.jquery) {
                            $log.error('resizable: jQuery should be included before AngularJS!');
                            return;
                        }

                        recalculateWidths(opts);

                        // Create resizable
                        element.resizable(opts);

                        if (opts.mirror) {
                            breakIfNeeded(opts);

                            element.on('resize', function (event, ui) {
                                var leftMargin = 30; // this should be picked up from the style sheet really
                                var parent = ui.element.parent();
                                var parentWidth = parent.width(); // ui.element.parent().width();
                                var remainingSpace = parentWidth - ui.element.outerWidth();
                                var minWidth = parent.innerWidth() * opts.minWidthPct / 100;

                                if (remainingSpace < minWidth){
                                    ui.element.width((parentWidth - minWidth)/parentWidth * 100 + "%");
                                    remainingSpace = minWidth;
                                }
                                var detailPane = jQuery(opts.mirror);
                                var detailPaneWidth = (remainingSpace - (detailPane.outerWidth() - detailPane.width()) - leftMargin) / parentWidth * 100 + "%";
                                var detailPaneLeft = ui.size.width + leftMargin;
                                // console.log("parentWidth:" + parentWidth + " remainingSpace:" + remainingSpace + " minWidth:" + minWidth + " detailWidth:" + detailPaneWidth + " detailLeft:" + detailPaneLeft);
                                detailPane.width(detailPaneWidth);
                                detailPane.offset({left: detailPaneLeft});

                                $rootScope.$broadcast('infiniteTableRedraw');
                                event.stopPropagation(); // not sure whether we need this?
                            });
                            jQuery($window).on('resize', function () {
                                var parentWidth, elementWidth, newWidth;

                                if (breakIfNeeded(opts)) {
                                    return;
                                }

                                recalculateWidths(opts);

                                parentWidth = element.parent().innerWidth();
                                elementWidth = element.outerWidth();

                                if (opts.minWidth && elementWidth < opts.minWidth) {
                                    element.width(opts.minWidth);
                                    elementWidth = element.outerWidth();
                                } else if (opts.maxWidth && elementWidth > opts.maxWidth) {
                                    element.width(opts.maxWidth);
                                    elementWidth = element.outerWidth();
                                }

                                newWidth = parentWidth - elementWidth - (opts.parentWidthCorrection ? opts.parentWidthCorrection : 1);

                                jQuery(opts.mirror).width(newWidth);
                                jQuery(opts.mirror).offset({left: elementWidth})
                            })
                        }
                    }
                };
            }
        ]);

})(window, window.angular);
