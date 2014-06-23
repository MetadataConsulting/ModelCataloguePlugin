ModelCataloguePlugin
====================

Model Catalogue Grails Plugin [![Build Status](https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/badge/icon)](https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/)

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
