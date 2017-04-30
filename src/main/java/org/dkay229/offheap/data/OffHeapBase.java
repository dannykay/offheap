package org.dkay229.offheap.data;

import sun.misc.Unsafe;
import java.lang.reflect.Field;

@SuppressWarnings("restriction")
public class OffHeapBase {

	private static final Unsafe unsafe;
	private static final Exception unsafeException;
	protected final long BYTES_PER_LONG = 8L;
    static
    {
    	Exception ex=null;
        try
        {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe)field.get(null);
        }
        catch (Exception e)
        {
        	ex=e;
            throw new RuntimeException(e);
        } finally {
        	unsafeException=ex;
        }
    }
    protected Unsafe getUnsafe()
    {
    	if (unsafeException!=null||unsafe==null)
    		throw new RuntimeException(unsafeException);
    	return unsafe;
    }
    public OffHeapBase()
    {
    	super();
    }
}
