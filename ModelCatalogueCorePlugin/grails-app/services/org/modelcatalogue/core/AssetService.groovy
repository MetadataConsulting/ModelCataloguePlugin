package org.modelcatalogue.core

import groovy.transform.CompileStatic
import org.apache.commons.io.input.CountingInputStream
import org.apache.commons.io.output.CountingOutputStream
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.modelcatalogue.core.publishing.DraftContext
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile

import java.security.DigestInputStream
import java.security.DigestOutputStream
import java.security.MessageDigest

/**
 * Created by ladin on 10.07.14.
 */
@CompileStatic
class AssetService {

    StorageService modelCatalogueStorageService
    ElementService elementService

    private static final long GIGA = 1024 * 1024 * 1024
    private static final long MEGA = 1024 * 1024
    private static final long KILO = 1024

    static String toBytes(Long value) {
        if (!value) return "0 B"

        if (value > GIGA) return String.format("%.2f GB", value / GIGA)
        if (value > MEGA) return String.format("%.2f MB", value / MEGA)
        if (value > KILO) return String.format("%.2f KB", value / KILO)
        "$value B"
    }

    Asset upload(Long id, String name, String description, MultipartFile file) {
        if (file.size > modelCatalogueStorageService.maxFileSize) {
            Asset asset = new Asset()
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "You cannot upload files greater than ${toBytes(modelCatalogueStorageService.maxFileSize)}")
            return asset
        }

        Asset asset = new Asset()

        asset.name              = name ?: file.originalFilename
        asset.description       = description
        asset.contentType       = file.contentType
        asset.size              = file.size
        asset.originalFileName  = file.originalFilename

        asset.validate()

        if (asset.hasErrors()) {
            return asset
        }

        if (id) {
            Asset existing = Asset.get(id)

            if (!existing) {
                return null
            }

            existing = elementService.createDraftVersion(existing, DraftContext.forceNew()) as Asset

            if (existing.hasErrors()) {
                return existing
            }


            existing.name              = asset.name
            existing.description       = asset.description
            existing.contentType       = asset.contentType
            existing.originalFileName  = asset.originalFileName

            asset = existing
        }

        asset.save(flush: true)


        try {
            storeAssetFromFile(file, asset)
        } catch (e) {
            log.error('Exception storing asset ' + asset.name, e)
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "There were problems uploading file $file.originalFilename")
        }

        return asset
    }

    void storeAssetFromFile(MultipartFile file, Asset asset) {
        DigestInputStream dis = null
        try {
            MessageDigest md5 = MessageDigest.getInstance('MD5')
            dis = new DigestInputStream(file.inputStream, md5)
            CountingInputStream countingInputStream = new CountingInputStream(dis)
            modelCatalogueStorageService.store('assets', "${asset.id}", file.contentType, { OutputStream it -> it << countingInputStream })
            asset.md5 = DigestUtils.md5DigestAsHex(md5.digest())
            asset.size = countingInputStream.byteCount
            asset.save(flush: true)
        } catch (Exception e) {
            log.error("Exception storing asset from file", e)
            throw e
        } finally {
            dis?.close()
        }
    }

    void storeAssetFromInputStream(InputStream inputStream, String contentType, Asset asset) {
        DigestInputStream dis = null
        try {
            MessageDigest md5 = MessageDigest.getInstance('MD5')
            dis = new DigestInputStream(inputStream, md5)
            CountingInputStream countingInputStream = new CountingInputStream(dis)
            modelCatalogueStorageService.store('assets', "${asset.id}", contentType, { OutputStream it -> it << countingInputStream })
            asset.md5 = DigestUtils.md5DigestAsHex(md5.digest())
            asset.size = countingInputStream.byteCount
            asset.save(flush: true)
        } catch (Exception e) {
            log.error("Exception storing asset from file", e)
            throw e
        } finally {
            dis?.close()
        }
    }

    void storeAssetWithSteam(Asset asset, String contentType, Closure withOutputStream) {
        if (!asset) throw new IllegalArgumentException("Please, provide valid asset.")
        MessageDigest md5 = MessageDigest.getInstance('MD5')
        modelCatalogueStorageService.store('assets', "${asset.id}", contentType) { OutputStream it ->
            DigestOutputStream dos      = null
            CountingOutputStream cos    = null
            try {
                dos = new DigestOutputStream(it, md5)
                cos = new CountingOutputStream(dos)
                withOutputStream(cos)
                asset.size = cos.byteCount
            } catch (InvokerInvocationException e) {
                // sadly this sometimes happens
                log.error("Exception storing asset with output stream", e.cause)
                throw e.cause
            }  catch (Exception e) {
                log.error("Exception storing asset with output stream", e)
                throw e
            } finally {
                dos?.close()
                cos?.close()
            }
        }
        asset.md5 = DigestUtils.md5DigestAsHex(md5.digest())
        asset.save(flush: true)
    }

}
