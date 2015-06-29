import org.modelcatalogue.core.DataType
import org.modelcatalogue.core.api.ElementStatus

fixture{
    DT_integer(DataType, status: ElementStatus.FINALIZED, name: "integer", description: "an integer")
}