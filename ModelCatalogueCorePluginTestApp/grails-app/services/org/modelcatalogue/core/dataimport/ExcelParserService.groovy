package org.modelcatalogue.core.dataimport

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

class ExcelParserService {

    List<Map<String, String>> getRowMaps(Workbook workbook) {
        if (!workbook) {
            throw new IllegalArgumentException("Excel file contains no worksheet!")
        }

        Sheet sheet = workbook.getSheetAt(0)
        getRowMaps(sheet)
    }

    List<Map<String, String>> getRowMaps(Sheet sheet) {
        Iterator<Row> rowIt = sheet.rowIterator()
        Row row = rowIt.next()
        List<String> headers = getRowData(row)
        log.info("Headers are ${headers as String}")
        List<Map<String, String>> rowMaps = []
        int counter = 0
        int batchSize = 1000
        while (rowIt.hasNext()) {
            println("processing row" + counter)
            if(++counter % batchSize == 0 ){
                rowMaps.clear()
            }
            row = rowIt.next()
            Map<String, String> rowMap = createRowMap(row, headers)
            rowMaps << rowMap
        }
        rowMaps
    }

    List<String> getRowData(Row row) {
        def data = []
        for (Cell cell : row) {
            getValue(cell, data)
        }
        data
    }

    void getValue(Cell cell, List<String> data) {
        def colIndex = cell.getColumnIndex()
        data[colIndex] = valueHelper(cell)
    }

    protected Map<String, String> createRowMap(Row row, List<String> headers) {
        Map<String, String> rowMap = new LinkedHashMap<>()
        // Important that it's LinkedHashMap, for order to be kept, to get the last section which is metadata!
        for (Cell cell : row) {
            rowMap = updateRowMap(rowMap, cell, headers)
        }
        return rowMap
    }

    /**
     * getCatalogueElementDtoFromRow
     * @param Cell cell
     * @param List rowData
     * @return CatalogueElementDto
     */
    protected Map<String, String> updateRowMap(Map<String,String> rowMap, Cell cell,  List<String> headers) {
        def colIndex = cell.getColumnIndex()
        rowMap[headers[colIndex]] = valueHelper(cell)
        rowMap
    }

    protected String valueHelper(Cell cell){
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim()
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue()
                }
                return cell.getNumericCellValue()
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue()
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula()
        }
        return ""
    }
}
