package org.modelcatalogue.core.util

/**
 * Constants class of metadata keys
 */
class Metadata {

    public static final def ORGANISATION = "http://www.modelcatalogue.org/metadata/#organization"
    public static final def OWNER = "http://www.modelcatalogue.org/metadata/#owner"
    public static final def AUTHORS = "http://www.modelcatalogue.org/metadata/#authors"
    public static final def REVIEWERS = "http://www.modelcatalogue.org/metadata/#reviewers"

    /**
     * The HPO and clinical tests report exporters only work for and should only be available to the relevant data class(es), discerned by this metadata field
     */
    public static final def HPO_REPORT_AVAILABLE = "rare-disease-hpo-clinical-tests-report"
    public static final def CANCER_TYPES_AVAILABLE = "cancer-types-export"

}
