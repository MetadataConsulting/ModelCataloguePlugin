package org.modelcatalogue.core.errors

import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException
import javax.servlet.http.HttpServletRequest

class MetadataExceptionResolver extends GrailsExceptionResolver {

    @Override
    protected void logStackTrace(Exception e, HttpServletRequest request) {
        if ( e instanceof AccessDeniedException ) {
            LOG.error(exceptionProcessingRequestDescription(request, 'AccessDeniedException'))
            return
        }
        if ( e instanceof NotFoundException) {
            LOG.error(exceptionProcessingRequestDescription(request, 'NotFoundException'))
            return
        }
        super.logStackTrace(e, request)
    }

    String exceptionProcessingRequestDescription(HttpServletRequest request, String exceptionName) {
        StringBuilder sb = new StringBuilder()
        sb.append(exceptionName)
                .append(" occurred when processing request: ")
                .append("[").append(request.getMethod().toUpperCase()).append("] ")
        String forwardRequestUriAttribute = request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE)
        sb.append(forwardRequestUriAttribute ? forwardRequestUriAttribute : request.requestURI)
        sb.toString()
    }
}
