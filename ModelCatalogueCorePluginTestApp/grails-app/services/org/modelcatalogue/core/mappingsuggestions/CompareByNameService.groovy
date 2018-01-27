package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.simmetrics.StringMetric
import org.simmetrics.metrics.StringMetrics

@CompileStatic
class CompareByNameService {

    Float distance(WithName source, WithName destination) {
        StringMetric metric = StringMetrics.levenshtein()
        new Float(metric.compare(source.name, destination.name))
    }
}