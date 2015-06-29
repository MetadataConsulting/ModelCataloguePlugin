import org.modelcatalogue.core.DataElement
import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.api.ElementStatus

fixture{
    DE_patient_temperature_uk(DataElement, name:"patient temperature uk", description: "Patient's Temperature in the UK", status: ElementStatus.FINALIZED, valueDomain: VD_degree_C)
}