package org.modelcatalogue.core.scripting;

import java.util.List;

public interface Validating {

    /**
     * Implicit rule is defined by the nature of the implementing type.
     *
     * For example for enumerated type it is that the values must be contained whing the enumerated
     * type keys.
     * @return the implicit rule as rule script
     */
    String getImplicitRule();

    /**
     * Explicit rule is declared by the user.
     * @return the explicit rule declared by the user
     */
    String getExplicitRule();

    List<? extends Validating> getBases();


}
