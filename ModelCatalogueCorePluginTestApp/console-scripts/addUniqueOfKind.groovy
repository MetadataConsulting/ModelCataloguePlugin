import org.modelcatalogue.core.DataModel
import org.modelcatalogue.core.DataModelPolicy


DataModelPolicy policy = DataModelPolicy.findOrCreateByName('Unique of Kind')

assert policy

List<DataModel> models = DataModel.where {

}.build {
    isEmpty('policies')
}.list()

for (DataModel model in models) {
    model.addToPolicies(policy).save(failOnError: true)
}

policy.save(failOnError: true)
