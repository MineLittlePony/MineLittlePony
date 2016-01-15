package com.mumfrey.liteloader.transformers.event.inject;

import java.util.Collection;
import java.util.ListIterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import com.mumfrey.liteloader.transformers.ClassTransformer;
import com.mumfrey.liteloader.transformers.event.Event;
import com.mumfrey.liteloader.transformers.event.InjectionPoint;
import com.mumfrey.liteloader.transformers.event.MethodInfo;
import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

/**
 * An injection point which searches for method invocations matching its
 * arguments and returns a list of insns immediately prior to matching
 * invocations. Only the method name is required, owners and signatures are
 * optional and can be used to disambiguate between methods of the same name but
 * with different args, or belonging to different classes.
 * 
 * @author Adam Mummery-Smith
 */
public class BeforeInvoke extends InjectionPoint
{
    protected class InsnInfo
    {
        public final String owner;
        public final String name;
        public final String desc;

        public InsnInfo(AbstractInsnNode insn)
        {
            if (insn instanceof MethodInsnNode)
            {
                MethodInsnNode methodNode = (MethodInsnNode)insn;
                this.owner = methodNode.owner;
                this.name = methodNode.name;
                this.desc = methodNode.desc;
            }
            else if (insn instanceof FieldInsnNode)
            {
                FieldInsnNode fieldNode = (FieldInsnNode)insn;
                this.owner = fieldNode.owner;
                this.name = fieldNode.name;
                this.desc = fieldNode.desc;
            }
            else
            {
                throw new IllegalArgumentException("insn must be an instance of MethodInsnNode or FieldInsnNode");
            }
        }
    }

    /**
     * Method name(s) to search for, usually this will contain the different
     * names of the method for different obfuscations (mcp, srg, notch)
     */
    protected final String[] methodNames;

    /**
     * Method owner(s) to search for, the values in this array MUST much the
     * equivalent indices in methodNames, if the array is NULL then all owners
     * are valid.  
     */
    protected final String[] methodOwners;

    /**
     * Method signature(s) to search for, the values in this array MUST much the
     * equivalent indices in methodNames, if the array is NULL then all
     * signatures are valid.  
     */
    protected final String[] methodSignatures;

    /**
     * This strategy can be used to identify a particular invocation if the same
     * method is invoked at multiple points, if this value is -1 then the
     * strategy returns ALL invocations of the method. 
     */
    protected final int ordinal;

    /**
     * True to turn on strategy debugging to the console
     */
    protected boolean logging = false;

    protected final String className;

    /**
     * Match all occurrences of the specified method or methods
     * 
     * @param methodNames Method name(s) to search for
     */
    public BeforeInvoke(String... methodNames)
    {
        this(methodNames, null, -1);
    }

    /**
     * Match the specified invocation of the specified method
     * 
     * @param methodName Method name to search for
     * @param ordinal ID of the invocation to hook, or -1 to hook all
     *      invocations
     */
    public BeforeInvoke(String methodName, int ordinal)
    {
        this(new String[] { methodName }, null, null, ordinal);
    }

    /**
     * Match the specified invocation of the specified method(s)
     * 
     * @param methodNames Method names to search for
     * @param ordinal ID of the invocation to hook, or -1 to hook all
     *      invocations
     */
    public BeforeInvoke(String[] methodNames, int ordinal)
    {
        this(methodNames, null, null, ordinal);
    }

    /**
     * Match all occurrences of the specified method or methods with the
     * specified owners.
     * 
     * @param methodNames Method names to search for
     * @param methodOwners Owners to search for, indices in this array MUST
     *      match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodOwners should contain
     *      { "net/minecraft/pkg/ClsName", "net/minecraft/pkg/ClsName", "abc" }
     *      in order that the appropriate owner name obfuscation matches the
     *      corresponding index in the methodNames array 
     */
    public BeforeInvoke(String[] methodNames, String[] methodOwners)
    {
        this(methodNames, methodOwners, null, -1);
    }

