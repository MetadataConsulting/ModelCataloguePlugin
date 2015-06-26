import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_John(User, name: "John", username: "John", password: "password", status: ElementStatus.FINALIZED)
}

