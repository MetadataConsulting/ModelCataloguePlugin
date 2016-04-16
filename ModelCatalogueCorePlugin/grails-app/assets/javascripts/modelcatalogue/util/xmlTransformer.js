(function(window){
    var Saxon;

    window.onSaxonLoad = function() {
        Saxon = window.Saxon
    };

    angular.module("mc.util.xsltTransformer", []).factory('xsltTransformer', function($q, $log, $window, vkbeautify) {

        function XsltTransformer() {
            var SaxonPromise;

            SaxonPromise = $q(function(resolve){
                if (Saxon) {
                    $log.debug('Saxon resolved from local variable');
                    return resolve(Saxon)
                }

                if ($window.Saxon) {
                    $log.debug('Saxon resolved from Window variable');
                    return $window.Saxon
                }

                $window.onSaxonLoad = function() {
                    $log.debug('Saxon resolved with another onSaxonLoad callback');
                    resolve($window.Saxon);
                };
            });

            /**
             * Transforms the sourceText XML using the stylesheet provided by stylesheetText.
             *
             * If you need additional configuration of the Saxon's XSLT20Processor use xsltProcessorConfigurer.
             *
             * @param {string} sourceText
             * @param {string} stylesheetText
             * @param {xsltProcessorConfigurerCallback} [xsltProcessorConfigurer=noop] - Function providing additional configuration for the XSLT20Processor
             * @returns {Promise} Promise which resolves to transformed XML text and rejected if the transformation fails
             */
            this.transformXml = function(sourceText, stylesheetText, xsltProcessorConfigurer) {
                return $q(function (resolve,reject) {
                    SaxonPromise.then(function(Saxon) {
                        var processor;

                        Saxon.setErrorHandler(function(error){
                            reject(error)
                        });

                        processor = Saxon.newXSLT20Processor(Saxon.parseXML(stylesheetText));

                        if (xsltProcessorConfigurer) {
                            xsltProcessorConfigurer(processor);
                        }

                        processor.setSuccess(function(proc){
                            var document = proc.getResultDocument();
                            resolve(vkbeautify.xml(Saxon.serializeXML(document), 2));
                        });

                        processor.transformToDocument(Saxon.parseXML(sourceText));
                    });
                });
            }
        }

        return new XsltTransformer();
    });

    /**
     * Function provided to configure XSLT20Processor
     * @callback xsltProcessorConfigurerCallback
     * @param {XSLT20Processor} processor - The processor to be configured
     */

})(window);
