import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.api.ElementStatus

fixture{
    A_file(Asset, name:"file", description: "random file", status: ElementStatus.FINALIZED)
}
