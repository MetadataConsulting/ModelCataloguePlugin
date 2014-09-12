import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.PublishedElementStatus

fixture{
    DE_patient_temperature_uk(DataElement, name:"patient temperature uk", description: "Patient's Temperature in the UK", status: PublishedElementStatus.FINALIZED, valueDomain: VD_degree_C)
}