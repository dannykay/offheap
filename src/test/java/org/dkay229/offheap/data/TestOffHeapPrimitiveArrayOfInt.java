package org.dkay229.offheap.data;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.dkay229.offheap.data.OffHeapBase.OffheapMemoryWasFreedException;
import org.junit.Test;

public class TestOffHeapPrimitiveArrayOfInt {

    @Test
    public void readWrite() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	for (int i = 0; i < 10; i++)
	    arr.set(i, Long.MAX_VALUE -i);
	for (int i = 0; i < 10; i++)
	    assertEquals("Read write test: " + i, Long.MAX_VALUE -i, arr.get(i));
    }

    @Test
    public void getOutOfBounds() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	Exception ex = null;
	try {
	    arr.get(10);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Is instance of ArrayIndexOutOfBoundsException", ex instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    public void setOutOfBounds() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	Exception ex = null;
	try {
	    arr.set(10, 11L);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Is instance of ArrayIndexOutOfBoundsException", ex instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    public void getNegative() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	Exception ex = null;
	try {
	    arr.get(-1);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Is instance of ArrayIndexOutOfBoundsException", ex instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    public void setNegative() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	Exception ex = null;
	try {
	    arr.set(-1, 999L);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Is instance of ArrayIndexOutOfBoundsException", ex instanceof ArrayIndexOutOfBoundsException);
    }

    @Test
    public void testFinalize() {
	OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
	Exception ex = null;
	try {
	    arr.finalize();
	    arr.get(0);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Is instance of OffheapMemoryWasFreedException", ex instanceof OffheapMemoryWasFreedException);
    }

    @Test
    public void testSerialization() {

	Exception ex = null;
	ByteArrayOutputStream serBuf = new ByteArrayOutputStream();
	try {
	    OffHeapPrimitiveArrayOfInt src = new OffHeapPrimitiveArrayOfInt(10);
	    for (int i = 0; i < 10; i++)
		src.set(i, Long.MAX_VALUE - i);
	    ObjectOutputStream oos = new ObjectOutputStream(serBuf);
	    oos.writeObject(src);
	    oos.close();
	    src.finalize();
	} catch (Exception e) {
	    ex = e;
	}
	assertNull("Serialization output had no exceptions", ex);
	OffHeapPrimitiveArrayOfInt dest = null;
	try {
	    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serBuf.toByteArray()));
	    dest = (OffHeapPrimitiveArrayOfInt) ois.readObject();
	} catch (Exception e) {
	    e.printStackTrace();
	    ex = e;
	}
	assertNull("Serialization input had no exceptions", ex);
	for (int i = 0; i < 20; i++) {
	    long v = 0L;
	    Throwable th = null;
	    try {
		v = dest.get(i);
	    } catch (Throwable e) {
		e.printStackTrace();
		th = e;
	    }
	    assertNull("No exceptions reading location " + i,th);
	    System.out.println("dst[" + i + "]=" + v);
	    assertEquals("Deserialized object validation test. Element " + i, Long.MAX_VALUE - i, v);
	}

    }
}
