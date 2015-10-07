package org.modelcatalogue.builder.xlsx.poi

import org.apache.poi.xssf.usermodel.XSSFColor
import spock.lang.Specification
import spock.lang.Unroll

class PoiCellStyleSpec extends Specification {

    @Unroll
    def "parse #hex to #r,#g,#b"() {
        when:
        XSSFColor color = PoiCellStyle.parseColor(hex)

        then:
        color.rgb == [r,g,b] as byte[]

        where:
        hex         | r     | g     | b
        '#000000'   |    0  |    0  |   0
        '#aabbcc'   |  -86  |  -69  | -52
        '#ffffff'   |   -1  |   -1  |  -1
    }
}
