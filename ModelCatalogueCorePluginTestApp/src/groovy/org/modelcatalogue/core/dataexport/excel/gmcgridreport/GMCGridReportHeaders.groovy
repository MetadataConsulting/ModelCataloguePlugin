package org.modelcatalogue.core.dataexport.excel.gmcgridreport

/**
 * Created by james on 17/08/2017.
 */
class GMCGridReportHeaders {
    static String id = 'ID'
    static String dataElement = 'Data Element'
    static String multiplicity = 'Multiplicity'
    static String dataType = 'Data Type'
    static String validationRule = 'Validation Rule'
    static String businessRule = 'Business Rule'
    static String placeholder = 'Placeholder'
    static String sourceSystem = 'Source System'
    static String previouslyInSourceSystem = 'Previously In Source System'

    static String semanticMatching = 'Semantic Matching'
    static String knownIssue = 'Known Issue'
    static String immediateSolution = 'Immediate Solution'
    static String immediateSolutionOwner = 'Immediate Solution Owner'
    static String longTermSolution = 'Long Term Solution'
    static String longTermSolutionOwner = 'Long Term Solution Owner'
    static String dataItemUniqueCode = 'Data Item Unique Code'
    static String relatedTo = 'Related To'
    static String partOfStandardDataSet = 'Part Of Standard Data Set'
    static String dataCompleteness = 'Data Completeness'
    static String estimatedQuality = 'Estimated Quality'
    static String timely = 'Timely'
    static String comments = 'Comments'

    static List<String> ntElementMetadataHeaders =
        [semanticMatching, knownIssue, immediateSolution, immediateSolutionOwner, longTermSolution, longTermSolutionOwner, dataItemUniqueCode, relatedTo, partOfStandardDataSet, dataCompleteness, estimatedQuality, timely, comments]
    static List<String> ntElementMetadataKeys = ntElementMetadataHeaders //ntElementMetadataHeaders.collect{ it.replace(/Related To Metadata/, 'Related To')}
    static List<String> excelHeaders = // v0.2
        [id, dataElement, multiplicity, dataType, validationRule, businessRule, placeholder, sourceSystem, previouslyInSourceSystem] + ntElementMetadataHeaders
}
