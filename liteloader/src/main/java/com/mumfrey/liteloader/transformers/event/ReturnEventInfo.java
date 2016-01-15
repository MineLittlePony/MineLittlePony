package com.mumfrey.liteloader.transformers.event;

import org.objectweb.asm.Type;

import com.mumfrey.liteloader.core.event.EventCancellationException;

/**
 * EventInfo for events which have a return type
 * 
 * @author Adam Mummery-Smith
 *
 * @param <S> Source object type. For non-static methods this will be the
 *      containing object instance.
 * @param <R> Return type
 */
public class ReturnEventInfo<S, R> extends EventInfo<S>
{
    private R returnValue;

    public ReturnEventInfo(String name, S source, boolean cancellable)
    {
        super(name, source, cancellable);
        this.returnValue = null;
    }

    public ReturnEventInfo(String name, S source, boolean cancellable, R returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = returnValue;
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, byte returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Byte.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, char returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Character.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, double returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Double.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, float returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Float.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, int returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Integer.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, long returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Long.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, short returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Short.valueOf(returnValue);
    }

    @SuppressWarnings("unchecked")
    public ReturnEventInfo(String name, S source, boolean cancellable, boolean returnValue)
    {
        super(name, source, cancellable);
        this.returnValue = (R)Boolean.valueOf(returnValue);
    }

    /**
     * Sets a return value for this event and cancels the event (required in
     * order to return the new value).
     * 
     * @param returnValue
     */
    public void setReturnValue(R returnValue) throws EventCancellationException
    {
        super.cancel();

        this.returnValue = returnValue;
    }

    public R getReturnValue()
    {
        return this.returnValue;
    }
    
    // CHECKSTYLE:OFF

    // All of the accessors below are to avoid having to generate unboxing conversions in bytecode
    public byte    getReturnValueB() { if (this.returnValue == null) return 0;     return (Byte)     this.returnValue; }
    public char    getReturnValueC() { if (this.returnValue == null) return 0;     return (Character)this.returnValue; }
    public double  getReturnValueD() { if (this.returnValue == null) return 0.0;   return (Double)   this.returnValue; }
    public float   getReturnValueF() { if (this.returnValue == null) return 0.0F;  return (Float)    this.returnValue; }
    public int     getReturnValueI() { if (this.returnValue == null) return 0;     return (Integer)  this.returnValue; }
    public long    getReturnValueJ() { if (this.returnValue == null) return 0;     return (Long)     this.returnValue; }
    public short   getReturnValueS() { if (this.returnValue == null) return 0;     return (Short)    this.returnValue; }
    public boolean getReturnValueZ() { if (this.returnValue == null) return false; return (Boolean)  this.returnValue; }

    // CHECKSTYLE:ON

    
    public static String getReturnAccessor(Type returnType)
    {
        if (returnType.getSort() == Type.OBJECT)
        {
            return "getReturnValue";
        }

        return String.format("getReturnValue%s", returnType.getDescriptor());
    }

    public static String getReturnDescriptor(Type returnType)
    {
        if (returnType.getSort() == Type.OBJECT)
        {
            return String.format("()%s", EventInfo.OBJECT);
        }

        return String.format("()%s", returnType.getDescriptor());
    }
}
