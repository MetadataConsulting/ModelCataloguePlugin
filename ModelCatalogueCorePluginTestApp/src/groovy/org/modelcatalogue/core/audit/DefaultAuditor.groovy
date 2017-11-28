package org.modelcatalogue.core.audit

import groovy.util.logging.Log4j
import org.modelcatalogue.core.*
import org.modelcatalogue.core.util.FriendlyErrors
import rx.Observable
import rx.subjects.AsyncSubject
import java.util.concurrent.ExecutorService

/**
 * Default auditor auditing the changes using the change table/entity.
 */
@Log4j
class DefaultAuditor extends LoggingAuditor {

    private final ExecutorService executorService

    DefaultAuditor(ExecutorService executorService) {
        this.executorService = executorService
    }

    Observable<Long> logChange(Map <String, Object> changeProps, CatalogueElement element, boolean async) {
        AsyncSubject<Long> subject = AsyncSubject.create()
        if (!getSystem()) {

            boolean currentSystem = system

            Closure code = {

                try {
                    Change change = new Change(changeProps)
                    change.system = change.system || currentSystem
                    change.validate()

                    if (change.hasErrors()) {
                        log.warn FriendlyErrors.printErrors("Error logging ${changeProps.type} of $element", change.errors)

                        subject.onNext(0)
                        subject.onCompleted()
                        return
                    }

                    change.save()
                    subject.onNext(change.id)
                } catch (Exception e) {
                    log.error "Exception writing audit log for $element", e
                    subject.onError(e)
                }

                subject.onCompleted()
            }

            if (async) {
                executorService.submit code
            } else {
                code()
            }

        }
        return subject
    }

}
