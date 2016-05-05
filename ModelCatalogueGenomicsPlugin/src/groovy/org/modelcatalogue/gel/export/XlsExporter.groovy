package org.modelcatalogue.gel.export

import org.modelcatalogue.builder.spreadsheet.api.Sheet
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass

/**
 * Not quite generic Excel Exporter interface. Works for RareDiseaseChangeLogXlsExporter atm.
 */
interface XlsExporter {

    /**
     * Exports Excel spreadsheet to OutputStream
     * @param dataClass DataClass that spreadsheet is created for
     * @param out OutputStream writing to exported file
     */
    public void export(DataClass dataClass, OutputStream out)

    /**
     * Override to build Excel sheet as required
     * @param sheet The Excel sheet that you are populating
     * @param lines Collection of row strings produced by {@link org.modelcatalogue.gel.export.XlsExporter#generateLine generateLine()}
     */
    void buildSheet(Sheet sheet, List lines)

    /**
     * operates at level 5
     */
    List<String> generateLine(CatalogueElement model, List lines, groupDescriptions, level)

}
