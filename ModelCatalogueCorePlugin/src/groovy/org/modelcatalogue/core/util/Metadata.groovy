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

    //Phenotype subclass metadata
    public static final def OBD_ID = "OBO ID"

    /**
     * The HPO and clinical tests report exporters only work for and should only be available to the relevant data class(es), discerned by this metadata field
     */
    public static final def HPO_REPORT_AVAILABLE = "rare-disease-reports"

}
