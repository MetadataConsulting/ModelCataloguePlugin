import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Gina(User, name: "Gina", username: "Gina", password: "password", status: ElementStatus.FINALIZED)
}

