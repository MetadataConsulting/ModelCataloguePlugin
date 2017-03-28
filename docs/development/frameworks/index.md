# Technology Stack
Following listing can help you find the documentation for the tools, frameworks and libraries in use.

## Dependencies
Following dependecies are managed automatically if using [Model Catalogue with Docker]((https://github.com/MetadataRegistry/registry/)) (except Discourse)
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Tomcat 8.x](https://tomcat.apache.org/download-80.cgi)
* [MySQL 5.6+](https://www.mysql.com/)
* [ElasticSearch 2.0+](https://www.elastic.co/products/elasticsearch) ([additional notes](../../deployment/elasticsearch.md))
* [Discourse](https://www.discourse.org/) (optional for comments)

## Frontend

### Tools
* [NodeJS](https://nodejs.org/en/) ecosystem (tested with version 0.10.x)
* [NPM](https://www.npmjs.com/) for managing tools dependencies
* [Bower](http://bower.io/) for managing frontend dependencies
* [Karma Test Runner](http://karma-runner.github.io/) for running JavaScript tests

See [package.json](../../../ModelCatalogueCorePlugin/package.json) for information on versions

### Languages
Apart from standard HTML + CSS + JS stack we use following languages in the frontend:
* [CoffeeScript](http://coffeescript.org/) as alternative to JavaScript
* [LESS](http://lesscss.org/) as alternative to CSS

### Polyfills
Polyfills grants access to features which might not be available in older browser
* ES5 and ES6 features covered by via [core-js](https://github.com/zloirock/core-js)
* [Blob](https://developer.mozilla.org/en-US/docs/Web/API/Blob) via [Blob.js](https://github.com/eligrey/Blob.js/)
* [Window.saveAs](https://dev.w3.org/2009/dap/file-system/file-writer.html) via [FileSaver.js](https://github.com/eligrey/FileSaver.js)

### Frameworks and Libraries
* [Bootstrap](http://getbootstrap.com/) as the base CSS framework
* [FontAwesome](http://fontawesome.io/) for application icons
* [AngularJS](https://angularjs.org/) as the base JavaScript framework
* [jQuery](https://jquery.com/) is not used directly but as an base of other framework
* [jQuery UI](https://jqueryui.com/) for dragging and resizable support
* [Angular UI Bootstrap](http://angular-ui.github.io/bootstrap/versioned-docs/0.13.4/) as a bridge between AngularJS and Bootstrap
* [Angular UI Router](http://angular-ui.github.io/ui-router/site/#/api/ui.router) for URL routing the application
* [NG File Upload](https://github.com/danialfarid/ng-file-upload) for uploading assets
* [Angular Loading Bar](http://chieffancypants.github.io/angular-loading-bar/) for showing the network progress
* [Angular Auth Interceptor](https://github.com/witoldsz/angular-http-auth) for seamless authentication
* [Angular File Saver](https://github.com/alferov/angular-file-saver) for storing content to the client
* [RxJS](https://github.com/Reactive-Extensions/RxJS) ([ReactiveX](http://reactivex.io/)) for event handling (waiting for wider adoption through codebase)
* [Rx Angular](https://github.com/Reactive-Extensions/rx.angular.js/) as a bridge between AngularJS and RxJS
* [STOMP](http://jmesnil.net/stomp-websocket/doc/) over [SockJS](http://sockjs.org) for server push notification
* [SaxonCE](http://www.saxonica.com/ce/index.xml) for client side XSLT 2.0 transformations
* [vkBeautify](http://www.eslinstructor.net/vkbeautify/) for formatiing generated XML on client side

See [bower.json](../../../ModelCatalogueCorePlugin/bower.json) for information on versions

## Backend

### Frameworks and Libraries
* [Grails 2.5.x](http://grails.github.io/grails-doc/2.5.x/) ecosystem
* [Grails Asset Pipeline](https://grails.org/plugin/asset-pipeline) for handling the frontend resources
* [Grails Spring Security Core 2.0](https://grails.org/plugin/spring-security-core) for securing the application
* [Grails Spring Security Core UI](https://grails.org/plugin/spring-security-ui) for managing the application users
* [Grails Database Migration](https://grails.org/plugin/database-migration) (wrapper around [Liquibase](http://www.liquibase.org/)) for database migrations
* [Grails Executor](https://grails.org/plugin/executor) for executing asynchronous code ([additional notes](../executor_service.md))
* [Guava](https://github.com/google/guava) for in memory caching and immutable collections
* [RxJava](https://github.com/ReactiveX/RxJava) ([ReactiveX](http://reactivex.io/)) for easier event/stream based programming (waiting for wider adoption through codebase, currently used mostly with search indexing) ([additional notes](rxjava.md))
* [Document Builder](https://github.com/craigburke/document-builder) for MS Word exports
* [Spreadsheet Builder](http://metadataregistry.github.io/spreadsheet-builder/) for MS Excel exports
* [OpenClinica Case Report Form Builder](https://github.com/MetadataRegistry/crf-builder) for OpenClinica forms export
* [Owlapi](https://github.com/owlcs/owlapi/) for parsing OBO files
* [ElasticSearch Java API](https://www.elastic.co/guide/en/elasticsearch/client/java-api/current/index.html) for indexing and searching catalogue
* [Spock](https://spockframework.github.io/spock/docs/1.0/) for general unit testing
* [Geb](http://www.gebish.org/) for browser based testing ([additional notes](geb.md))

See particular `grails-app/config/BuildConfig.groovy` files in Grails plugins folders for information on versions



