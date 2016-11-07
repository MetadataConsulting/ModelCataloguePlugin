import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.api.ElementStatus

fixture{
    DT_string(DataType, status: ElementStatus.FINALIZED, name: "String", description: "a string")
}