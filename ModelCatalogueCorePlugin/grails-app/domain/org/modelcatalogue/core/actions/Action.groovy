package org.modelcatalogue.core.actions

/**
 * Created by ladin on 04.08.14.
 */
class Action {

    static hasMany = [dependsOn: Action]

    Map<String, String> parameters
    Class<? extends ActionRunner> actionClass

    ActionState state = ActionState.PENDING
    String outcome

    static constraints = {
        outcome maxSize: 10000, nullable: true
    }

}
