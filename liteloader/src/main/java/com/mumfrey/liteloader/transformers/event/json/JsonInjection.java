package com.mumfrey.liteloader.transformers.event.json;

import java.io.Serializable;
import java.lang.reflect.Constructor;

import com.google.gson.annotations.SerializedName;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.transformers.event.inject.BeforeInvoke;
import com.mumfrey.liteloader.transformers.event.inject.BeforeReturn;
import com.mumfrey.liteloader.transformers.event.inject.BeforeStringInvoke;
import com.mumfrey.liteloader.transformers.event.inject.MethodHead;

/**
 * A JSON injection point definition
 * 
 * @author Adam Mummery-Smith
 */
public class JsonInjection implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Method to inject into
     */
    @SerializedName("method")
    private String methodName;

    /**
     * Type of injection point
     */
    @SerializedName("type")
    private JsonInjectionType type;

    /**
     * Shift type (optional)
     */
    @SerializedName("shift")
    private JsonInjectionShiftType shift;

    /**
     * Target method to search for when using INVOKE and INVOKESTRING
     */
    @SerializedName("target")
    private String target;

    /**
     * Ordinal to use when using INVOKE and INVOKESTRING
     */
    @SerializedName("ordinal")
    private int ordinal = -1;

    /**
     * InjectionPoint class to use for CUSTOM
     */
    @SerializedName("class")
    private String className;

    /**
     * Constructor arguments to pass wehn using CUSTOM
     */
    @SerializedName("args")
    private Object[] args;

    private transient MethodInfo method;

    private transient InjectionPoint injectionPoint;

    public MethodInfo getMethod()
    {
        return this.method;
    }

    public InjectionPoint getInjectionPoint()
    {
        return this.injectionPoint;
    }

    public void parse(JsonMethods methods)
    {
        this.method = this.parseMethod(methods);
        this.injectionPoint = this.parseInjectionPoint(methods);
    }

    private MethodInfo parseMethod(JsonMethods methods)
    {
        try
        {
            return methods.get(this.methodName);
        }
        catch (NullPointerException ex)
        {
            throw new InvalidEventJsonException("'method' must not be null for injection");
        }
    }

    public InjectionPoint parseInjectionPoint(JsonMethods methods)
    {
        switch (this.type)
        {
            case INVOKE:
                return this.applyShift(new BeforeInvoke(methods.get(this.getTarget()), this.ordinal));

            case INVOKESTRING:
                return this.applyShift(new BeforeStringInvoke(this.getArg(0).toString(), methods.get(this.getTarget()), this.ordinal));

            case RETURN:
                return this.applyShift(new BeforeReturn(this.ordinal));

            case HEAD:
                return new MethodHead();

            case CUSTOM:
                try
                {
                    @SuppressWarnings("unchecked")
                    Class<InjectionPoint> injectionPointClass = (Class<InjectionPoint>)Class.forName(this.className);
                    if (this.args != null)
                    {
                        Constructor<InjectionPoint> ctor = injectionPointClass.getDeclaredConstructor(Object[].class);
                        return ctor.newInstance(this.args);
                    }
                    return injectionPointClass.newInstance();
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }

            default:
                throw new InvalidEventJsonException("Could not parse injection type");
        }
    }

    private Object getArg(int arg)
    {
        if (this.args == null || this.args.length >= this.args.length || arg < 0)
        {
            return "";
        }

        return this.args[arg];
    }

    private String getTarget()
    {
        if (this.target != null && this.shift == null)
        {
            if (this.target.startsWith("before(") && this.target.endsWith(")"))
            {
                this.target = this.target.substring(7, this.target.length() - 1);
                this.shift = JsonInjectionShiftType.BEFORE;
            }
            else if (this.target.startsWith("after(") && this.target.endsWith(")"))
            {
                this.target = this.target.substring(6, this.target.length() - 1);
                this.shift = JsonInjectionShiftType.AFTER;
            }
        }

        if (this.target == null)
        {
            throw new InvalidEventJsonException("'target' is required for injection type " + this.type.name());
        }

        return this.target;
    }

    private InjectionPoint applyShift(InjectionPoint injectionPoint)
    {
        if (this.shift != null)
        {
            switch (this.shift)
            {
                case AFTER:
                    return InjectionPoint.after(injectionPoint);

                case BEFORE:
                    return InjectionPoint.before(injectionPoint);
                    
                default:
                    return injectionPoint;
            }
        }

        return injectionPoint;
    }
}
