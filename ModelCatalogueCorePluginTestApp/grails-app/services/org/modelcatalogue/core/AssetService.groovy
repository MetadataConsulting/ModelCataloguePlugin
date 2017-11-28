package org.modelcatalogue.core

import com.google.common.collect.ImmutableMap
import groovy.transform.CompileDynamic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FromString
import org.apache.commons.io.input.CountingInputStream
import org.apache.commons.io.output.CountingOutputStream
import org.codehaus.groovy.runtime.InvokerInvocationException
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.audit.AuditService
import org.modelcatalogue.core.persistence.DataModelGormService
import org.modelcatalogue.core.publishing.CloningContext
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile
import java.security.DigestInputStream
import java.security.DigestOutputStream
import java.security.MessageDigest
import java.util.concurrent.ExecutorService

//@CompileStatic
class AssetService {

    StorageService modelCatalogueStorageService
    ElementService elementService
    ExecutorService executorService
    SecurityService modelCatalogueSecurityService
    AuditService auditService
    DataModelGormService dataModelGormService

    private static final long GIGA = 1024 * 1024 * 1024
    private static final long MEGA = 1024 * 1024
    private static final long KILO = 1024

    private static final ImmutableMap<String, String> EXTENSIONS_TO_CONTENT_TYPE = ImmutableMap.of('xsl', 'text/xsl')

    static String toBytes(Long value) {
        if (!value) return "0 B"

        if (value > GIGA) return String.format("%.2f GB", value / GIGA)
        if (value > MEGA) return String.format("%.2f MB", value / MEGA)
        if (value > KILO) return String.format("%.2f KB", value / KILO)
        "$value B"
    }

    Asset upload(Long id, Long dataModelId, String name, String description, MultipartFile file, String filename = file.originalFilename) {
        Asset asset = dataModelId ? new Asset(dataModel: dataModelGormService.findById(dataModelId)) : new Asset()

        if (file.size > modelCatalogueStorageService.maxFileSize) {
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "You cannot upload files greater than ${toBytes(modelCatalogueStorageService.maxFileSize)}")
            return asset
        }

        // TODO: set data model

        asset.name              = name ?: filename
        asset.description       = description
        asset.contentType       = getOverridableContentType(file)
        asset.size              = file.size
        asset.originalFileName  = filename

        asset.validate()

        if (asset.hasErrors()) {
            return asset
        }

        Asset existing
        if (id) {
            existing = Asset.get(id)

            if (!existing) {
                return null
            }

            Asset clone = elementService.cloneElement(existing, CloningContext.create(existing.dataModel, existing.dataModel))

            if (clone.hasErrors()) {
                return clone
            }


            clone.name              = asset.name
            clone.description       = asset.description
            clone.contentType       = asset.contentType
            clone.originalFileName  = asset.originalFileName

            clone.latestVersionId   = existing.latestVersionId ?: existing.id

            asset = clone
        }

        asset.save(flush: true)

        if (existing) {
            addToSupersedes(existing, asset)
            elementService.archive(existing, true)
        }

        try {
            storeAssetFromFile(file, asset)
        } catch (e) {
            log.error('Exception storing asset ' + asset.name, e)
            asset.errors.rejectValue('md5', 'asset.uploadfailed', "There were problems uploading file $filename")
        }

        return asset
    }

    private static String getOverridableContentType(MultipartFile multipartFile) {
        for (Map.Entry<String, String> entry in EXTENSIONS_TO_CONTENT_TYPE) {
            if (multipartFile.originalFilename?.endsWith(entry.key)) {
                return entry.value
            }
        }
        return multipartFile.contentType
    }

    @CompileDynamic
    protected static void addToSupersedes(Asset existing, Asset asset) {
        asset.addToSupersedes(existing, skipUniqueChecking: true)
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

    void storeAssetWithStream(Asset asset, String contentType, Closure withOutputStream) {
        if (!asset) throw new IllegalArgumentException("Please, provide valid asset.")
        MessageDigest md5 = MessageDigest.getInstance('MD5')
        modelCatalogueStorageService.store('assets', "${asset.id}", contentType) { OutputStream it ->
            DigestOutputStream dos      = null
            CountingOutputStream cos    = null
            try {
                dos = new DigestOutputStream(it, md5)
                cos = new CountingOutputStream(dos)
                if (withOutputStream.maximumNumberOfParameters == 1) {
                    withOutputStream(cos)
                } else {
                    withOutputStream(cos, asset.id)
                }
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

    protected Asset storeAsset(Map param, MultipartFile file, String contentType = 'application/xslt'){
        String theName = (param.name ?: param.action)

        // data model unknown at the moment
        Asset asset = new Asset(
                name: "Import for " + theName,
                originalFileName: file.originalFilename,
                description: "Your import will be available in this asset soon. Use Refresh action to reload.",
                status: ElementStatus.PENDING,
                contentType: contentType,
                size: 0
        )
        asset.save(flush: true, failOnError: true)
        storeAssetFromFile(file, asset)
        return asset
    }

    Long storeReportAsAsset(Map<String, Object> assetParams, DataModel dataModel, @ClosureParams(value = FromString, options= ["java.io.OutputStream", "java.io.OutputStream,java.lang.Long"]) Closure worker){
        assert assetParams.name
        assert assetParams.contentType
        assert assetParams.originalFileName

        assetParams.size = 0
        assetParams.status = ElementStatus.PENDING
        assetParams.description = assetParams.description ?: "Your report will be available in this asset soon. Use Refresh action to reload"

        Asset asset = new Asset(assetParams)
        asset.dataModel = dataModel
        asset.save(flush: true, failOnError: true)

        Long id = asset.id
        Long authorId = modelCatalogueSecurityService.currentUser?.id

        executorService.submit {
            auditService.withDefaultAuthorId(authorId) {
                Asset updated = Asset.getWithRetries(id)
                try {
                    //do the hard work
                    storeAssetWithStream(updated, assetParams.contentType?.toString(), worker)

                    updated.status = ElementStatus.FINALIZED
                    updated.description = "Your report is ready. Use Download button to download it."
                    updated.save(flush: true, failOnError: true)
                } catch (e) {
                    log.error "Exception of type ${e.class} with id=${id}", e

                    updated.refresh()
                    updated.status = ElementStatus.FINALIZED
                    updated.name = updated.name + " - Error during generation"
                    updated.description = "Error generating report: $e"
                    updated.save(flush: true, failOnError: true)
                }
            }
        }
        return asset.id;
    }

}
