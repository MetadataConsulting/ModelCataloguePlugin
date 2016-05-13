package org.modelcatalogue.gel.export

import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.core.CatalogueElement

/**
 * Not quite generic Excel Exporter interface. Works for RareDiseaseChangeLogXlsExporter atm.
 */
interface XlsExporter {

    /**
     * Exports Excel spreadsheet to OutputStream
     * @param dataClass DataClass that spreadsheet is created for
     * @param out OutputStream writing to exported file
     */
    public void export(CatalogueElement element, OutputStream out)

    /**
     * Override to build Excel sheet as required
     * @param sheet The Excel sheet that you are populating
     * @param lines Collection of row strings produced by {@link org.modelcatalogue.gel.export.XlsExporter#searchExportSpecificTypes searchExportSpecificTypes()}
     */
    void buildSheet(Sheet sheet, List lines)

    /**
     * operates at level 5
     */
    List<String> searchExportSpecificTypes(CatalogueElement model, List lines, groupDescriptions, level)

    /**
     * Method called to descend through the data class hierarchy, ultimately calling {@link org.modelcatalogue.gel.export.XlsExporter#generateLine generateLine()}
     * @param element Probably top-level data class
     * @param lines Collection of Strings to be populated by {@link org.modelcatalogue.gel.export.XlsExporter#generateLine generateLine()}
     * @param level Level requested at
     * @param groupDescriptions Map of change descriptions
     */
    def descendModels(CatalogueElement element, lines, level, Map groupDescriptions, exclusions)

}
