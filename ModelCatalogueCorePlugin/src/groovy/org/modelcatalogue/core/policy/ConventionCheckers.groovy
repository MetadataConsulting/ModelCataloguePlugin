package org.modelcatalogue.core.policy

import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.HibernateHelper

import java.util.regex.Matcher
import java.util.regex.Pattern

class ConventionCheckers {

    private static final Pattern EXT_PATTERN = Pattern.compile(/^ext\[(.*?)\]$/)

    static Object getPropertyOrExtension(CatalogueElement item, String property) {
        Matcher matcher = EXT_PATTERN.matcher(property);

        if (matcher.matches()) {
            String extKey =  matcher.group(1)
            return item.ext.get(extKey)
        }

        if (!item.hasProperty(property)) {
            return null
        }
        return item.getProperty(property)
    }

    static String getValueOrName(Object value) {
        if (!value) {
            return ""
        }
        if (CatalogueElement.isAssignableFrom(HibernateHelper.getEntityClass(value))) {
            return ((CatalogueElement)value).name
        }
        return value
    }
}
