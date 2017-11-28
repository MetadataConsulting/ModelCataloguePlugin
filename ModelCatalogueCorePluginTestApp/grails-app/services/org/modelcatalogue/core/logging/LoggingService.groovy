package org.modelcatalogue.core.logging

import org.modelcatalogue.core.AssetService
import org.modelcatalogue.core.util.builder.BuildProgressMonitor
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LoggingService {

    private final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("ddMMyyyyHHmmss")

    AssetService assetService

    Long saveLogsToAsset() {
        String timestamp = TIMESTAMP_FORMAT.format(new Date())
        assetService.storeReportAsAsset(
            null,
            name: "Logs from $timestamp",
            originalFileName: "${timestamp}.zip",
            contentType: "application/zip"
        ) { OutputStream outputStream, Long assetId ->
            BuildProgressMonitor monitor = BuildProgressMonitor.create("Export logs $timestamp", assetId)

            File logs = logsDirectory

            if (!logs) {
                monitor.onError(new IllegalStateException("Cannot find logs directory. Is application running on Tomcat?"))
                return monitor
            }


            ZipOutputStream output = new ZipOutputStream(outputStream)

            try {
                logs.eachFile { File file ->
                    output.putNextEntry(new ZipEntry(file.name.toString())) // Create the name of the entry in the ZIP
                    InputStream input = new FileInputStream(file)
                    try {
                        Files.copy(input, output)
                    } finally {
                        output.closeEntry()
                        input.close()
                    }
                }
            } catch (Exception e) {
                monitor.onError(e)
            } finally {
                output.close()
                monitor.onNext("\nLogs archive is available <a class='logs-link' href='#/catalogue/asset/$assetId/'>here</a>")
                monitor.onCompleted()
            }
        }
    }


    private static File getLogsDirectory() {
        String catalinaHome = System.getenv('CATALINA_HOME')
        if (!catalinaHome) {
            return null
        }
        File home = new File(catalinaHome)
        new File(home, 'logs')
    }

}
