package org.modelcatalogue.core.util

import org.modelcatalogue.core.ValueDomain

abstract class ValueDomainRuleScript  extends Script {

    static final Class<BigDecimal> decimal  = BigDecimal
    static final Class<BigDecimal> number   = BigDecimal
    static final Class<BigInteger> integer  = BigInteger
    static final Class<String> string       = String

    /**
     * Validates that the 'x' variable can be converted to given type and if it is possible
     * it casts the variable to given type and store back to the binding.
     *
     * @param type tested type
     * @return true if the variable 'x' can be (and has been) converted to given type
     */
    boolean is(Class type) {
        if (!type) return false
        if (type == String) return x = string(x)
        try {
            x = x.asType(type)
            return true
        } catch (Exception e) {
            exception = e
        }
        try {
            x = type.newInstance([x] as Object[])
            return true
        } catch (Exception e) {
            exception = e
        }
        return false
    }

    /**
     * Converts object to it's string representation.
     *
     * Return empty string for null objects.
     *
     * @param o object to be converted
     * @return string representation of given object
     */
    String string(Object o) {
        if (o == null) return ""
        if (o instanceof String) return o
        return o.toString()
    }

    /**
     * Converts object to it's numeric representation.
     *
     * Return zero for null objects or non-numeric values but fills the exception variable with the exception thrown.
     *
     * @param o object to be converted
     * @return numeric representation of given object
     */
    Number number(Object o) {
        if (o == null) return 0
        if (o instanceof Number) return o
        try {
            return new BigDecimal(o.toString())
        } catch(e) {
            exception = e
            return 0
        }
    }

    /**
     * Check if string representations of the 'x' variable is the same as string representation of the value parameter.
     * @param value the value to be fixed
     * @return true if string representations of the 'x' variable is the same as string representation of the value parameter
     */
    boolean fixed(Object value) {
        string(x) == string(value)
    }

    /**
     * Check if string representations of the 'x' variable is longer or equal than the minimal length.
     * @param length the minimal length
     * @return true if string representations of the 'x' variable is longer or equal than the minimal length
     */
    boolean minLength(int length) {
        string(x).size() >= length
    }

    /**
     * Check if string representations of the 'x' variable is shorter or equal than the maximal length.
     * @param length the maximal length
     * @param length the maximal length
     * @return true if string representations of the 'x' variable is shorter or equal than the maximal length
     */
    boolean maxLength(int length) {
        string(x).size() <= length
    }

    /**
     * Check if string representations of the 'x' variable has exactly the defined length.
     * @param length desired length
     * @param length desired length
     * @return true if string representations of the 'x' variable has exactly the defined length
     */
    boolean length(int length) {
        string(x).size() == length
    }

    /**
     * Check if the numeric representation of the 'x' variable is greater than or the same as the limit.
     * @param limit the limit
     * @return true if the numeric representation of the 'x' variable is greater or the same as the limit
     */
    boolean minInclusive(Number limit) {
        number(x) >= limit
    }

    /**
     * Check if the numeric representation of the 'x' variable is smaller than or the same as the limit.
     * @param limit the limit
     * @return true if the numeric representation of the 'x' variable is greater or the same as the limit
     */
    boolean maxInclusive(Number limit) {
        number(x) <= limit
    }

    /**
     * Check if the numeric representation of the 'x' variable is greater than the limit.
     * @param limit the limit
     * @return true if the numeric representation of the 'x' variable is greater or the same as the limit
     */
    boolean minExclusive(Number limit) {
        number(x) > limit
    }

    /**
     * Check if the numeric representation of the 'x' variable is smaller than as the limit.
     * @param limit the limit
     * @return true if the numeric representation of the 'x' variable is greater or the same as the limit
     */
    boolean maxExclusive(Number limit) {
        number(x) < limit
    }

    Object getX() {
        if (!binding.hasVariable('x')) {
            return null
        }
        binding.getVariable('x')
    }

    void setX(Object o) {
        binding.setVariable('x', o)
    }

    Object getException() {
        if (!binding.hasVariable('exception')) {
            return null
        }
        binding.getVariable('exception')
    }

    void setException(Exception e) {
        binding.setVariable('exception', e)
    }

    ValueDomain getDomain() {
        if (!binding.hasVariable('domain')) {
            return null
        }
        binding.getVariable('domain')
    }

    void setDomain(ValueDomain domain) {
        binding.setVariable('domain', domain)
    }
}
