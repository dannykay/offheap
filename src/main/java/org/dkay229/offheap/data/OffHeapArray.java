package org.dkay229.offheap.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings({ "restriction", "serial" })
public class OffHeapArray implements Externalizable {
    Logger logger = LoggerFactory.getLogger(OffHeapArray.class);
    private static WeakHashMap<OffHeapArray,OffHeapArray> interns = new WeakHashMap<>();
    private static final Unsafe unsafe;
    private static final Exception unsafeException;
    private static final long WRITE_BUFFER_SIZE = 1024 * 50;
    private volatile long address;
    private long numElements;
    private long elementSize;
    private boolean wasFreed = false;
    private boolean isImutable=false;
    static {
	Exception ex = null;
	try {
	    Field field = Unsafe.class.getDeclaredField("theUnsafe");
	    field.setAccessible(true);
	    unsafe = (Unsafe) field.get(null);
	} catch (Exception e) {
	    ex = e;
	    throw new RuntimeException(e);
	} finally {
	    unsafeException = ex;
	}
    }

    protected static OffHeapArray intern(OffHeapArray o)
    {
	synchronized(interns)
	{	
	    OffHeapArray rtn=interns.get(o);
	    if (rtn==null) {
		interns.put(o, o);
		return o;
	    } else {
		return rtn;
	    }
	}
    }
    public final boolean isImutable() {
        return isImutable;
    }
    public void verifyMutable()
    {
	if (!isImutable)
	    throw new IsImutableException();
    }
    public final void setImutable(boolean isImutable) {
        this.isImutable = isImutable;
    }
    protected void makeImutable()
    {
	isImutable=true;
    }
    public final long getNumElements() {
        return numElements;
    }


    public final long getElementSize() {
        return elementSize;
    }

    public class OffheapMemoryWasFreedException extends RuntimeException {
	public OffheapMemoryWasFreedException() {
	    super();
	}
    }
    public class IsImutableException extends RuntimeException {
	public IsImutableException() {
	    super();
	}
    }
    public class NotImutableException extends RuntimeException {
	public NotImutableException() {
	    super();
	}
    }
    protected Unsafe getUnsafe() {
	if (unsafeException != null || unsafe == null)
	    throw new RuntimeException(unsafeException);
	if (wasFreed)
	    throw new OffheapMemoryWasFreedException();
	return unsafe;
    }

    protected long address(long i) {
	if (i < 0L || i >= this.numElements)
	    throw new ArrayIndexOutOfBoundsException((int) i);
	return address + i * elementSize;
    }

    private void init(long numElements, long elementSize)
    {
	if (numElements <= 0L)
	    throw new RuntimeException("numElements must be positive long value: " + numElements);
	this.numElements = numElements;
	if (elementSize <= 0L)
	    throw new RuntimeException("elementSize must be positive long value: " + elementSize);
	this.elementSize = elementSize;
	address = getUnsafe().allocateMemory(numElements * elementSize);
	logger.info("OffHeapBase ctor completed for size " + numElements * elementSize + " for " + this);
    }
    protected OffHeapArray(long numElements, long elementSize) {
	super();
	init(numElements,elementSize);
    }
    /**
     * Only called during serialization
     */
    @SuppressWarnings("unused")
    protected OffHeapArray() {
	super();
	logger.info("No arg constructor was called");
    }

    @SuppressWarnings("unused")
    private final Object guardian = new Object() {
	/**
	 * Implements the Finalizer Guardian pattern to ensure outer object
	 * calls base finalize
	 */
	@Override
	protected void finalize() {
	    doFinalize();
	}
    };

    private void doFinalize() {
	finalize();
    }

    @Override
    protected void finalize() {
	unsafe.freeMemory(address);
	wasFreed = true;
	logger.info("freed " + numElements*elementSize + " bytes starting at " + String.format("0x%016X", address));
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
	long numElements=in.readLong();
	long elementSize=in.readLong();
	init(numElements,elementSize);
	for (long i=0;i<numElements*elementSize;i++)
	{
	    long writeAddress=address+i;
	    unsafe.putByte(writeAddress,in.readByte());
	    logger.info("writeExternal read byte "+i+" "+String.format("0x%02X",unsafe.getByte(writeAddress)));
	    if (writeAddress%8 == 7L)
	    {
		logger.info("Wrote last of 8 bytes for a long, value is "+String.format("0x%016X",unsafe.getLong(writeAddress/8L*8L)));
	    }
	
	}
	logger.info("finished reading");
    }

    public void writeExternal(ObjectOutput out) throws IOException {
	out.writeLong(numElements);
	out.writeLong(elementSize);
	for (long i=0;i<numElements*elementSize;i++)
	{
	    logger.info("writeExternal write byte "+i+" "+String.format("0x%02X",unsafe.getByte(address+i)));
	    out.write(unsafe.getByte(address+i));
	}
	logger.info("finished writing");
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	for(long i=0;i<elementSize*numElements/4L;i++)
	    result = prime * result + unsafe.getInt(address+i+4L);
	return result;
    }
    /**
     * Equal if both OffHeapBase objects have the same number of elements of the same elment size and all the bytes
     * are equal.
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	OffHeapArray other = (OffHeapArray) obj;
	if (this.numElements!=other.numElements)
	    return false;
	if (this.elementSize!=other.elementSize)
	    return false;
	long i;
	for(i=0;i<elementSize*numElements;i++)
	    if (this.unsafe.getByte(this.address+i) != other.unsafe.getByte(other.address+i))
	    return false;
	return true;
    }


    
    
}
