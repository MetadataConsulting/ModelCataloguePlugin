package org.modelcatalogue.core.actions

/**
 * Created by ladin on 04.08.14.
 */
class Action {

    static hasMany = [dependsOn: ActionDependency, dependencies: ActionDependency]

    Map<String, String> parameters
    Class<? extends ActionRunner> actionClass

    ActionState state = ActionState.PENDING
    String outcome

    static constraints = {
        outcome maxSize: 10000, nullable: true, bindable: false
        state bindable: false
    }

    static mappedBy = [dependsOn: 'dependant', dependencies: 'provider']

    static mapping = {
        dependsOn lazy: false
    }

}
