package com.mumfrey.liteloader.transformers;

import java.util.ArrayList;
import java.util.List;

import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * Target information for injected callback methods
 * 
 * @author Adam Mummery-Smith
 */
public class Callback
{
    /**
     * Type of callback to inject
     */
    public enum CallbackType
    {
        /**
         * Redirect callbacks are injected at the start of a method and
         * immediately return, thus short-circuiting the method 
         */
        REDIRECT(true),

        /**
         * Event callbacks are injected at the start of a method but do not
         * alter the normal method behaviour.
         */
        EVENT(false),

        /**
         * Return callbacks are injected immediately prior to every RETURN
         * opcode in a particular method. Callback handlers must have the SAME
         * return type as the method containing the injected callback
         */
        RETURN(false),

        /**
         * A profiler callback injected at a profiler "startSection" invocation
         */
        PROFILER_STARTSECTION(Obf.startSection, true),

        /**
         * A profiler callback injected at a profiler "endSection" invocation 
         */
        PROFILER_ENDSECTION(Obf.endSection, false),

        /**
         * A profiler callback injected at a profiler "endStartSection"
         * invocation 
         */
        PROFILER_ENDSTARTSECTION(Obf.endStartSection, true);

        /**
         * 
         */
        private final boolean injectReturn;

        /**
         * 
         */
        private final boolean isProfilerCallback;

        /**
         * 
         */
        private final boolean sectionRequired;

        /**
         * 
         */
        private final Obf profilerMethod;

        private CallbackType(boolean returnFrom)
        {
            this.injectReturn = returnFrom;
            this.isProfilerCallback = false;
            this.profilerMethod = null;
            this.sectionRequired = false;
        }

        private CallbackType(Obf profilerMethod, boolean sectionRequired)
        {
            this.injectReturn = false;
            this.isProfilerCallback = true;
            this.profilerMethod = profilerMethod;
            this.sectionRequired = sectionRequired;
        }

        boolean injectReturn()
        {
            return this.injectReturn;
        }

        boolean isProfilerCallback()
        {
            return this.isProfilerCallback;
        }

        String getProfilerMethod(int obfType)
        {
            return this.profilerMethod != null ? this.profilerMethod.names[obfType] : "";
        }

        String getProfilerMethodSignature()
        {
            return this.sectionRequired ? "(Ljava/lang/String;)V" : "()V";
        }

        boolean isSectionRequired()
        {
            return this.sectionRequired;
        }

        public String getSignature()
        {
            if (this == CallbackType.EVENT || this == CallbackType.REDIRECT)
            {
                return "head";
            }

            return this.name().toString().toLowerCase();
        }
    }

    /**
     * 
     */
    private final CallbackType callbackType;

    /**
     * 
     */
    private final String sectionName;

    /**
     * 
     */
    private final String profilerMethod;

    /**
     * Callback class reference
     */
    private final String callbackClass;

    /**
     * Callback method name 
     */
    private final String callbackMethod;

    /**
     * Return callbacks are injected before every RETURN opcode in the method,
     * each RETURN is thus allocated a sequential refNumber which is passed to
     * the callback method so that the callback handler can choose which RETURN
     * it wishes to handle.
     */
    int refNumber;

    /**
     * 
     */
    private final List<Callback> chainedCallbacks;

    public Callback(CallbackType callbackType, String callbackMethod, String callbackClass)
    {
        this(callbackType, callbackMethod, callbackClass, null, 0);
    }

    /**
     * A new callback method in the specified class 
     * 
     * @param callbackMethod Method to call, must be public, static and have the
     *      appropriate signature for the type of injected callback
     * @param callbackClass Fully qualified name of the class containing the
     *      callback method, must also be public or visible from the calling
     *      package
     */
    public Callback(CallbackType callbackType, String callbackMethod, String callbackClass, String section, int obfType)
    {
        if (section == null && callbackType.isSectionRequired())
        {
            throw new RuntimeException(String.format("Callback of type %s requires a section name but no section name was provided",
                    callbackType.name()));
        }

        this.callbackType = callbackType;
        this.callbackClass = callbackClass.replace('.', '/');
        this.callbackMethod = callbackMethod;
        this.sectionName = section;
        this.chainedCallbacks = new ArrayList<Callback>();
        this.profilerMethod = callbackType.getProfilerMethod(obfType);
    }

    private Callback(Callback other, int refNumber)
    {
        this.callbackType = other.callbackType;
        this.callbackClass = other.callbackClass;
        this.callbackMethod = other.callbackMethod;
        this.sectionName = other.sectionName;
        this.chainedCallbacks = other.chainedCallbacks;
        this.profilerMethod = other.profilerMethod;
        this.refNumber = refNumber;
    }

    public CallbackType getType()
    {
        return this.callbackType;
    }

    public String getCallbackClass()
    {
        return this.callbackClass;
    }

    public String getCallbackMethod()
    {
        return this.callbackMethod;
    }

    public boolean injectReturn()
    {
        return this.callbackType.injectReturn();
    }

    public boolean isProfilerCallback()
    {
        return this.callbackType.isProfilerCallback();
    }

    public String getSectionName()
    {
        return this.sectionName;
    }

    public String getProfilerMethod()
    {
        return this.profilerMethod;
    }

    public String getProfilerMethodSignature()
    {
        return this.callbackType.getProfilerMethodSignature();
    }

    public Callback getNextCallback()
    {
        return new Callback(this, this.refNumber++);
    }

    void addChainedCallback(Callback chained)
    {
        this.chainedCallbacks.add(chained);
    }

    public List<Callback> getChainedCallbacks()
    {
        return this.chainedCallbacks;
    }

    @Override
    public String toString()
    {
        return this.callbackMethod;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == null || !(other instanceof Callback)) return false;
        Callback callback = (Callback)other;
        return callback.callbackClass.equals(this.callbackClass) && callback.callbackMethod.equals(this.callbackMethod)
                && callback.callbackType == this.callbackType;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}