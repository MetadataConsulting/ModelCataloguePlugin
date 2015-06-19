package org.modelcatalogue.discourse

import grails.util.GrailsNameUtils
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.Classification
import org.modelcatalogue.core.LogoutListener
import org.modelcatalogue.core.security.User


class DiscourseService implements LogoutListener {

    static transactional = false

    def grailsApplication
    def modelCatalogueSecurityService

    String findOrCreateDiscourseCategoryName(Long classificationId) {
        CategoriesForClassifications classification = CategoriesForClassifications.findByClassificationId(classificationId)

        if (classification) {
            return classification.discourseCategoryName
        }

        Classification classificationEntity = Classification.get(classificationId)
        if (!classificationEntity) {
            throw new IllegalArgumentException("Classification with ID $classificationId does not exist")
        }

        if (classificationEntity.latestVersionId) {
            classification = CategoriesForClassifications.findByClassificationId(classificationEntity.latestVersionId)
        }

        if (classification) {
            new CategoriesForClassifications(discourseCategoryName: classification.discourseCategoryName, classificationId: classificationId).save(flush: true)
            return classification.discourseCategoryName
        }


        String newName = classificationEntity.name

        if (newName.size() > 50) {
            log.warn("Classification name $newName too long, using only first 50 characters")
            newName = newName.substring(0,50)
        }

        def newClassification = discourse.categories.createCategory(newName, '283890', 'FFFFFF', classificationEntity.description)

        if (newClassification.status == 500) {
            throw new IllegalArgumentException("Cannot create classification: Discourse Server Error")
        }

        if (newClassification.data.errors) {
            if ("Category Name has already been taken" in newClassification.data.errors) {
                newClassification = discourse.categories.getCategory(newName)
                if (newClassification.status == 200) {
                    return new CategoriesForClassifications(discourseCategoryName: newName, classificationId: classificationId).save(flush: true).discourseCategoryName
                }
            }
            throw new IllegalArgumentException("Cannot create classification: ${newClassification.data.errors.join(', ')}")
        }

        String name = newClassification.data.category.name

        if (!name) {
            throw new IllegalArgumentException("Unable to get category name from $newClassification")
        }

        new CategoriesForClassifications(discourseCategoryName: name, classificationId: classificationId).save(flush: true).discourseCategoryName
    }

    Long findOrCreateDiscourseTopic(Long catalogueElementId) {
        TopicsForElements topic = TopicsForElements.findByCatalogueElementId(catalogueElementId)

        if (topic) {
            return topic.topicId
        }

        CatalogueElement element = CatalogueElement.get(catalogueElementId)

        if (!element) {
            throw new IllegalArgumentException("Catalogue Elmement with ID $catalogueElementId does not exist")
        }

        if (element.latestVersionId) {
            topic = TopicsForElements.findByCatalogueElementId(element.latestVersionId)
        }

        if (topic) {
            new TopicsForElements(topicId: topic.topicId, catalogueElementId: catalogueElementId).save(flush: true)
            return topic.topicId
        }

        String title = "${element.name} (${GrailsNameUtils.getNaturalName(element.getClass().simpleName)})"

        String categoryName = element.classifications ? findOrCreateDiscourseCategoryName(element.classifications.first().id) : null

        String link = "[Open in Model Catalogue](${element.getDefaultModelCatalogueId(true)} \"$title\")"
        String description = element.description ? "$element.description\n\n$link" : link

        def newTopic = discourse.topics.createTopic(title, description, categoryName, true)

        if (newTopic.status == 500) {
            throw new IllegalArgumentException("Cannot create classification: Discourse Server Error")
        }

        if (newTopic.data.errors) {
            throw new IllegalArgumentException("Cannot create topic: ${newTopic.data.errors.join(', ')}")
        }

        Long newTopicId = newTopic.data.topic_id

        if (!newTopicId) {
            throw new IllegalArgumentException("Unable to get topic id from $newTopic")
        }

        new TopicsForElements(topicId: newTopicId, catalogueElementId: catalogueElementId).save(flush: true).topicId
    }