    /**
     * Match the specified invocation of the specified method or methods with
     * the specified owners.
     * 
     * @param methodNames Method names to search for
     * @param methodOwners Owners to search for, indices in this array MUST
     *      match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodOwners should contain
     *      { "net/minecraft/pkg/ClsName", "net/minecraft/pkg/ClsName", "abc" }
     *      in order that the appropriate owner name obfuscation matches the
     *      corresponding index in the methodNames array 
     * @param ordinal ID of the invocation to hook or -1 to hook all invocations
     */
    public BeforeInvoke(String[] methodNames, String[] methodOwners, int ordinal)
    {
        this(methodNames, methodOwners, null, ordinal);
    }

    /**
     * Match all occurrences of the specified method or methods with the
     * specified owners or signatures, pass null to the owners array if you only
     * want to match signatures.
     * 
     * @param methodNames Method names to search for
     * @param methodOwners Owners to search for, indices in this array MUST
     *      match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodOwners should contain
     *      { "net/minecraft/pkg/ClsName", "net/minecraft/pkg/ClsName", "abc" }
     *      in order that the appropriate owner name obfuscation matches the
     *      corresponding index in the methodNames array 
     * @param methodSignatures Signatures to search for, indices in this array
     *      MUST match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodSignatures should
     *      contain
     *      { "(Lnet/minecraft/pkg/ClsName;)V", 
     *      "(Lnet/minecraft/pkg/ClsName;)V", "(Labc;)V" }
     *      in order that the appropriate signature obfuscation matches the
     *      corresponding index in the methodNames array (and ownerNames array
     *      if present)
     */
    public BeforeInvoke(String[] methodNames, String[] methodOwners, String[] methodSignatures)
    {
        this(methodNames, methodOwners, methodSignatures, -1);
    }

    /**
     * Match the specified invocation of the specified method or methods with
     * the specified owners or signatures, pass null to the owners array if you
     * only want to match signatures.
     * 
     * @param methodNames Method names to search for
     * @param methodOwners Owners to search for, indices in this array MUST
     *      match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodOwners should contain
     *      { "net/minecraft/pkg/ClsName", "net/minecraft/pkg/ClsName", "abc" }
     *      in order that the appropriate owner name obfuscation matches the
     *      corresponding index in the methodNames array 
     * @param methodSignatures Signatures to search for, indices in this array
     *      MUST match the indices in methodNames, eg. if methodNames contains
     *      { "mcpName", "func_12345_a", "a" } then methodSignatures should
     *      contain { "(Lnet/minecraft/pkg/ClassName;)V",
     *      "(Lnet/minecraft/pkg/ClassName;)V", "(Labc;)V" }
     *      in order that the appropriate signature obfuscation matches the
     *      corresponding index in the methodNames array (and ownerNames array
     *      if present)
     * @param ordinal ID of the invocation to hook or -1 to hook all invocations
     */
    public BeforeInvoke(String[] methodNames, String[] methodOwners, String[] methodSignatures, int ordinal)
    {
        if (methodNames == null || methodNames.length == 0)
        {
            throw new IllegalArgumentException("Method name selector must not be null");
        }

        if (methodSignatures != null && methodSignatures.length == 0) methodSignatures = null;
        if (methodOwners != null && methodOwners.length == 0) methodOwners = null;
        if (ordinal < 0) ordinal = -1;

        this.methodNames = methodNames;
        this.methodOwners = methodOwners;
        this.methodSignatures = methodSignatures;
        this.ordinal = ordinal;
        this.className = this.getClass().getSimpleName();

        this.convertClassRefs();
    }

    /**
     * Match the invocation described by the supplied MethodInfo
     * 
     * @param method
     */
    public BeforeInvoke(MethodInfo method)
    {
        this(method, -1);
    }

    /**
     * Match the invocation described by the supplied MethodInfo at the
     * specified ordinal.
     * 
     * @param method
     * @param ordinal
     */
    public BeforeInvoke(MethodInfo method, int ordinal)
    {
        this.methodNames = method.getNames();
        this.methodOwners = method.getOwners();
        this.methodSignatures = method.getDescriptors();
        this.ordinal = ordinal;
        this.className = this.getClass().getSimpleName();

        this.convertClassRefs();
    }

