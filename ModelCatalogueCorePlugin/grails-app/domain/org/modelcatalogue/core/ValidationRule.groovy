package org.modelcatalogue.core

class ValidationRule extends CatalogueElement {

    /** Rule Trigger. E.g. GEL processing received csv, GEL creating outbound csv, GEL receiving UKB picklist */
    String trigger

    /** Rule Logic. The data validation rules. */
    String rule

    /** Error Condition: i.e. does the whole file get rejected or just the data or is the data flagged to us etc. */
    String errorCondition

    /** Issue Record: i.e. where to record the issue e.g. record failure reasons to log files, flag record in database etc. */
    String issueRecord

    /** Notification: i.e. type of notification email to be sent to Notification target e.g. immediate email with log file, immediate email only, periodic reports etc. */
    String notification

    /** Notification Target: i.e. who get the notification email? E.g. GMC, GEL, UKB etc. */
    String notificationTarget

    /** Purpose: i.e. Purpose of the validation rule. */
    String purpose

    static constraints = {
        trigger(nullable: true, maxSize: 255)
        rule(nullable: true, maxSize: 10000)
        errorCondition(nullable: true, maxSize: 255)
        issueRecord(nullable: true, maxSize: 255)
        notification(nullable: true, maxSize: 255)
        notificationTarget(nullable: true, maxSize: 255)
        purpose(nullable: true, maxSize: 255)
    }

}
