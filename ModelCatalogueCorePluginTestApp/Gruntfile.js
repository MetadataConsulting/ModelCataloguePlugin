module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    javascripts: 'grails-app/assets/javascripts',
    angular_architecture_graph: {
      diagram: {
        files: {
          // outputs to build/architecture
          "build/architecture": [
            //"<%= javascripts %>/modelcatalogue/**/*.js",
            //"<%= javascripts %>/modelcatalogue/**/*.coffee"
            "build/js/**/*.js"
          ]

        }
      }
    }
  });

  // adds angular_architecture_graph task
  // see https://github.com/lucalanca/grunt-angular-architecture-graph
  // for instructions (including Grunt)
  grunt.loadNpmTasks('grunt-angular-architecture-graph')
  // before doing this, compile the coffeescript to build/js where the graph tool will pick it up (coffee -o build/js -cw sourceDir) (you can npm install coffee-script@12.7.1), and also for completeness, though there aren't many, copy the .js files, and transpile the es6 files into there.

  // Default task(s).
  // grunt.registerTask('default', ['angular_architecture_task']);

};
