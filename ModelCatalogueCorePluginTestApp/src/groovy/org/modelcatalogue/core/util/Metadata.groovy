package org.modelcatalogue.core.util

/**
 * Class containing constants for metadata keys
 */
class Metadata {

    //Model metadata
    public static final def ORGANISATION = "http://www.modelcatalogue.org/metadata/#organization"
    public static final def OWNER = "http://www.modelcatalogue.org/metadata/#owner"
    public static final def AUTHORS = "http://www.modelcatalogue.org/metadata/#authors"
    public static final def REVIEWERS = "http://www.modelcatalogue.org/metadata/#reviewers"

    /**
     * Phenotype subclass metadata
     */
    public static final def OBO_ID = "OBO ID"

    /**
     * The HPO and clinical tests report exporters only work for and should only be available to the relevant data class(es), discerned by this metadata field
     */
    public static final def HPO_REPORT_AVAILABLE = "http://www.modelcatalogue.org/metadata/genomics/#rare-disease-reports"
    public static final def CANCER_TYPES_AVAILABLE = "http://www.modelcatalogue.org/metadata/genomics/#cancer-types-export"
    public static final def ALL_RD_REPORTS = "http://www.modelcatalogue.org/metadata/genomics/#all-rd-reports"
    public static final def ALL_CANCER_REPORTS = "http://www.modelcatalogue.org/metadata/genomics/#all-cancer-reports"
    public static final def CHANGE_REF = "http://www.modelcatalogue.org/metadata/genomics/#change-reference"
    public static final def WEBSITE_SKIP = "http://www.modelcatalogue.org/metadata/genomics/#website-skip"


    public static final String MAX_OCCURS = "Max Occurs"
    public static final String MIN_OCCURS = "Min Occurs"

    // excel export
    public static final def SKIP_EXPORT = "http://www.modelcatalogue.org/metadata/#skip-export"
    public static final def SUBSECTION = "http://www.modelcatalogue.org/metadata/#subsection"
}
