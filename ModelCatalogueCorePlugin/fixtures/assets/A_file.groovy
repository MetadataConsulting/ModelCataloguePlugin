import org.modelcatalogue.core.Asset
import org.modelcatalogue.core.PublishedElementStatus

fixture{
    A_file(Asset, name:"file", description: "random file", status: PublishedElementStatus.FINALIZED)
}