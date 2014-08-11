import org.modelcatalogue.core.Model
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.CreateCatalogueElement

fixture{
    A_create_model_3(Action, batch: B_generic, type: CreateCatalogueElement)
    A_create_model_3_param_type(ActionParameter, action: A_create_model_3, name: 'type', extensionValue: Model.name)
    A_create_model_3_param_name(ActionParameter, action: A_create_model_3, name: 'name', extensionValue: 'Test Model 3')
}