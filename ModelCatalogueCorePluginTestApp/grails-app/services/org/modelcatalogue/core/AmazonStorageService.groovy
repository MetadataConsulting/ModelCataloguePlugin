package org.modelcatalogue.core

import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalStorageProvider
import org.codehaus.groovy.grails.commons.GrailsApplication

import javax.annotation.PostConstruct

class AmazonStorageService implements StorageService {

    GrailsApplication grailsApplication
    private String bucket
    private StorageProvider provider
    private Long maxSize

    @PostConstruct
    private void init() {
        maxSize = grailsApplication.config.mc.storage.maxSize ?: (20 * 1024 * 1024)
        if (grailsApplication.config.mc.storage.s3.bucket) {
            provider = StorageProvider.create(
                provider: 's3',
                accessKey: grailsApplication.config.mc.storage.s3.key,
                secretKey: grailsApplication.config.mc.storage.s3.secret,
                region: grailsApplication.config.mc.storage.s3.region ?: 'eu-west-1'
            )
            bucket = grailsApplication.config.mc.storage.s3.bucket
        } else {
            provider = new LocalStorageProvider(basePath: grailsApplication.config.mc.storage.directory ?: 'storage')
            bucket = 'modelcatalogue'
        }

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
        CloudFile file = provider[bucket]["$directory/$filename"]
        ByteArrayOutputStream stream = new ByteArrayOutputStream()
        stream.withStream withOutputStream
        file.bytes = stream.toByteArray()
        file.save()
    }

    /**
     * Tests if the file exists in the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the store
     */
    boolean exists(String directory, String filename) {
        provider[bucket]["$directory/$filename"].exists()
    }

    boolean delete(String directory, String filename) {
        if (exists(directory, filename)) {
            provider[bucket]["$directory/$filename"].delete()
            return true
        }
        return false
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
        provider[bucket]["$directory/$filename"].inputStream
    }
}
