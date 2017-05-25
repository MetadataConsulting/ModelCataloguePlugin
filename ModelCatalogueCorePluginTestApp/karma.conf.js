// an example karma.conf.js. Karma is a JS library for testing
module.exports = function(config) {
    config.set({
        basePath: '.',
        frameworks: ['jasmine'],

        browsers: [
            'Chrome',
            'Firefox'
        ],
        reporters: ['progress', 'junit', 'coverage'],
        singleRun: true,

        coverageReporter: {
            type: 'lcovonly',
            dir: 'target/test-reports-js/coverage/'
        },

        junitReporter: {
            outputDir: 'target/test-reports-js',
            suite: 'unit'
        },

        files: [
            // Required libraries
            'grails-app/assets/bower_components/core.js/client/core.js',
            'web-app/js/libs/saxonce/Saxonce.nocache.js',
            'grails-app/assets/bower_components/rxjs/dist/rx.all.js',
            'grails-app/assets/bower_components/blob-polyfill/Blob.js',
            'grails-app/assets/bower_components/file-saver.js/FileSaver.js',
            'grails-app/assets/bower_components/ace-builds/src-min-noconflict/ace.js',
            'grails-app/assets/bower_components/vkbeautify/vkbeautify.js',
            'grails-app/assets/bower_components/jquery/dist/jquery.js',
            'grails-app/assets/bower_components/angular/angular.js',
            'grails-app/assets/bower_components/angular-cookies/angular-cookies.js',
            'grails-app/assets/bower_components/angular-sanitize/angular-sanitize.js',
            'grails-app/assets/bower_components/angular-animate/angular-animate.js',
            'grails-app/assets/bower_components/angular-http-auth/src/http-auth-interceptor.js',
            'grails-app/assets/bower_components/angular-ui-router/release/angular-ui-router.js',
            'grails-app/assets/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'grails-app/assets/bower_components/angular-rx/dist/rx.angular.js',
            'grails-app/assets/bower_components/angular-ui-ace/ui-ace.js',
            'grails-app/assets/bower_components/angular-file-saver/dist/angular-file-saver.js',
            'grails-app/assets/bower_components/sly-repeat/scalyr.js',

            'web-app/js/libs/google-diff-match-patch/javascript/diff_match_patch.js',

            // Tests Helpers
            'test/js/**/*.fixture.js',
            'test/js/**/*.fixture.coffee',

            // App under test
            'grails-app/assets/javascripts/modelcatalogue/modelcatalogue.coffee',
            'grails-app/assets/javascripts/**/*.es6',
            'grails-app/assets/javascripts/**/*.coffee',
            'grails-app/assets/javascripts/**/*.js',
            'grails-app/assets/javascripts/**/*.tpl.html',

            // Angular Mock
            'grails-app/assets/bower_components/angular-mocks/angular-mocks.js',

            // Tests
            'test/js/**/*.!(fixture.)es6',
            'test/js/**/*.!(fixture.)js',
            'test/js/**/*.!(fixture.)coffee'
        ],
        exclude: [
        ],

        plugins: [
            'karma-coverage',
            'karma-jasmine',
            'karma-chrome-launcher',
            'karma-firefox-launcher',
            'karma-junit-reporter',
            'karma-coffee-preprocessor',
            'karma-ng-html2js-preprocessor',
            'karma-babel-preprocessor'
        ],

        preprocessors: {
            '**/*.coffee': ['coffee'],
            '**/*.html': ['ng-html2js'],
            '**/*.es6': ['babel']
        },

        coffeePreprocessor: {
            // options passed to the coffee compiler
            options: {
                bare: true,
                sourceMap: true
            },
            // transforming the filenames
            transformPath: function(path) {
                return path.replace(/\.js$/, '.coffee');
            }
        },
        ngHtml2JsPreprocessor: {
            cacheIdFromPath: function(filepath) {
                // example strips 'public/' from anywhere in the path
                // module(app/templates/template.html) => app/public/templates/template.html
                return filepath.replace('grails-app/assets/javascripts', '').replace('templates/', '').replace('.tpl', '');
            },

            // - setting this option will create only a single module that contains templates
            //   from all the files, so you can load them all with module('foo')
            // - you may provide a function(htmlPath, originalPath) instead of a string
            //   if you'd like to generate modules dynamically
            //   htmlPath is a originalPath stripped and/or prepended
            //   with all provided suffixes and prefixes
            moduleName: function(htmlPath) {
                return htmlPath.substring(1, htmlPath.lastIndexOf('/')).replace(/\//g,'.');
            }
        },
        babelPreprocessor: {
            options: {
                presets: ['es2015'],
                sourceMap: 'inline'
            },
            filename: function (file) {
                return file.originalPath.replace(/\.es6$/, '.es5.js');
            },
            sourceFileName: function (file) {
                return file.originalPath;
            }
        }
    });
};
