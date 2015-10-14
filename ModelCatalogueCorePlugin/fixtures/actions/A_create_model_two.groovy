import org.modelcatalogue.core.DataClass
import org.modelcatalogue.core.actions.Action
import org.modelcatalogue.core.actions.ActionParameter
import org.modelcatalogue.core.actions.CreateCatalogueElement

fixture{
    A_create_model_2(Action, batch: B_generic, type: CreateCatalogueElement)
    A_create_model_2_param_type(ActionParameter, action: A_create_model_2, name: 'type', extensionValue: DataClass.name)
    A_create_model_2_param_name(ActionParameter, action: A_create_model_2, name: 'name', extensionValue: 'Test Model 2')
}