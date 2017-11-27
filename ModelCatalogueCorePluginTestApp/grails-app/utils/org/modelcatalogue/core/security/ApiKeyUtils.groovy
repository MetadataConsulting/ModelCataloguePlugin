package org.modelcatalogue.core.security

import groovy.transform.CompileStatic

@CompileStatic
class ApiKeyUtils {

    /**
     * @description returns a random API KEY encoded in Base 64
     */
    static String apiKey() {
        UUID.randomUUID().toString().bytes.encodeBase64()
    }
}
