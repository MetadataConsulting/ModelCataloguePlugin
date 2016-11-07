import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.EnumeratedType

fixture{
    ET_schoolSubjects(EnumeratedType, status: ElementStatus.FINALIZED, name: "sub1", enumerations:['H':'history', 'P':'politics', 'SCI':'science', 'GEO':'geography'])
}