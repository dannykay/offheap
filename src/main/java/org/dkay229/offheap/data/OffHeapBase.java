package org.dkay229.offheap.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Unsafe;

@SuppressWarnings({ "restriction", "serial" })
public class OffHeapBase implements Externalizable {
    Logger logger = LoggerFactory.getLogger(OffHeapBase.class);

    private static final Unsafe unsafe;
    private static final Exception unsafeException;
    private static final long WRITE_BUFFER_SIZE = 1024 * 50;
    private volatile long address;
    private long numElements;
    private long elementSize;
    private boolean wasFreed = false;
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

    public class OffheapMemoryWasFreedException extends RuntimeException {
	public OffheapMemoryWasFreedException() {
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
	return address + i * numElements;
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
    protected OffHeapBase(long numElements, long elementSize) {
	super();
	init(numElements,elementSize);
    }
    /**
     * Only called during serialization
     */
    @SuppressWarnings("unused")
    protected OffHeapBase() {
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
}
