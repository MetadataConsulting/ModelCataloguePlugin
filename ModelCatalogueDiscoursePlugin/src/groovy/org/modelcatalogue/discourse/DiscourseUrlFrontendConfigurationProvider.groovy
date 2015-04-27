package org.modelcatalogue.discourse

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.modelcatalogue.core.util.js.FrontendConfigurationProvider
import org.springframework.beans.factory.annotation.Autowired

class DiscourseUrlFrontendConfigurationProvider extends  FrontendConfigurationProvider {

    @Autowired DiscourseService discourseService

    @Override
    String getJavascriptConfiguration() {
        //language=JavaScript
        """
        angular.module('mc.core.comments.discourseUrl', ['mc.core.comments']).value('discourseUrl', '${discourseService.discourseServerUrl}').value('discourseSSOEnabled', ${discourseService.discourseSSOEnabled});
        modelcatalogue.registerModule('mc.core.comments.discourseUrl');
        """
    }
}
