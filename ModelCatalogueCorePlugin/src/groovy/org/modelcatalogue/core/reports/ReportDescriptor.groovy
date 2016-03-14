package org.modelcatalogue.core.reports

import groovy.util.logging.Log4j
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.springframework.util.SerializationUtils

/**
 * Created by ladin on 09.06.14.
 */
@Log4j
class ReportDescriptor {

    static enum RenderType {
        /**
         * Report will be generated in new asset.
         */
        ASSET,

        /**
         * Report is plain link
         */
        LINK

    }

    LinkGenerator generator

    /**
     * Title of the report to be displayed in the front end.
     */
    Closure<String> title = { '' }

    /**
     * Default name to be used for asset generation.
     */
    Closure<String> defaultName = { '' }

    /**
     * Can set export depth for given report.
     */
    Closure<Boolean> hasExportDepth = { false }

    /**
     * List of conditions which all needs to be met to apply make this report available to given endpoint.
     */
    List<Closure> conditions = []

    RenderType renderType = RenderType.LINK

    /**
     * Additional params to be used to generate the destination URL.
     *
     * Use <code>true</code> instead of actual <code>id</code> parameter if <code>id</code> parameter is required.
     *
     * @see org.codehaus.groovy.grails.web.mapping.LinkGenerator
     */
    Closure<Map> linkParams = {[:]}

    boolean appliesTo(Object model) {
        for (condition in conditions) {
            try {
                if (!condition(model)) return false
            } catch (Exception e) {
                log.error("Error evaluating applies to for $model", e)
                return false
            }
        }
        return true
    }

    String getLink(Object model) {
        Map params = new HashMap(SerializationUtils.deserialize(SerializationUtils.serialize(linkParams(model))) as Map)
        if (params.id) {
            if (model.hasProperty('id')) {
                params.id = model.id
            } else {
                params.remove('id')
            }

        }

        if(!params.method) {
            params.method = 'GET'
        }

        if (renderType == RenderType.ASSET) {
            if (!params.params) {
                params.params = [asset: true]
            } else {
                params.params.putAll asset: true
            }
        }

        Exception cme = null

        for (int i = 0; i < 10 ; i++) {
            try {
                return generator.link(params)
            } catch (ConcurrentModificationException | ArrayIndexOutOfBoundsException e) {
                log.warn("Ignoring exception which happened generating report descriptor and retrying", e)
                cme = e
            }
        }
        throw new IllegalStateException("Unable to generate link after 10 attempts", cme)
    }

    String getTitle(Object model) {
        if (!title) return null
        title(model)
    }

    String getDefaultName(Object model) {
        if (!defaultName) return null
        defaultName(model)
    }

    Boolean getHasExportDepth(Object model) {
        if (!hasExportDepth) return null
        hasExportDepth(model)
    }
}
