package org.dkay229.offheap.data;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.dkay229.offheap.data.OffHeapArray.OffheapMemoryWasFreedException;
import org.junit.Test;

public class TestOffHeapPrimitiveArrayOfLong {

    @Test
    public void readWrite() {
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
	for (int i = 0; i < 10; i++)
	    arr.set(i, Long.MAX_VALUE - i);
	for (int i = 0; i < 10; i++)
	    assertEquals("Read write test: " + i, Long.MAX_VALUE - i, arr.get(i));
    }

    @Test
    public void getOutOfBounds() {
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
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
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
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
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
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
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
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
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(10);
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
    public void testNegSize() {
	Exception ex = null;
	try {
	    OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(-1);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Neg Size", ex instanceof RuntimeException);
    } 
    @Test
    public void testZeroSize() {
	Exception ex = null;
	try {
	    OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(0);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Neg Size", ex instanceof RuntimeException);
    } 
    @Test
    public void testNegEleSize() {
	Exception ex = null;
	try {
	    OffHeapArray arr = new OffHeapArray(10,-1);
	} catch (Exception e) {
	    ex = e;
	}
	assertTrue("Neg Size", ex instanceof RuntimeException);
    } 
    @Test
    public void testEquals() {
	OffHeapPrimitiveArrayOfLong arr1 = new OffHeapPrimitiveArrayOfLong(1000);
	OffHeapPrimitiveArrayOfLong arr2 = new OffHeapPrimitiveArrayOfLong(1000);
	for (long i=0;i<arr1.getNumElements();i++)
	{
	    arr2.set(i, arr1.set(i, Long.MIN_VALUE+i));
	}
	assertTrue("equals check",arr1.equals(arr2));
	assertTrue("equals check",arr1.equals(arr1));
	assertFalse("equals check",arr1.equals(null));
	assertFalse("equals check",arr1.equals("x"));
	arr2.set(999L, 137L);
	assertFalse("equals check",arr1.equals(arr2));
	assertFalse("equals check",arr1.equals(new OffHeapPrimitiveArrayOfLong(1)));
	assertFalse("equals check",new OffHeapArray(20L,4L).equals(new OffHeapArray(20L,8L)));
    }

    
    @Test
    public void sequentialReadWriteTest() {
	OffHeapPrimitiveArrayOfLong arr = new OffHeapPrimitiveArrayOfLong(3000);
	for (long i = 0; i < arr.getNumElements(); i++) {
	    arr.set(i, Long.MIN_VALUE + i);
	    if (i > 0) {
		assertEquals("Prior element is not corrupted", Long.MIN_VALUE + i -1, arr.get(i - 1));
	    }
	}

    }
    

    @Test
    public void testSerialization() {

	Exception ex = null;
	ByteArrayOutputStream serBuf = new ByteArrayOutputStream();
	final long SER_TEST_BUF_LEN=10L;
	OffHeapPrimitiveArrayOfLong src = new OffHeapPrimitiveArrayOfLong(SER_TEST_BUF_LEN);
	try {
	    for (int i = 0; i < SER_TEST_BUF_LEN; i++)
		src.set(i, Long.MAX_VALUE - i);
	    ObjectOutputStream oos = new ObjectOutputStream(serBuf);
	    oos.writeObject(src);
	    oos.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    ex = e;
	}
	assertNull("Serialization output had no exceptions", ex);
	OffHeapPrimitiveArrayOfLong dest = null;
	try {
	    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serBuf.toByteArray()));
	    dest = (OffHeapPrimitiveArrayOfLong) ois.readObject();
	} catch (Exception e) {
	    e.printStackTrace();
	    ex = e;
	}
	assertNull("Serialization input had no exceptions", ex);

	assertEquals("Correct Num Elements read back from serialization", src.getNumElements(), dest.getNumElements());
	assertEquals("Correct Element size read back from serialization", src.getElementSize(), dest.getElementSize());

	for (int i = 0; i < SER_TEST_BUF_LEN; i++) {
	    long v = 0L;
	    Throwable th = null;
	    try {
		v = dest.get(i);
	    } catch (Throwable e) {
		e.printStackTrace();
		th = e;
	    }
	    assertNull("No exceptions reading location " + i, th);
	    System.out.println("dst[" + i + "]=" + v);
	    assertEquals("Deserialized object validation test. Element " + i, Long.MAX_VALUE - i, v);
	}

    }
}
