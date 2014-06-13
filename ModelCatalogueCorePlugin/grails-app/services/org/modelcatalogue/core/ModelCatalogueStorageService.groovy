package org.modelcatalogue.core

import com.bertramlabs.plugins.karman.CloudFile
import com.bertramlabs.plugins.karman.Directory
import com.bertramlabs.plugins.karman.StorageProvider
import com.bertramlabs.plugins.karman.local.LocalStorageProvider
import grails.util.Environment

class ModelCatalogueStorageService {

    def grailsApplication

    /**
     * Returns serving url if available or null if the content has to be served from current application.
     * @param directory directory (bucket) of the file
     * @param filename name (id) of the file
     * @return serving url if available or null if the content has to be served from current application
     */
    String getServingUrl(String directory, String filename) { null }

    /**
     * Stores the file defined by given bytes and returns true if succeeded.
     * @param directory directory (bucket) of the file
     * @param filename name (id)  of the file
     * @param contentType content type of the file
     * @param content content of the file
     * @return <code>true</code> if the file has been stored successfully
     */
    boolean store(String directory, String filename, String contentType, byte[] content) {
        try {
            CloudFile file = getCloudFile(filename)
            file.contentType(contentType)
            file.bytes = content
            file.save()
            return true
        } catch (ignored) {
            return false
        }
    }

    /**
     * Tests if the file exists in the store.
     * @param directory
     * @param filename
     * @return <code>true</code> if the file exits in the store
     */
    boolean exists(String directory, String filename) {
        CloudFile file = getCloudFile(filename)
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
        CloudFile file = getCloudFile(filename)
        if (!file.exists()) throw new FileNotFoundException("No such file $filename in $directory")
        file.inputStream
    }

    private CloudFile getCloudFile(String assetFileName) {
        String providerName = grailsApplication.config.modelcatalogue.karman.provider ?: 'local'
        String directoryName = grailsApplication.config.modelcatalogue.karman.directory ?: 'assets'

        StorageProvider provider = StorageProvider.create(provider: providerName)

        if (provider instanceof LocalStorageProvider) {
            provider.basePath = grailsApplication.config.modelcatalogue.karman.basePath ? grailsApplication.config.modelcatalogue.karman.basePath : "${System.getProperty(Environment.currentEnvironment == Environment.DEVELOPMENT ? "java.io.tmpdir" : "user.dir")}/modelcatalogue/storage"
        }

        Directory directory = provider.getDirectory(directoryName)
        directory.mkdirs()
        directory.getFile(assetFileName)
    }
}
