package org.modelcatalogue.gel.export

import grails.gorm.DetachedCriteria
import grails.gsp.PageRenderer
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.Relationship
import org.modelcatalogue.core.RelationshipType
import org.modelcatalogue.core.util.Metadata
import org.modelcatalogue.core.util.builder.ProgressMonitor
import org.modelcatalogue.gel.GenomicsService

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class RareDiseasesWebsiteExporter {

    final GenomicsService genomicsService
    final DataModel dataModel
    final PageRenderer pageRenderer
    final ProgressMonitor monitor

    RareDiseasesWebsiteExporter(GenomicsService genomicsService, DataModel dataModel, PageRenderer pageRenderer, ProgressMonitor monitor = ProgressMonitor.NOOP) {
        this.genomicsService = genomicsService
        this.dataModel = dataModel
        this.pageRenderer = pageRenderer
        this.monitor = monitor
    }

    void export(OutputStream outputStream) {
        try {
            monitor.onNext("Finding diseases...")
            List<DataClass> diseases = genomicsService.findRareDiseases(dataModel).items
            int size = diseases.size()
            monitor.onNext("... found ${size} diseases")

            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)
            zipOutputStream.withStream {
                copyResource(it, 'inc/bootstrap-responsive.min.css')
                copyResource(it, 'inc/filterTable.js')
                copyResource(it, 'inc/jquery.min.js')
                copyResource(it, 'inc/search.png')
                copyResource(it, 'inc/style.css')
                copyResource(it, 'inc/united.min.css')

                monitor.onNext("Writing index file")
                writeIndexFile(it, diseases)

                diseases.eachWithIndex { DataClass disease, Integer index ->
                    if (disease.ext[Metadata.WEBSITE_SKIP] == 'true') {
                        monitor.onNext("Skipping $disease.name as it has ${Metadata.WEBSITE_SKIP} flag set")
                        return
                    }
                    monitor.onNext("Writing page detail file for ${disease.name} (${index + 1} of $size)")
                    writeDetailPage(it, disease)
                }
            }
            monitor.onNext("Finished website generation")
            monitor.onCompleted()
        } catch (Exception e) {
            monitor.onError(e)
        }
    }

    void writeDetailPage(ZipOutputStream zos, DataClass disease) {
        zos.putNextEntry(new ZipEntry("${getId(disease)}.html"))
        pageRenderer.renderTo(view: '/rareDiseasesWeb/page', model: [
            disease: disease,
            eligibility: findEligibility(disease),
            phenotypes: findPhenotypes(disease),
            clinicalReports: findClinicalReports(disease)
        ], zos)
        zos.closeEntry()
    }

    void writeIndexFile(ZipOutputStream zos, List<DataClass> diseases) {
        zos.putNextEntry(new ZipEntry('index.html'))
        pageRenderer.renderTo(view: '/rareDiseasesWeb/index', model: [diseases: diseases], zos)
        zos.closeEntry()
    }

    static void copyResource(ZipOutputStream zos, String name) {
        zos.putNextEntry(new ZipEntry(name))
        zos << RareDiseasesWebsiteExporter.getResourceAsStream("rare_diseases_web/$name")
        zos.closeEntry()
    }


    static Map<String, DataClass> findEligibility(DataClass parent) {
        DataClass eligibility = findChild parent, '%eligibility%'

        if (!eligibility) {
            return [:]
        }

        DataClass inclusionCriteria = findChild eligibility, '%inclusion%'
        DataClass exclusionCriteria = findChild eligibility, '%exclusion%'
        DataClass pgc = findChild eligibility, '%prior genetic%'
        DataClass genes = findChild eligibility, '%genes%'
        DataClass closingStmt = findChild eligibility, '%closing%'

        [
            eligibility: eligibility,
            inclusionCriteria: inclusionCriteria,
            exclusionCriteria: exclusionCriteria,
            pgt: pgc,
            genes: genes,
            cs: closingStmt

        ]
    }

    static String getId(CatalogueElement disease) {
        if (disease.hasModelCatalogueId() && !disease.getModelCatalogueId().startsWith('http')) {
            return "${disease.getModelCatalogueId()}.${disease.versionNumber}"
        }
        return "${disease.latestVersionId ?: disease.id}.${disease.versionNumber}"
    }

    static DataClass findPhenotypes(DataClass parent) {
        findChild parent, '%phenotypes%'
    }

    static DataClass findClinicalReports(DataClass parent) {
        findChild parent, '%clinical%tests%'
    }

    static DataClass findChild(DataClass parent, String likeString) {
        List<Relationship> results = new DetachedCriteria<Relationship>(Relationship).build {
            eq('relationshipType', RelationshipType.hierarchyType)
            eq('source', parent)
            destination {
                or {
                    ilike('name', likeString)
                }
            }
        }.list(max: 1)

        if (results) {
            return results.first().destination as DataClass
        }

        return null
    }
}









