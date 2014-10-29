# 2014-10-29

`EnumeratedTypeMarshaller` does not extend `DataTypeMarshaller` directly because if so we got into infinite loop
thanks to current Groovy language bug. Calling `super.prepareJsonMap(element)` is calling itself causing stack
overflow when initiated from `EnumeratedTypeMarshaller.prepareJsonMap(element)`.

# 2014-10-27

Reverted test configuration because current `code-coverage` plugin does not support forked mode.