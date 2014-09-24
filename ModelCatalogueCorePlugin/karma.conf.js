// an example karma.conf.js
module.exports = function(config) {
    config.set({
        basePath: '.',
        frameworks: ['jasmine'],

        browsers: [
            'Chrome'
            // 'Firefox', // Firefox is slow!
            // 'Safari',  // Safari keeps old tabs open causing testing multiple times
        ],
        reporters: ['progress', 'junit', 'coverage', 'osx'],
        singleRun: false,
        autoWatch : true,

        coverageReporter: {
            type: 'lcovonly',
            dir: 'target/reports/js/coverage/'
        },

        junitReporter: {
            outputFile: 'target/reports/js/karma-test-results.xml',
            suite: 'unit'
        },

        files: [
            // Required libraries
            'grails-app/assets/bower_components/jquery/dist/jquery.js',
            'grails-app/assets/bower_components/angular/angular.js',
            'grails-app/assets/bower_components/angular-cookies/angular-cookies.js',
            'grails-app/assets/bower_components/angular-sanitize/angular-sanitize.js',
            'grails-app/assets/bower_components/angular-animate/angular-animate.js',
            'grails-app/assets/bower_components/angular-http-auth/src/http-auth-interceptor.js',
            'grails-app/assets/bower_components/angular-ui-router/release/angular-ui-router.js',
            'grails-app/assets/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',

            // App under test
            'grails-app/assets/javascripts/**/*.coffee',
            'grails-app/assets/javascripts/**/*.js',

            // Tests
            'grails-app/assets/bower_components/angular-mocks/angular-mocks.js',

            'test/js/**/*.fixture.js',
            'test/js/**/*.fixture.coffee',
            'test/js/**/*.!(fixture.)js',
            'test/js/**/*.!(fixture.)coffee'
        ],
        exclude: [
        ],

        plugins: [
            'karma-coverage',
            'karma-jasmine',
            'karma-chrome-launcher',
            'karma-safari-launcher',
            'karma-junit-reporter',
            'karma-osx-reporter',
            'karma-coffee-preprocessor'
        ],

        preprocessors: {
            '**/*.coffee': ['coffee']
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
        }
    });
};