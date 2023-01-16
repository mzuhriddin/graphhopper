package com.graphhopper.routing.ev;

import com.graphhopper.storage.IntsRef;
import com.graphhopper.storage.IntsRefImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaxWeightTest {

    @Test
    public void testSetAndGet() {
        DecimalEncodedValue mappedDecimalEnc = MaxWeight.create();
        mappedDecimalEnc.init(new EncodedValue.InitializerConfig());
        IntsRef intsRef = new IntsRefImpl(1);
        mappedDecimalEnc.setDecimal(false, intsRef, 20);
        assertEquals(20, mappedDecimalEnc.getDecimal(false, intsRef), .1);

        intsRef = new IntsRefImpl(1);
        mappedDecimalEnc.setDecimal(false, intsRef, Double.POSITIVE_INFINITY);
        assertEquals(Double.POSITIVE_INFINITY, mappedDecimalEnc.getDecimal(false, intsRef), .1);
    }
}