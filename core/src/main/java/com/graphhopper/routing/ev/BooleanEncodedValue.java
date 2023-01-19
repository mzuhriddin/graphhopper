package com.graphhopper.routing.ev;

import com.graphhopper.storage.IntsRef;

/**
 * This interface defines access to an edge property of type boolean. The default value is false.
 */
public interface BooleanEncodedValue extends EncodedValue {
    default void setBool(boolean reverse, IntsRef ref, boolean value) {
        setBool(reverse, -1, ref, value);
    }

    void setBool(boolean reverse, int edgeId, IntsRef ref, boolean value);

    default boolean getBool(boolean reverse, IntsRef ref) {
        return getBool(reverse, -1, ref);
    }

    boolean getBool(boolean reverse, int edgeId, IntsRef ref);
}
