# 2015-02-25
Deleted `ModelCatalogueElasticSearchPlugin` and the `searchable` configuration in the domain classes as it become very
stale. It will be very difficult to provide classifications filters in elastic search at the current status.

# 2014-11-04
Reverted `CatalogueElement.latestVersion` to be a just `Long` because otherwise Hibernate is pretty confused by it (or
at least Fixtures plugin).

# 2014-11-02

Mappings returned from the application are not filtered by any classification because the nature of mappings is to be
map elements from different classifications.

History is not filtered by classifications chosen because this will barely happen but if it does happen it is probably
something the user wants to be always aware.

We want to use Spring security as a dependency, so we can do auditing etc. on users

# 2014-10-29

`EnumeratedTypeMarshaller` does not extend `DataTypeMarshaller` directly because if so we got into infinite loop
thanks to current Groovy language bug. Calling `super.prepareJsonMap(element)` is calling itself causing stack
overflow when initiated from `EnumeratedTypeMarshaller.prepareJsonMap(element)`.

Classifies is now modeled again as `Relationship` between `Classification` and `PublishedElement`. Grails simply doesn't
handle complicated many-to-many mappings gracefully.

# 2014-10-27

Reverted test configuration to not-forked because current `code-coverage` plugin does not support forked mode.
