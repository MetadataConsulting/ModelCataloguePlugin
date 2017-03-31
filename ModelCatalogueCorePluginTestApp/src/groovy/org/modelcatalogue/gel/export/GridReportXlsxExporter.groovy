package org.modelcatalogue.gel.export

import com.google.common.collect.ImmutableMap
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.DataClassService
import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.diff.CatalogueElementDiffs
import org.modelcatalogue.core.export.inventory.CatalogueElementToXlsxExporter
import org.modelcatalogue.core.util.DataModelFilter
import org.modelcatalogue.core.util.HibernateHelper


/**
 * GridReportXlsxExporter.groovy
 * Purpose: Generate an excel report from a data model, including metadata using the required format
 *
 * @author Adam Milward
 * @version 31/03/2017
 */
class GridReportXlsxExporter extends CatalogueElementToXlsxExporter {


    static GridReportXlsxExporter forDataModel(DataModel element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new GridReportXlsxExporter(element, dataClassService, grailsApplication, depth)
    }

    static GridReportXlsxExporter forDataClass(DataClass element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3) {
        return new GridReportXlsxExporter(element, dataClassService, grailsApplication,  depth)
    }


    private GridReportXlsxExporter(CatalogueElement element, DataClassService dataClassService, GrailsApplication grailsApplication, Integer depth = 3){
        super(element, dataClassService, grailsApplication, depth)
    }

    @Override
    void export(OutputStream outputStream) {
        CatalogueElement element = CatalogueElement.get(elementId)
        def totalDepth = getDepth(element)
        println(totalDepth)
    }


    private getDepth(CatalogueElement element){

        List<DataClass> dataClasses = Collections.emptyList()

        if (HibernateHelper.getEntityClass(element) == DataClass) {
            dataClasses = [element as DataClass]
        } else if (HibernateHelper.getEntityClass(element) == DataModel) {
            dataClasses = dataClassService.getTopLevelDataClasses(DataModelFilter.includes(element as DataModel), ImmutableMap.of('status', 'active'), true).items
        }


        dataClasses.each{ dc->

            println(dataClassService.getInnerClasses())
            println(dataClassService.getDataElementsFromClasses())
        }



    }





}
