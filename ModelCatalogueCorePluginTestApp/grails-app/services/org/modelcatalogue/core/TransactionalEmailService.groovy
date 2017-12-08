package org.modelcatalogue.core

class TransactionalEmailService {

    def mailService

    void sendEmail(String emailRecipient, String emailFrom, String emailSubject, String body) {
        mailService.sendMail {
            to emailRecipient
            from emailFrom
            subject emailSubject
            html body
        }
    }
}
