package uk.co.mc.core

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

/*
* Measurement units are units such as MPH, cm3, cm2, m etc.
* They are used by value domains to instantiate a data element
*
* */

class MeasurementUnit extends CatalogueElement {

    String symbol

    static constraints = {
        symbol nullable: true, size: 1..100
    }

    String toString() {
        "${getClass().simpleName}[id: ${id}, name: ${name}]"
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MeasurementUnit)) {
            return false;
        }
        if (this.is(obj)) {
            return true;
        }
        MeasurementUnit ce = (MeasurementUnit) obj;
        return new EqualsBuilder()
                .append(name, ce.name)
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .toHashCode();
    }

}
