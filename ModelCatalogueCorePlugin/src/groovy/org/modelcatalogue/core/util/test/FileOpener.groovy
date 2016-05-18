package org.modelcatalogue.core.util.test

import groovy.util.logging.Commons
import org.codehaus.groovy.runtime.StackTraceUtils

import java.awt.Desktop

@Commons
class FileOpener {


    /**
     * Tries to open the file in Word. Only works locally on Mac at the moment. Ignored otherwise.
     * Main purpose of this method is to quickly open the generated file for manual review.
     * @param file file to be opened
     */
    static void open(File file) {
        try {
            if (Desktop.desktopSupported && Desktop.desktop.isSupported(Desktop.Action.OPEN)) {
                Desktop.desktop.open(file)
                println file
                Thread.sleep(10000)
            }
        } catch(e) {
            StackTraceUtils.deepSanitize(e)
            log.info "Failed to open file", e
        }
    }
}
