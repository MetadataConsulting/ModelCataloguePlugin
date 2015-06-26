import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Kris(User, name: "Kris", username: "Kris", password: "password", status: ElementStatus.FINALIZED)
}

