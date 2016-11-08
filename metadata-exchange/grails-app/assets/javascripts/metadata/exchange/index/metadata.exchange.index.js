//= wrapped
//= require /angular/angular
//= require /angular/ui-bootstrap-tpls
//= require /angular/angular-ui-router
//= require_self
//= require_tree services
//= require_tree controllers
//= require_tree directives
//= require_tree templates

angular.module("metadata.exchange.index", [
    "metadata.exchange.core",
    "ui.bootstrap.dropdown",
    "ui.bootstrap.collapse",
    "ui.router"
])
.config(config);

function config($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('index', {
            url: "/",
            templateUrl: "/metadata/exchange/index/index.html"
        });

    $urlRouterProvider.otherwise('/');
}
