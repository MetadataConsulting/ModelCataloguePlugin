package org.modelcatalogue.core

import org.codehaus.groovy.grails.web.json.JSONElement
import org.modelcatalogue.core.util.ResultRecorder

/**
 * Created by ladin on 15.10.14.
 */
public enum DummyRecorder implements ResultRecorder {

    INSTANCE

    File recordResult(String fixtureName, JSONElement json) {
        return null
    }

    File recordInputJSON(String fixtureName, Map json) {
        return null
    }

    File recordInputJSON(String fixtureName, String json) {
        return null
    }

}