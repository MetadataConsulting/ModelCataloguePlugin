Model Catalogue Grails Plugin
====================
[![Build Status](https://travis-ci.org/MetadataRegistry/ModelCataloguePlugin.svg?branch=develop)](https://travis-ci.org/MetadataRegistry/ModelCataloguePlugin) [![Gitter](https://badges.gitter.im/Join Chat.svg)] (https://gitter.im/MetadataRegistry/ModelCataloguePlugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

To use the model catalogue core plugin in your grails app, include the following in your BuildConfig.groovy:

```
repositories {	 
    mavenRepo 'http://dl.bintray.com/modelcatalogue-core/ModelCatalogueCorePlugin'
}
```

and 

```
plugins{ 
    compile "org.modelcatalogue.plugins:grails-model-catalogue-core-plugin:<version>"
}
```

[Plugin Documentation](https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/javadoc/)


## Running the Standalone Application

You can run the model catalogue application standalone either for development purposes or to setup your own metadata
registry.


### Requirements

To run application locally you need following installed on your machine 

  * [Java](https://java.com/en/download/)
  * [NodeJS](https://nodejs.org/download/)
  * [Git](http://git-scm.com/) (Alternatively you can download the latest snapshot [in ZIP archive](https://github.com/MetadataRegistry/ModelCataloguePlugin/archive/develop.zip)) 
  
To verify you have these requirements installed try following commands in the terminal:
 
```
java -version
node --version
npm --version # part of NodeJS
git --version
```

All tools should return their versions.
  

### Launching the application

Model Catalogue provides rich web client which requires some other tools to be installed before launching the application.
You need to install required NodeJS tools and dependencies for the JavaScript frontend. 

```
git clone https://github.com/MetadataRegistry/ModelCataloguePlugin.git
cd ModelCataloguePlugin
cd ModelCatalogueCorePlugin
npm install
bower install

```

This will download all the necessary JavaScript sources. Than you can run the standalone application

```
cd ..
cd ModelCatalogueCorePluginTestApp
./grailsw run-app
```

This will launch the application in development mode with in memory database. If you want to run the application
against persistent database, you need to override the configuration according to [Grails database configuration docs](http://grails.github.io/grails-doc/2.4.4/guide/conf.html#dataSource)

```
open ModelCataloguePlugin/ModelCatalogueCorePluginTestApp/grails-app/conf/DataSource.groovy
```

There are three default users available

Username      | Description 
------------- | -------------  
viewer        | User with role `VIEWER` can only browse the catalogue without any   
curator       | User with role `CURATOR` has most of the privileges except editing relationship types and user management
admin         | User with role `ADMIN`  has all the privileges

The users comes with passwords which are by default same as the username. You should change them in the `Bootstrap.groovy` file.

```
open ModelCataloguePlugin/ModelCatalogueCorePluginTestApp/grails-app/conf/BootStrap.groovy
```

You find the code where new users are crated around line 40 so change the `password: 'xyz'` definitions to the more
solid passwords.


When everything set up you can run the application with `./grailsw prod run-app`. You can also create a WAR file running 
`./grailsw war`and than deploy the WAR file located under the `target` folder to your web server such as Tomcat.

When application has started you can navigate to your browser to `http://localhost:8080/ModelCatalogueCorePluginTestApp`
and use one of the predefined users and start using the catalogue.











