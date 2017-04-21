Model Catalogue
===============
[![Build Status](https://travis-ci.org/MetadataConsulting/ModelCataloguePlugin.svg?branch=2.x)](https://travis-ci.org/MetadataConsulting/ModelCataloguePlugin)

## Introduction

This introduction is taken from the [Wiki on Confluence](https://metadata.atlassian.net/wiki/display/ME/Metadata+Exchange+Home) which also includes instructions for users of the catalogue.
The Model Catalogue Plugin (Mx) is a web-based toolkit for managing metadata.
The Model Catalogue Plugin is designed to enable the easy conformance of information systems with prevailing data standards and best practise.
It was originally an experimental metadata registry which was used to support an NHIC project in 2013, and later on to support clinicians at Genomics England in building and curating datasets. The Model Catalogue Plugin is an open source project, available under an Apache 2  license.  It has a modular architecture, and the core component is a dataset or "Models Catalogue", which provides a singular reference point for the creation and curation of datasets or data models.
The project is aimed at automating and simplifying data and metadata management.  In particular the MC allows Data architects, Business Analysts, Scientists to centrally define datasets, formatting, business-rules, and metadata.  These models and datasets can then be used across a large user bases, inside or outside the organization, by developers to build conformant applications and to verify datasets. Different models or components can be compared, analysed and matched, data elements can be identified for tagging, for security purposes and linked to existing artefacts, such as databases or applications.
The core ideas embedded in the Model Catalogue Plugin are inspired by model driven engineering (MDE) principles, and initially the design was based around ISO 11179. The design has since been refined to provide a more general and user-friendly capability.
The core registry and catalogue is built using Angular 1, Bootstrap and Coffeescript on the front-end user interface, powered by a grails framework and a relational (in most cases MySQL) database on the backend. However we are currently researching the use of graph databases, in particular triple stores for usage on the backend.
The Model Catalogue Plugin  allows users to define datasets as general platform-independent models or to specify more specific models as required, the core model can be output as XML, and transformed to an XML Schema, or as an Excel spreadsheet, or it has a REST-interface which can be accessed over the web to discover data elements and perform verification against the datasets as required.

## Deployment

 * [Running Model Catalogue with Docker](https://github.com/MetadataConsulting/registry/)
 * [Running Model Catalogue on AWS](docs/deployment/aws.md)
 * [Configuration for Production](docs/deployment/production.adoc)
 * [Environment](docs/deployment/environment.adoc)
 * [Using Grails Console in Production](docs/development/frameworks/grails_console.md)

## Development

Note from James:

There are a number of misnomers in this project, first of all the project itself being called ModelCataloguePlugin; secondly, the actual (Grails) app is in the subdirectory called ModelCatalogueCorePluginTestApp, and it is neither a test nor a plugin. All development, including gradle running, should be done in ModelCatalogueCorePluginTestApp.


 * [Technology Stack](docs/development/frameworks/index.md)
 * [Migration from Version 1.x](docs/development/migration.adoc)
 * [Interacting with Model Catalogue using XML](docs/development/integration/xml.adoc)
 * [Common Issues](docs/development/bugs/index.md)
 * [ElasticSearch Integration](docs/deployment/elasticsearch.md)
 * [Docker Release Pipepine](docs/deployment/docker_releases.md)
 * [Exporting from Model Catalogue](docs/development/recipes/exports.md)

## Testing
 * [Writing Geb Specifications using Stale-Safe DSL](docs/development/frameworks/geb.md)

## Miscellaneous
 * [Model Catalogue XML Schema](ModelCatalogueCorePlugin/grails-app/assets/other/schema/2.0/metadataregistry.xsd)
 * [Data Model Policy Definitons](docs/development/recipes/policies.md)

## License
 * The Model Catalogue is license under the terms of the [Apache License, Version 2.0.](http://www.apache.org/licenses/LICENSE-2.0.html)

## Credits
The model catalogue is maintained by [Metadata Consulting](http://www.metadataconsulting.co.uk).
We also wish to thank [Genomics England](http://www.genomicsengland.co.uk/), [the Medical Research Council](http://www.mrc.ac.uk/) and [NIHR](http://www.nihr.ac.uk/) for their support, as well as Charles Chrichton, Jim Davies, Steve Harris, Matous Kucera, Adam Milward, David Milward, Vladimir Orany, Soheil Saifipour and James Welch

