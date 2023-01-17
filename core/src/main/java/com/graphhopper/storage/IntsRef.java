/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.storage;

import java.util.Arrays;

/**
 * Idea and most of the code is from Lucene. But the variables are final, except for the array content.
 */
public final class IntsRef implements Comparable<IntsRef> {
    /**
     * An IntsRef with an array of size 0.
     */
    public static final IntsRef EMPTY = new IntsRef(0, false);
    /**
     * The contents of the IntsRef. Cannot be {@code null}.
     */
    public final int[] ints;
    /**
     * Create a IntsRef pointing to a new int array of size <code>capacity</code> leading to capacity*32 bits.
     * Offset will be zero and length will be the capacity.
     */
    public IntsRef(int capacity) {
        this(capacity, true);
    }

    private IntsRef(int capacity, boolean checked) {
        if (checked && capacity == 0)
            throw new IllegalArgumentException("Use instance EMPTY instead of capacity 0");
        ints = new int[capacity];
    }

    /**
     * This instance will directly reference ints w/o making a copy.
     * ints should not be null.
     */
    public IntsRef(int[] ints) {
        this.ints = ints;
        assert isValid();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        final int end = ints.length;
        for (int i = 0; i < end; i++) {
            result = prime * result + ints[i];
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof IntsRef) {
            return this.intsEquals((IntsRef) other);
        }
        return false;
    }

    public boolean intsEquals(IntsRef other) {
        if (ints.length == other.ints.length) {
            int otherUpto = 0;
            final int[] otherInts = other.ints;
            final int end = ints.length;
            for (int upto = 0; upto < end; upto++, otherUpto++) {
                if (ints[upto] != otherInts[otherUpto]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Signed int order comparison
     */
    @Override
    public int compareTo(IntsRef other) {
        if (this == other) return 0;
        final int[] aInts = this.ints;
        int aUpto = 0;
        final int[] bInts = other.ints;
        int bUpto = 0;
        final int aStop = aUpto + Math.min(this.ints.length, other.ints.length);
        while (aUpto < aStop) {
            int aInt = aInts[aUpto++];
            int bInt = bInts[bUpto++];
            if (aInt > bInt) {
                return 1;
            } else if (aInt < bInt) {
                return -1;
            }
        }
        // One is a prefix of the other, or, they are equal:
        return this.ints.length - other.ints.length;
    }

    /**
     * Creates a new IntsRef that points to a copy of the ints from
     * <code>other</code>
     * <p>
     * The returned IntsRef will have a length of other.length
     * and an offset of zero.
     */
    public static IntsRef deepCopyOf(IntsRef other) {
        return new IntsRef(Arrays.copyOf(other.ints, other.ints.length));
    }

    /**
     * Performs internal consistency checks.
     * Always returns true (or throws IllegalStateException)
     */
    public boolean isValid() {
        if (ints == null) {
            throw new IllegalStateException("ints is null");
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        final int end = ints.length;
        for (int i = 0; i < end; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(Integer.toHexString(ints[i]));
        }
        sb.append(']');
        return sb.toString();
    }

    public boolean isEmpty() {
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] != 0)
                return false;
        }
        return true;
    }
}