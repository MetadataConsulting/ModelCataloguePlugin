package uk.co.mc.core.util

import groovy.util.slurpersupport.GPathResult
import org.codehaus.groovy.grails.web.json.JSONElement

interface ResultRecorder {

    File recordResult(String fixtureName, JSONElement json)
    File recordResult(String fixtureName, GPathResult xml)
    File recordInputJSON(String fixtureName, Map json)
    File recordInputJSON(String fixtureName, String json)
    File recordInputXML(String fixtureName, String xml)
}
