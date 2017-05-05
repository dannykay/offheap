package org.dkay229.offheap.data;

public class OffHeapPackedIntegerArray extends OffHeapArray {
    public static class PackedIntegerType {
	private final int numberOfBits;
	private final boolean isSigned;
	private final long valueMask;
	private final long signBitMask;
	private final long maxValue=0L;
	private final long minValue=0L;
	
	public PackedIntegerType(int numberOfBits, boolean isSigned) {
	    super();
	    this.numberOfBits = numberOfBits;
	    this.isSigned = isSigned;
	    this.valueMask = 0xFFFFFFFFFFFFFFFFL >> 64-numberOfBits;
	    if (isSigned) {
		signBitMask=1L<<numberOfBits-1;
	    } else {
		signBitMask=0L;
	    }
	}

	public final int getNumberOfBits() {
	    return numberOfBits;
	}

	public final boolean isSigned() {
	    return isSigned;
	}

	public long pack(long value)
	{
	    long v=0L;
	    v&=valueMask;
	    return v;
	}
	public String getBitString(long v)
	{
	    StringBuilder sb=new StringBuilder();
	    long bitMask=1L;
	    for (int i=0;i<64;i++) {
		sb.append(((v&bitMask)>0L)?1:0);
		bitMask<<=1;
		if (i%8==7) {
		    sb.append(" ");
		}
	    }
	    return sb.reverse().toString();
	}
    }
}
