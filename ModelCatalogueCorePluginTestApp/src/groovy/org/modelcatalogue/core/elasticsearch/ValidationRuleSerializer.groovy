package org.modelcatalogue.core.elasticsearch

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.ValidationRule

class ValidationRuleSerializer extends CatalogueElementDocumentSerializer<ValidationRule> {


    @Override
    ImmutableMap.Builder<String, Object> buildDocument(IndexingSession session, ValidationRule user, ImmutableMap.Builder<String, Object> builder) {
        super.buildDocument(session, user, builder)

        safePut(builder, 'component',  user.component)
        safePut(builder, 'rule_rocus',  user.ruleFocus)
        safePut(builder, 'trigger',  user.trigger)
        safePut(builder, 'rule',  user.rule)
        safePut(builder, 'error_condition',  user.errorCondition)
        safePut(builder, 'issue_record',  user.issueRecord)
        safePut(builder, 'notification',  user.notification)
        safePut(builder, 'notification_target',  user.notificationTarget)

        return builder
    }
}
