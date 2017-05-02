/**
 * 
 */
package org.dkay229.offheap.data;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dkay
 *
 */
@SuppressWarnings("restriction")
public class OffHeapPrimitiveArrayOfLong extends OffHeapBase implements Serializable {
    Logger logger = LoggerFactory.getLogger(OffHeapBase.class);

    public OffHeapPrimitiveArrayOfLong() {
	super();
    }

    protected static final long BYTES_PER_LONG = 8L;

    public OffHeapPrimitiveArrayOfLong(long size) {
	super(size, BYTES_PER_LONG);
    }

    public long get(long index) {
	long value = getUnsafe().getLong(address(index));
	logger.debug("get[ "+index+"]="+String.format("0x%016X", value));
	return value;
    }

    public long set(long index, long value) {
	verifyMutable();
	getUnsafe().putLong(address(index), value);
	logger.debug("write[ "+index+"]="+String.format("0x%016X", value));
	return value;
    }
    public void makeImutable()
    {
	super.makeImutable();
    }
}
