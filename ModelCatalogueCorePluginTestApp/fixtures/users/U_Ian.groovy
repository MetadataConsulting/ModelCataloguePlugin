import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Ian(User, name: "Ian", username: "Ian", password: "password", status: ElementStatus.FINALIZED)
}

