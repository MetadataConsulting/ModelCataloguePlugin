package org.modelcatalogue.core

import org.codehaus.groovy.grails.commons.GrailsApplication
import javax.annotation.PostConstruct

class LocalFilesStorageService implements StorageService {

    GrailsApplication grailsApplication
    private File fileStoreBase
    private Long maxSize

    @PostConstruct
    private void init() {
        fileStoreBase   = new File(grailsApplication.config.mc.storage.directory ?: 'storage')
        maxSize         = grailsApplication.config.mc.storage.maxSize ?: (20 * 1024 * 1024)

        // create all necessary directories
        fileStoreBase.mkdirs()
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
        File dir = new File(fileStoreBase, directory)
        dir.mkdirs()
        new File(dir, filename).withOutputStream withOutputStream
    }

    /**
     * Tests if the file exists in the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the store
     */
    boolean exists(String directory, String filename) {
        File dir = new File(fileStoreBase, directory)
        if (!dir.exists()) return false
        File file = new File(dir, filename)
        file.exists()
    }

    /**
     * Fetches the file from the storage as input stream.
     * @param directory
     * @param filename
     * @return the file from the storage as input stream
     * @throws FileNotFoundException if the file does not exist in the store
     */
    InputStream fetch(String directory, String filename) {
        if (!exists(directory, filename)) throw new FileNotFoundException("No such file $filename in $directory")
        new File(new File(fileStoreBase, directory), filename).newInputStream()
    }

    /**
     * Removes the file from the file system.
     * @param directory
     * @param filename
     * @throws FileNotFoundException if the file does not exist in the store
     * @return true if the file existed in the storage
     */
    boolean delete(String directory, String filename) {
        if (!exists(directory, filename)) return false
        return new File(new File(fileStoreBase, directory), filename).delete()
    }
}
