package org.dkay229.offheap.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestOffHeapPrimitiveArrayOfInt {

	@Test
	public void readWrite() {
		OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
		for (int i=0;i<10;i++)
			arr.set(i,i+1);
		for (int i=0;i<10;i++)
			assertEquals("Read write test: "+i,i+1,arr.get(i));
	}
	@Test
	public void getOutOfBounds() {
		OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
		Exception ex= null;
		long val=-1;
		
		try {
			val=arr.get(10);
		} catch (Exception e) {
			ex=e;
		}
		assertTrue("Is instance of ArrayIndexOutOfBoundsException",ex instanceof ArrayIndexOutOfBoundsException);
	}
	@Test
	public void setOutOfBounds() {
		OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
		Exception ex= null;
		long val=-1;
		
		try {
			val=arr.set(10,11L);
		} catch (Exception e) {
			ex=e;
		}
		assertTrue("Is instance of ArrayIndexOutOfBoundsException",ex instanceof ArrayIndexOutOfBoundsException);
	}
	@Test
	public void getNegative() {
		OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
		Exception ex= null;
		long val=-1;
		
		try {
			val=arr.get(-1);
		} catch (Exception e) {
			ex=e;
		}
		assertTrue("Is instance of ArrayIndexOutOfBoundsException",ex instanceof ArrayIndexOutOfBoundsException);
	}
	@Test
	public void setNegative() {
		OffHeapPrimitiveArrayOfInt arr = new OffHeapPrimitiveArrayOfInt(10);
		Exception ex= null;
		long val=-1;
		
		try {
			val=arr.set(-1,999L);
		} catch (Exception e) {
			ex=e;
		}
		assertTrue("Is instance of ArrayIndexOutOfBoundsException",ex instanceof ArrayIndexOutOfBoundsException);
	}

}
