package org.modelcatalogue.core.dataimport.excel.gmcGrid

import org.modelcatalogue.core.dataimport.excel.ExcelLoader

/**
 * Class for loading GMC (Genomic Medical Centre) Grid Reports.
 *
 * Should load excel files of the format exported by what is now called UCLHGridReportXlsxExporter.
 * This format should eventually become the one format used by all 13 GMCs.
 * The UCLHGridReport links a source data model such as the Cancer Model with GMC models representing data sources.
 * Loading such a file consists of creating or updating the GMC models, and changing the link relationships appropriately.
 * Updating would involve finding the differences between what is said on file (placeholder metadata may change;
 * a placeholder may move from one GMC model to another)
 * and the current GMC models.
 *
 * These GMC models should have draft status, such that they can be easily updated. But if they are finalized,
 * updating would involve creating a new version.
 *
 * TODO: step 1: load excel into an in-memory programmatic/object representation Rep.
 * TODO: step 1.5: determine if the GMC models are to be created or updated
 * TODO: step 2C(reate): create GMC models from Rep.
 * TODO: step 2U(pdate): find differences between the representation and the GMC models.
 * TODO: step 3U: create new versions if necessary
 * TODO: step 4U: apply changes to placeholder metadata
 * TODO: step 5U: move placeholders
 * Created by james on 15/08/2017.
 */
class GMCGridReportExcelLoader extends ExcelLoader {

}
