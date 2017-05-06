package org.dkay229.offheap.data;

public class OffHeapPackedIntegerArray extends OffHeapArray {
    public static class PackedIntegerType {
	private PackedIntegerTypeData data = new PackedIntegerTypeData(0L, 0L);

	public PackedIntegerType(int numberOfBits, boolean isSigned) {
	    super();
	    this.data.numberOfBits = numberOfBits;
	    this.data.isSigned = isSigned;
	    this.data.valueMask = 0xFFFFFFFFFFFFFFFFL >> 64-numberOfBits;
	    if (isSigned) {
		data.signBitMask=1L<<numberOfBits-1;
	    } else {
		data.signBitMask=0L;
	    }
	}

	public final int getNumberOfBits() {
	    return data.numberOfBits;
	}

	public final boolean isSigned() {
	    return data.isSigned;
	}

	public long pack(long value)
	{
	    long v=0L;
	    v&=data.valueMask;
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