    private void convertClassRefs()
    {
        for (int i = 0; i < this.methodOwners.length; i++)
        {
            if (this.methodOwners[i] != null) this.methodOwners[i] = this.methodOwners[i].replace('.', '/');
        }

        if (this.methodSignatures != null)
        {
            for (int i = 0; i < this.methodSignatures.length; i++)
            {
                if (this.methodSignatures[i] != null) this.methodSignatures[i] = this.methodSignatures[i].replace('.', '/');
            }
        }
    }

    public BeforeInvoke setLogging(boolean logging)
    {
        this.logging = logging;
        return this;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.transformers.event.InjectionStrategy
     *      #findInjectionPoint(java.lang.String,
     *      org.objectweb.asm.tree.InsnList,
     *      com.mumfrey.liteloader.transformers.event.Event,
     *      java.util.Collection)
     */
    @Override
    public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
    {
        int ordinal = 0;
        boolean found = false;

        if (this.logging)
        {
            LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);
            LiteLoaderLogger.debug(this.className + " is searching for an injection point in method with descriptor %s", desc);
        }

        ListIterator<AbstractInsnNode> iter = insns.iterator();
        while (iter.hasNext())
        {
            AbstractInsnNode insn = iter.next();

            if (this.matchesInsn(insn))
            {
                InsnInfo nodeInfo = new InsnInfo(insn);

                if (this.logging)
                {
                    LiteLoaderLogger.debug(this.className + " is considering insn NAME=%s DESC=%s OWNER=%s",
                            nodeInfo.name, nodeInfo.desc, nodeInfo.owner);
                }

                int index = BeforeInvoke.arrayIndexOf(this.methodNames, nodeInfo.name, -1);
                if (index > -1 && this.logging) LiteLoaderLogger.debug(this.className + "   found a matching insn, checking owner/signature...");

                int ownerIndex = BeforeInvoke.arrayIndexOf(this.methodOwners, nodeInfo.owner, index);
                int descIndex = BeforeInvoke.arrayIndexOf(this.methodSignatures, nodeInfo.desc, index);
                if (index > -1 && ownerIndex == index && descIndex == index)
                {
                    if (this.logging) LiteLoaderLogger.debug(this.className + "     found a matching insn, checking preconditions...");
                    if (this.matchesInsn(nodeInfo, ordinal))
                    {
                        if (this.logging) LiteLoaderLogger.debug(this.className + "         found a matching insn at ordinal %d", ordinal);
                        nodes.add(insn);
                        found = true;

                        if (this.ordinal == ordinal)
                        {
                            break;
                        }
                    }

                    ordinal++;
                }
            }

            this.inspectInsn(desc, insns, insn);
        }

        if (this.logging) LiteLoaderLogger.debug(ClassTransformer.HORIZONTAL_RULE);

        return found;
    }

    protected boolean matchesInsn(AbstractInsnNode insn)
    {
        return insn instanceof MethodInsnNode;
    }

    protected void inspectInsn(String desc, InsnList insns, AbstractInsnNode insn)
    {
        // stub for subclasses
    }

    protected boolean matchesInsn(InsnInfo nodeInfo, int ordinal)
    {
        if (this.logging)
        {
            LiteLoaderLogger.debug(this.className + "       comparing target ordinal %d with current ordinal %d", this.ordinal, ordinal);
        }
        return this.ordinal == -1 || this.ordinal == ordinal;
    }

    /**
     * Special version of contains which returns TRUE if the haystack array is
     * null, which is an odd behaviour we actually want here because null
     * indicates that the value is not important.
     * 
     * @param haystack
     * @param needle
     */
    private static int arrayIndexOf(String[] haystack, String needle, int pos)
    {
        if (haystack == null) return pos;
        if (pos > -1 && pos < haystack.length && needle.equals(haystack[pos])) return pos;

        for (int index = 0; index < haystack.length; index++)
        {
            if (needle.equals(haystack[index]))
            {
                return index;
            }
        }

        return -1;
    }
}
