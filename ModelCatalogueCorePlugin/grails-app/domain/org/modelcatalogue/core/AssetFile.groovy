package org.modelcatalogue.core

class AssetFile {

    String path
    byte[] content

    static constraints = {
        content maxSize: 50 * 1024 * 1024
    }

}
