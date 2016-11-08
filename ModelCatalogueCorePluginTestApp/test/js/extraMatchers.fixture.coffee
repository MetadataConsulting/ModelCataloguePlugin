###
Adds extra matchers to all Jasmine spec.
This script is guaranteed to be executed by Karma before any other specification script
because it has .fixture.coffee extension
###
beforeEach ->
  @addMatchers {
    ###
      Evaluates to true if the item under test is a function.
    ###
    toBeFunction: () ->
      @message = () -> "Expected #{@actual} to be a function but it wasn't"
      angular.isFunction(@actual)

    ###
      Evaluates to true if the item under test is an object.
    ###
    toBeObject: () ->
      @message = () -> "Expected #{@actual} to be an object but it wasn't"
      angular.isObject(@actual)

    ###
      Evaluates to true if the item under test is an array.
    ###
    toBeArray: () ->
      @message = () -> "Expected #{@actual} to be an array but it wasn't"
      angular.isArray(@actual)
  }