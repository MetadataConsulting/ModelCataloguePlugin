import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_gender(EnumeratedType, status: ElementStatus.FINALIZED, name: "gender", enumerations:['m':'male', 'f':'female', 'u':'unknown', 'ns':'not specified'])
}