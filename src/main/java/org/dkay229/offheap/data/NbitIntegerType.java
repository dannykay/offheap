package org.dkay229.offheap.data;

import java.util.ArrayList;
import java.util.List;

public class NbitIntegerType {
    public static final int MAX_NUM_BITS = 64;
    private final int numberOfBits;
    private final boolean isSigned;
    private final long signBitMask;
    private final long maxValue;
    private final long minValue;
    private static final List<NbitIntegerType> signedTypes = new ArrayList<>(MAX_NUM_BITS);
    private static final List<NbitIntegerType> unsignedTypes = new ArrayList<>(MAX_NUM_BITS);

    static {
	    for (int i = 2; i <= MAX_NUM_BITS; i++) {
		signedTypes.add(new NbitIntegerType(i, true));
		unsignedTypes.add(new NbitIntegerType(i, false));
	    }
    }

    private NbitIntegerType(int numberOfBits, boolean isSigned) {
	this.isSigned = isSigned;
	this.numberOfBits = numberOfBits;
	long valMsk = 0xFFFFFFFFFFFFFFFFL >>> (64 - numberOfBits );
	if (isSigned) {
	    valMsk >>>= 1;
	    signBitMask = 1L << numberOfBits - 1;
	} else {
	    signBitMask = 0L;
	}
	this.maxValue = valMsk;
	this.minValue = isSigned? 0 - maxValue:0;
    }
    
    public static final NbitIntegerType getSmallestTypeToHold(long value,boolean signed)
    {
	for (NbitIntegerType type: (signed?signedTypes:unsignedTypes))
	{
	    if (value<=type.maxValue) {
		return type;
	    }
	}
	throw new RuntimeException("Internal Error"+value);
    }
 
    public final int getNumberOfBits() {
        return numberOfBits;
    }

    public final boolean isSigned() {
        return isSigned;
    }

    public final long getSignBitMask() {
        return signBitMask;
    }

    public final long getMaxValue() {
        return maxValue;
    }

    public final long getMinValue() {
        return minValue;
    }

    @Override
    public String toString() {
	
	return String.format("bitIntegerType [numberOfBits=%02d isSigned=%5s signBitMask=0x%016X maxValue=0x%016X maxDecimal=%s minDecimal=%d",numberOfBits,isSigned?"true":"false",signBitMask,maxValue,maxValue,minValue);
    }
}