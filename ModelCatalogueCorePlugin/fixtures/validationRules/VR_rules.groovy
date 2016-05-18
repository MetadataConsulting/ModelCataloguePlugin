import org.modelcatalogue.core.ValidationRule
import org.modelcatalogue.core.api.ElementStatus

fixture{
    for (int i = 1 ; i <= 12 ; i++) {
        "VR_rule$i"(ValidationRule, name:"rule$i", description: "some random rule $i", status: i <=6 ? ElementStatus.FINALIZED : ElementStatus.DRAFT)
    }
}
