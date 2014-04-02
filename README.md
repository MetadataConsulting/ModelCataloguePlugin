ModelCataloguePlugin
====================

Model Catalogue Grails Plugin:

Grails version 2.3.7

To use the model catalogue core plugin and the model catalogue elastic search plugin in your grails app, include the following in your BuildConfig.groovy:

```
repositories {	 
		mavenRepo 'http://dl.bintray.com/modelcatalogue-core/ModelCatalogueCorePlugin/'
	        mavenRepo 'http://dl.bintray.com/modelcatalogue-core/ModelCatalogueElasticSearchPlugin/'
	}
```

and 

```
plugins{ 
		compile "org.modelcatalogue.plugins:grails-model-catalogue-core-plugin:0.1"
        	compile "org.modelcatalogue.plugins:grails-model-catalogue-elastic-search-plugin:0.1"
	}
```

documentation is available:

```
https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/javadoc/
```
