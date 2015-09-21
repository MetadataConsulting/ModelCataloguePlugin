package org.modelcatalogue.core.gel

import com.craigburke.document.builder.BasicDocumentPartTypes
import com.craigburke.document.builder.WordDocumentBuilder

class GelWordDocumentBuilder extends WordDocumentBuilder {

    GelWordDocumentBuilder(OutputStream outputStream) {
        super(outputStream)
    }

    @Override
    protected boolean isStylesEnabled() {
        return true
    }

    @Override
    Object renderCustomFiles() {

//        wordDocument.generateDocumentPart(BasicDocumentPartTypes.NUMBERING) { builder ->
//            w.numbering {
//                w.abstractNum 'w:abstractNumId': "1", {
//                    for (int lvl in 0..8) {
//                        w.lvl 'w:ilvl': "${lvl}", {
//                            w.start 'w:val': '1'
//                            w.numFmt 'w:val': 'none'
//                            w.suff 'w:val': 'nothing'
//                            w.lvlText 'w:val': ''
//                            w.lvlJc 'w:val': 'left'
//                            w.pPr {
//                                // let's make the fifth level at the one inch
//                                String tabPosition = pointToTwip(22 + lvl * 10).intValue()
//                                w.tabs {
//                                    w.tab 'w:val': 'num', 'w:pos': tabPosition
//                                }
//                                w.ind 'w:left': tabPosition, 'w:hanging': tabPosition
//                            }
//                        }
//                    }
//                    w.num 'w:numId': "1", {
//                        w.abstractNumId 'w:val': '1'
//                    }
//                }
//            }
//        }
//

        wordDocument.generateDocumentPart(BasicDocumentPartTypes.STYLES) {
            w.styles {
                def normal = ['w:val': 'Normal']
                w.style 'w:type': 'paragraph', 'w:styleId': 'Normal', 'w:default': '1', {
                    w.name normal
                    w.qFormat()
                }
                int headingMax = 8
                for (int lvl in 1..headingMax) {
                    w.style 'w:type': 'paragraph', 'w:styleId': "Heading${lvl}", {
                        w.name 'w:val': "heading ${lvl}"
                        w.basedOn normal
                        w.next normal
                        w.link 'w:val': "Heading${lvl}Char"
                        w.uiPriority 'w:val': '9'
                        w.qFormat()
                        w.pPr {
                            w.keepNext()
                            w.keepLines()
                            w.outlineLvl('w:val': "${lvl - 1}")
                        }
                        w.rPr {
                            w.b()
                            w.bCs()
                            switch (lvl) {
                                case 1:
                                    def params = ['w:val': '32']
                                    w.sz params
                                    w.szCs params
                                    break
                                case 2:
                                    def params = ['w:val': '26']
                                    w.sz params
                                    w.szCs params
                                    break
                            }
                        }

                    }
                }
                for (int lvl in 1..headingMax) {
                    w.style 'w:type': 'character', 'w:styleId': "Heading${lvl}Char", {
                        w.name 'w:val': "heading ${lvl} char"
                        w.link 'w:val': "Heading${lvl}"
                        w.qFormat()
                    }
                }
            }
        }

    }
}