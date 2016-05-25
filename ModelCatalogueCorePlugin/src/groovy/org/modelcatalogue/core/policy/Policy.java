package org.modelcatalogue.core.policy;

import com.google.common.collect.ImmutableList;
import org.modelcatalogue.core.DataModel;

public interface Policy {

    ImmutableList<Convention> getConventions();
    void verify(DataModel policy);

}
