Model Catalogue Grails Plugin
====================
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/MetadataRegistry/ModelCataloguePlugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Build Status](https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/badge/icon)](https://metadata.ci.cloudbees.com/job/ModelCatalogueCorePluginDevelop/) [![Stories in progress](https://badge.waffle.io/metadataregistry/modelcatalogueplugin.png?label=in+progress&title=In+Progress)](http://waffle.io/metadataregistry/modelcatalogueplugin)

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


