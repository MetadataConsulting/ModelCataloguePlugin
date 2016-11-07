import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_test5(EnumeratedType, status: ElementStatus.FINALIZED, name: "etTest5", enumerations:['m5m':'test5', 'm2m':'test2'])
}