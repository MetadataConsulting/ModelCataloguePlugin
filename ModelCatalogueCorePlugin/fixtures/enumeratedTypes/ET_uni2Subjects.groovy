import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_uni2Subjects(EnumeratedType, status: ElementStatus.FINALIZED, name: "sub2", enumerations:['HISTORY':'history', 'POLITICS':'politics', 'SCIENCE':'science'])
}