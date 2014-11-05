import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Lily(User, name: "Lily", username: "Lily", password: "password", status: ElementStatus.FINALIZED)
}

