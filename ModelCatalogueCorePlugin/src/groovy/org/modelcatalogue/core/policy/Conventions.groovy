package org.modelcatalogue.core.policy

import com.google.common.collect.ImmutableMap
import org.modelcatalogue.core.CatalogueElement
import org.modelcatalogue.core.util.HibernateHelper

import java.util.regex.Matcher
import java.util.regex.Pattern

class Conventions {

    private static final Pattern EXT_PATTERN = Pattern.compile(/^ext\[(.*?)\]$/)
    private static ImmutableMap<String, ConventionChecker> CHECKERS

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

    static String getExtensionAlias(String extensionKey) {
        return "ext[$extensionKey]"
    }

    static boolean isExtensionAlias(String property) {
        return EXT_PATTERN.matcher(property).matches()
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

    static ImmutableMap<String, ConventionChecker> getCheckers() {
        if (CHECKERS != null) {
            return CHECKERS
        }
        ServiceLoader<ConventionChecker> loader = ServiceLoader.load(ConventionChecker)

        ImmutableMap.Builder<String, ConventionChecker> checkers = ImmutableMap.builder()
        for (ConventionChecker checker in loader) {
            checkers.put(getCheckerName(checker), checker)
        }
        return CHECKERS = checkers.build()
    }

    private static String getCheckerName(ConventionChecker checker) {
        String name = checker.getClass().simpleName
        name[0].toLowerCase() + name[1..(name.lastIndexOf('Checker') - 1)]
    }


}
