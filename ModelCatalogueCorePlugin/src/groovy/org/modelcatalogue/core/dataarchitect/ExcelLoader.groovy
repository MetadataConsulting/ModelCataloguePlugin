package org.modelcatalogue.core.dataarchitect

import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellReference

class ExcelLoader {

    private static InputStream stream


    public ExcelLoader(String path)
    {
        stream = new FileInputStream(path)
    }

    public ExcelLoader(InputStream inputStream)
    {
        stream  = inputStream
    }


	def parse() {
		Workbook wb = WorkbookFactory.create(stream);
        if(!wb) return [[],[]]
		Sheet sheet = wb.getSheetAt(0);

		Iterator<Row> rowIt = sheet.rowIterator()
		Row row = rowIt.next()
		def headers = getRowData(row)

		def rows = []
		while(rowIt.hasNext()) {
			row = rowIt.next()
            def data =getRowData(row)

            def canBeInserted = false;
            data.eachWithIndex { def entry, int i ->
                if(entry!=null && entry!="")
                    canBeInserted= true;
            }
            if(canBeInserted)
			    rows << data
		}
		[headers, rows]
	}

	def getRowData(Row row) {
		def data = []
		for (Cell cell : row) {
			getValue(row, cell, data)
		}
		data
	}

	static getRowReference(Row row, Cell cell) {
		def rowIndex = row.getRowNum()
		def colIndex = cell.getColumnIndex()
		CellReference ref = new CellReference(rowIndex, colIndex)
		ref.getRichStringCellValue().getString()
	}

	static getValue(Row row, Cell cell, List data) {
		def colIndex = cell.getColumnIndex()
		data[colIndex] = valueHelper(cell)
		data
	}

    static valueHelper(Cell cell){
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
        }
        return ""
    }

	static toXml(header, row) {
		def obj = "<object>\n"
		row.eachWithIndex { datum, i ->
			def headerName = header[i]
			obj += "\t<$headerName>$datum</$headerName>\n"
		}
		obj += "</object>"
	}

}