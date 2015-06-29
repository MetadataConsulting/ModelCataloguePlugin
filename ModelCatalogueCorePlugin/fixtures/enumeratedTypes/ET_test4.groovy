import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_test4(EnumeratedType, status: ElementStatus.FINALIZED, name: "etTest4", enumerations:['m4m':'test4', 'm2m':'test2'])
}