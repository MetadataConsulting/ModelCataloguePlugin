package org.modelcatalogue.integration.obo

import org.obolibrary.oboformat.model.Clause
import org.obolibrary.oboformat.model.Frame
import org.obolibrary.oboformat.model.OBODoc
import org.obolibrary.oboformat.parser.OBOFormatConstants
import org.obolibrary.oboformat.parser.OBOFormatParser
import org.obolibrary.oboformat.parser.OBOFormatParserException

class OrderKeepingOboFormatParser extends OBOFormatParser {

    @Override
    OBODoc parse(BufferedReader reader) throws IOException, OBOFormatParserException {
        this.setReader(reader);
        OBODoc obodoc = new OrderKeepingOboDoc();
        this.parseOBODoc(obodoc);
        Frame hf = obodoc.getHeaderFrame();
        LinkedList imports = new LinkedList();
        if(hf != null) {
            Iterator var5 = hf.getClauses(OBOFormatConstants.OboFormatTag.TAG_IMPORT).iterator();

            while(var5.hasNext()) {
                Clause cl = (Clause)var5.next();
                String path = this.resolvePath((String)cl.getValue(String.class));
                cl.setValue(path);
                if(this.followImport) {
                    OBOFormatParser parser = new OBOFormatParser();
                    OBODoc doc = parser.parseURL(path);
                    imports.add(doc);
                }
            }

            obodoc.setImportedOBODocs(imports);
        }

        return obodoc;
    }
}
