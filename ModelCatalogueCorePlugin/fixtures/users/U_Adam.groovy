import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Adam(User, name: "Adam", username: "Adam", password: "password", status: ElementStatus.FINALIZED)
}

