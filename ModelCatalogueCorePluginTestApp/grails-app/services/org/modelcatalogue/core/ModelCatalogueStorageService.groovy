package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.runtime.IOGroovyMethods
import org.hibernate.SessionFactory
import org.modelcatalogue.core.util.FriendlyErrors
import javax.annotation.PostConstruct

class ModelCatalogueStorageService implements StorageService {

    GrailsApplication grailsApplication
    SessionFactory sessionFactory
    private Long maxSize

    @PostConstruct
    private void init() {
        maxSize = grailsApplication.config.mc.storage.maxSize ?: (20 * 1024 * 1024)
    }

    /**
     * Returns serving url if available or null if the content has to be served from current application.
     * @param directory directory (bucket) of the file
     * @param filename name (id) of the file
     * @return serving url if available or null if the content has to be served from current application
     */
    String getServingUrl(String directory, String filename) { null }

    /**
     * Returns the maximal size of the file the storage can handle.
     * @return the maximal size of the file the storage can handle
     */
    long getMaxFileSize() {
        maxSize
    }

    /**
     * Stores the file defined by given bytes and returns true if succeeded.
     * @param directory directory (bucket) of the file
     * @param filename name (id)  of the file
     * @param contentType content type of the file
     * @param withOutputStream the closure which gets files output stream as a parameter
     */
    void store(String directory, String filename, String contentType, Closure withOutputStream) {
        AssetFile file = AssetFile.findByPath(getPath(directory, filename))

        if (!file) {
            file = new AssetFile(path: getPath(directory, filename), content: sessionFactory.currentSession.lobHelper.createBlob(new byte[0]))
        }

        IOGroovyMethods.withStream(file.content.setBinaryStream(1), withOutputStream)

        FriendlyErrors.failFriendlySave(file)
    }

    /**
     * Tests if the file exists in the database.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the database
     */
    boolean exists(String directory, String filename) {
        AssetFile.findByPath(getPath(directory, filename))
    }

    /**
     * Fetches the file from the database.
     * @param directory
     * @param filename
     * @return the file from the database as input stream
     * @throws FileNotFoundException if the file does not exist in the database
     */
    InputStream fetch(String directory, String filename) {
        if (!exists(directory, filename)) throw new FileNotFoundException("No such file $filename in $directory")
        AssetFile.findByPath(getPath(directory, filename))?.content?.binaryStream ?: new ByteArrayInputStream(new byte[0])
    }

    boolean delete(String directory, String filename) {
        if (!exists(directory, filename)) return false
        AssetFile.findByPath(getPath(directory, filename)).delete(flush: true, failOnError: true)
        return true
    }


    private static String getPath(String directory, String filename) {
        "$directory/$filename"
    }
}
