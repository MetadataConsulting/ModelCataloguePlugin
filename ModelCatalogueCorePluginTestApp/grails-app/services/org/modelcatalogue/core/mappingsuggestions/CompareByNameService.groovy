package org.modelcatalogue.core.mappingsuggestions

import groovy.transform.CompileStatic
import org.simmetrics.StringMetric
import org.simmetrics.metrics.StringMetrics

@CompileStatic
class CompareByNameService {

    float distance(WithName source, WithName destination) {
        StringMetric metric = StringMetrics.levenshtein()
        return metric.compare(source.name, destination.name)
    }
}