package org.dkay229.offheap.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class NbitIntegerTypeTest {

    @Test
    public void returnRightSignedSizeTest() {
	for (long i = 0; i < 63; i++) {
	    long val = 1L << i;
	    NbitIntegerType type = NbitIntegerType.getSmallestTypeToHold(val, true);
	    System.out.println("".format("%02d 0x%016X", i, val) + type);
	    assertTrue("is signed", type.isSigned());
	    assertTrue("holds value", type.getMaxValue() >= val);
	    assertTrue("MIN value", type.getMinValue() <= 0L - val);
	    assertEquals("bit count", i + 2, type.getNumberOfBits());
	    assertEquals("sign bitmask", 1L << i + 1, type.getSignBitMask());
	}
    }

    @Test
    public void returnRightUnSignedSizeTest() {
	for (long i = 0; i < 63; i++) {
	    long val = 1L << i;
	    NbitIntegerType type = NbitIntegerType.getSmallestTypeToHold(val, false);
	    System.out.println("".format("%02d 0x%016X", i, val) + type+" "+type.getSignBitMask());
	    assertFalse("is signed", type.isSigned());
	    assertTrue("holds value", type.getMaxValue() >= val);
	    assertEquals("MIN is zero", 0L,type.getMinValue());
	    
//	    assertEquals("bit count", i + 2, type.getNumberOfBits());
	    assertEquals("sign bitmask", 0L, type.getSignBitMask());
	}
    }


}
