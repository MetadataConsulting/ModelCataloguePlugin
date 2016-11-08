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

                            element.on('resizestart', function () {
                                // stores the width with padding as when setting back the padding is stripped out (setting with
                                // outerWidth does not help at all)
                                // this is probably bug in jQuery and there should be a mechanism to check if it isn't fix yet
                                jQuery(opts.mirror).data('resizestartwidth', jQuery(opts.mirror).outerWidth())
                            });
                            element.on('resize', function (event, ui) {
                                var delta, newWidth;
                                delta = ui.originalSize.width - ui.size.width;
                                if (!delta) {
                                    event.preventDefault();
                                    event.stopPropagation();
                                    return false;
                                }
                                newWidth = jQuery(opts.mirror).data('resizestartwidth') + delta - 1;

                                jQuery(opts.mirror).width(newWidth);
                                jQuery(opts.mirror).offset({left: ui.size.width});

                                $rootScope.$broadcast('infiniteTableRedraw');

                                event.stopPropagation();
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