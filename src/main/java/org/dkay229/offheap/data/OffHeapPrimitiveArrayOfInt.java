/**
 * 
 */
package org.dkay229.offheap.data;

/**
 * @author dkay
 *
 */
@SuppressWarnings("restriction")
public class OffHeapPrimitiveArrayOfInt extends OffHeapBase {
	private final long address;
	private final long size;
	
	public OffHeapPrimitiveArrayOfInt(long size) {
		if (size<0L)
			throw new RuntimeException("Size cannot be negative: "+size);
		this.size=size;
		address=getUnsafe().allocateMemory(size*BYTES_PER_LONG);
	}
	private long address(long i)
	{
		if (i<0L || i>=size)
			throw new ArrayIndexOutOfBoundsException((int)i);
		return address+i*BYTES_PER_LONG;
	}
	public long get(long i)
	{
		return getUnsafe().getLong(address(i));
	}
	public long set(long index,long value)
	{
		getUnsafe().putLong(address(index), value);
		return value;
	}
}
