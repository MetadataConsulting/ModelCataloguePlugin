import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Frank(User, name: "Frank", username: "Frank", password: "password", status: ElementStatus.FINALIZED)
}

