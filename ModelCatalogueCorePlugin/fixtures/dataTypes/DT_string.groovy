import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.ElementStatus

fixture{
    DT_string(DataType, status: ElementStatus.FINALIZED, name: "String", description: "a string")
}