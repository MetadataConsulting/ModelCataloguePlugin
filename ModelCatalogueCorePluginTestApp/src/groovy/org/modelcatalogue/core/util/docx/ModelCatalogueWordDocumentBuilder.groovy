package org.modelcatalogue.core.util.docx

import com.craigburke.document.builder.BasicDocumentPartTypes
import com.craigburke.document.builder.WordDocumentBuilder
import com.craigburke.document.core.Document
/** Generates a Word document */
class ModelCatalogueWordDocumentBuilder extends WordDocumentBuilder {

    ModelCatalogueWordDocumentBuilder(OutputStream outputStream) {
        super(outputStream)
    }

    @Override
    protected boolean isStylesEnabled() {
        return true
    }

    void initializeDocument(Document document, OutputStream out) {
        super.initializeDocument(document, out)

        def styles = {
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

        wordDocument.generateDocumentPart(BasicDocumentPartTypes.STYLES, styles)
        // comment out if stylesWithEffects.xml is breaking the document
        wordDocument.generateDocumentPart(BasicDocumentPartTypes.STYLES_WITH_EFFECTS, styles)
    }
}
