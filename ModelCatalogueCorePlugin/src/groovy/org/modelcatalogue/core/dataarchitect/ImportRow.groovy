package org.modelcatalogue.core.dataarchitect

/**
 * Created by adammilward on 17/04/2014.
 */
class ImportRow {
    String dataElementCode
    String dataElementName
    String dataElementDescription
    String conceptualDomainName
    String conceptualDomainDescription
    String dataType
    String parentModelName
    String parentModelCode
    String containingModelName
    String containingModelCode
    String measurementUnitName
    String measurementSymbol
    Map metadata
    Collection<RowAction> rowActions = []
}
