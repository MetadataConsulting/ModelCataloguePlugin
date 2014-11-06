import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_test1(EnumeratedType, status: ElementStatus.FINALIZED, name: "etTest1", enumerations:['m1':'test1', 'm2':'test2'])
}