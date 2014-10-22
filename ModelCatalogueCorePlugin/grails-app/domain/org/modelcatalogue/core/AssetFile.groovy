package org.modelcatalogue.core

import java.sql.Blob

class AssetFile {

    String path
    Blob content

    static constraints = {
        content maxSize: 50 * 1024 * 1024
    }

}
