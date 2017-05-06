package org.dkay229.offheap.data;

public class PackedIntegerTypeData {
	public int numberOfBits;
	public boolean isSigned;
	public long valueMask;
	public long signBitMask;
	public long maxValue;
	public long minValue;

	public PackedIntegerTypeData(long numberOfBits,boolean isSigned) {
		this.isSigned=isSigned;
		long valueMask=0xFFFFFFFFFFFFFFFFL >> (64 - numberOfBits-1);
		if (isSigned) {
			valueMask>>=1;
			signBitMask= 1<<numberOfBits-1;
		} else {
			signBitMask=0L;
		}
		this.maxValue = 0xFFFFFFFFFFFFFFFFL >> (64 - numberOfBits-1);
		this.minValue = 0-maxValue;
	}
}