    String ensureUserExistsInDiscourse(User currentUser) {
        String username = normalizeUsername currentUser.username
        def result = discourse.users.getUser(username, [:])

        if (result.status == 200) {
            if (!result.data.user.admin && currentUser.authorities.any { it.authority == 'ROLE_ADMIN'}) {
                discourse.users.grantAdmin(result.data.user.id)
            }

            return username
        }

        result = discourse.users.createUser(username, currentUser.email ?: getFallbackEmail(currentUser.username), username, UUID.randomUUID().toString(), true)

        if (result.status == 500) {
            throw new IllegalArgumentException("Cannot create user for ${currentUser.username} ($currentUser.email)): Discourse Server Error")
        }

        if (result.data.errors) {
            throw new IllegalArgumentException("Cannot create user ${currentUser.username} ($currentUser.email): ${result.data.errors}")
        }

        if (currentUser.authorities.any { it.authority == 'ROLE_ADMIN'}) {
            discourse.users.grantAdmin(result.data.user.id)
        }

        return username
    }

    org.modelcatalogue.discourse.sso.User getDiscourseUser() {
        if (!modelCatalogueSecurityService.currentUser) {
            return null
        }
        new org.modelcatalogue.discourse.sso.User(normalizeUsername(modelCatalogueSecurityService.currentUser.username), normalizeUsername(modelCatalogueSecurityService.currentUser.username), modelCatalogueSecurityService.currentUser.email ?: getFallbackEmail(modelCatalogueSecurityService.currentUser.username), modelCatalogueSecurityService.currentUser.id.toString())
    }

    /**
     * @return URL for the end users (for redirects and links)
     */
    String getDiscourseServerUrl() {
        notNull grailsApplication.config.discourse.url, "Discourse URL not set (discourse.url in Config.groovy)"
    }

    /**
     * @return URL for the API calls, defaults to #getDiscourseServerUrl()
     */
    String getDiscourseServerEndpoint() {
        notNull((grailsApplication.config.discourse.endpoint ?: grailsApplication.config.discourse.url), "Discourse API endpoint not set (discourse.endpoint or discourse.url in Config.groovy)")
    }

    boolean getDiscourseEnabled() {
        grailsApplication.config.discourse.url
    }

    boolean getDiscourseSSOEnabled() {
        discourseSSOKey as Boolean
    }

    private String getFallbackEmail(String username) {
        notNull(grailsApplication.config.discourse.users.fallbackEmail, "User $username does not have email address set and the fallback email is not set (discourse.users.fallbackEmail in Config.groovy)").replace(":username", username)
    }

    private String getDiscourseApiUser() {
        notNull grailsApplication.config.discourse.api.user, "Discourse API username not set (discourse.api.user in Config.groovy)"
    }

    private String getDiscourseApiKey() {
        notNull grailsApplication.config.discourse.api.key, "Discourse API key not set (discourse.api.key in Config.groovy)"
    }

    private String getDiscourseSSOKey() {
        grailsApplication.config.discourse.sso.key
    }

    private Discourse getDiscourse(String username = discourseApiUser) {
        if (discourseSSOKey) {
            Discourse.create(discourseServerEndpoint, discourseApiKey, username, discourseSSOKey)
        } else {
            Discourse.create(discourseServerEndpoint, discourseApiKey, username)
        }
    }

    private static String notNull(string, String message) {
        if (!string) throw new IllegalStateException(message)
        return string as String
    }

    @Override void userLoggedOut(User user) {
        if (!discourseSSOKey) {
            return
        }
        discourse.users.logOut(discourse.users.getUser(user.username).data.user.id)
    }


    static String normalizeUsername(String mcUsername) {
        mcUsername.replaceAll(/[^a-zA-Z_]/, '_')
    }
}
