import org.modelcatalogue.core.api.ElementStatus
import org.modelcatalogue.core.security.User

fixture {
    U_Henry(User, name: "Henry", username: "Henry", password: "password", status: ElementStatus.FINALIZED)
}

