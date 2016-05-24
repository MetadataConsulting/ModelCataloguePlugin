package org.modelcatalogue.core.policy;

import com.google.common.collect.ImmutableList;
import org.modelcatalogue.core.DataModel;

public interface Policy {

    void verify(DataModel policy);

}
