package org.apache.karaf.rcomp.api;

import aQute.bnd.annotation.headers.ProvideCapability;

@ProvideCapability( ns="rcomp")
public @interface ProvComp {
    String name();
}
