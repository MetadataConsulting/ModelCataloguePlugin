# 2014-11-02

Mappings returned from the application are not filtered by any classification because the nature of mappings is to be
map elements from different classifications.

History is not filtered by classifications chosen because this will barely happen but if it does happen it is probably
something the user wants to be always aware.

# 2014-10-29

`EnumeratedTypeMarshaller` does not extend `DataTypeMarshaller` directly because if so we got into infinite loop
thanks to current Groovy language bug. Calling `super.prepareJsonMap(element)` is calling itself causing stack
overflow when initiated from `EnumeratedTypeMarshaller.prepareJsonMap(element)`.

Classifies is now modeled again as `Relationship` between `Classification` and `PublishedElement`. Grails simply doesn't
handle complicated many-to-many mappings gracefully.

# 2014-10-27

Reverted test configuration to not-forked because current `code-coverage` plugin does not support forked mode.