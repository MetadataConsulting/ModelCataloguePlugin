import org.modelcatalogue.core.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Emily(User, name: "Emily", username: "Emily", password: "password", status: ElementStatus.FINALIZED)
}

