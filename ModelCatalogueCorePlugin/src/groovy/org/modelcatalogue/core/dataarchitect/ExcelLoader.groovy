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


	def parse(path) {
		Workbook wb = WorkbookFactory.create(stream);
        if(!wb)
            return [[],[]]
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

	def getRowReference(Row row, Cell cell) {
		def rowIndex = row.getRowNum()
		def colIndex = cell.getColumnIndex()
		CellReference ref = new CellReference(rowIndex, colIndex)
		ref.getRichStringCellValue().getString()
	}

	def getValue(Row row, Cell cell, List data) {
		def rowIndex = row.getRowNum()
		def colIndex = cell.getColumnIndex()
		def value = ""
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getRichStringCellValue().getString().trim();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					value = cell.getDateCellValue();
				} else {
					value = cell.getNumericCellValue();
				}
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				value = cell.getCellFormula();
				break;
			default:
				value = ""
		}
		data[colIndex] = value
		data
	}

	def toXml(header, row) {
		def obj = "<object>\n"
		row.eachWithIndex { datum, i ->
			def headerName = header[i]
			obj += "\t<$headerName>$datum</$headerName>\n"
		}
		obj += "</object>"
	}

}