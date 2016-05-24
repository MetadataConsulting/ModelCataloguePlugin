package org.modelcatalogue.core.policy

trait KnownCheckersShortcuts {

    static String getUnique() { 'unique' }
    static String getRegex() { 'regex' }
    // Typo proof
    static String getRegexp() { 'regex' }
    static String getRequired() { 'required' }

}
