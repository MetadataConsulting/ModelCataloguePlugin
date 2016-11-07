import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_uniSubjects(EnumeratedType, status: ElementStatus.FINALIZED, name: "sub3", enumerations:['h':'history', 'p':'politics', 'sci':'science'])
}