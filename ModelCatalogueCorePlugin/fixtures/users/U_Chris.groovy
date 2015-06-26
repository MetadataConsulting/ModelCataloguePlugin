import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Chris(User, name: "Chris", username: "Chris", password: "password", status: ElementStatus.FINALIZED)
}

