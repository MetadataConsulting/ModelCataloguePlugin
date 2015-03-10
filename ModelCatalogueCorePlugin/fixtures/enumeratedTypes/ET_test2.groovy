import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_test2(EnumeratedType, status: ElementStatus.FINALIZED, name: "etTest2", enumerations:['m2m':'test2', 'm3m':'test3'])
}