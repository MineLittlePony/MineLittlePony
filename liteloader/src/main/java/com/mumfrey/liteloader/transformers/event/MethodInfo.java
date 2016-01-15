package com.mumfrey.liteloader.transformers.event;

import joptsimple.internal.Strings;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ByteCodeUtilities;

/**
 * Encapsulates a method descriptor with varying degrees of accuracy from a
 * simpler owner/method mapping up to and including a multi-faceted
 * notch/srg/mcp method descriptor which works in all obfuscation environments. 
 * 
 * @author Adam Mummery-Smith
 */
public class MethodInfo
{
    public static final String INFLECT = Strings.EMPTY; 

    // Owning class
    final String owner;
    final String ownerRef;
    final String ownerObf;

    // Method name
    final String name;
    final String nameSrg;
    final String nameObf;

    // Descriptor
    final String desc;
    final String descObf;

    // "Signature" - method name plus descriptor
    final String sig;
    final String sigSrg;
    final String sigObf;

    /**
     * Create a MethodInfo for the specified class with a method name inflected
     * by context
     * 
     * @param owner Literal owner class name
     */
    public MethodInfo(String owner)
    {
        this(owner, owner, MethodInfo.INFLECT, MethodInfo.INFLECT, MethodInfo.INFLECT, null, null);
    }

    /**
     * Create a MethodInfo for the specified class with a method name inflected
     * by context
     * 
     * @param owner Owner name descriptor
     */
    public MethodInfo(Obf owner)
    {
        this(owner.name, owner.obf, MethodInfo.INFLECT, MethodInfo.INFLECT, MethodInfo.INFLECT, null, null);
    }

    /**
     * Create a MethodInfo for the specified class and method names (literal)
     * 
     * @param owner Literal owner class name
     * @param method Literal method name
     */
    public MethodInfo(String owner, String method)
    {
        this(owner, owner, method, method, method, null, null);
    }

    /**
     * Create a MethodInfo for the specified class and literal method name
     * 
     * @param owner Owner name descriptor
     * @param method Literal method name
     */
    public MethodInfo(Obf owner, String method)
    {
        this(owner.name, owner.obf, method, method, method, null, null);
    }

    /**
     * Create a MethodInfo for the specified class and method name
     * 
     * @param owner Owner name descriptor
     * @param method Literal method name
     */
    public MethodInfo(Obf owner, Obf method)
    {
        this(owner.name, owner.obf, method.name, method.srg, method.obf, null, null);
    }

    /**
     * Create a MethodInfo for the specified class, literal method name and
     * literal descriptor
     * 
     * @param owner Owner name descriptor
     * @param method Literal method name
     * @param descriptor Literal descriptor (useful for methods which only
     *      accept primitive types and therefore have a fixed descriptor)
     */
    public MethodInfo(Obf owner, String method, String descriptor)
    {
        this(owner.name, owner.obf, method, method, method, descriptor, descriptor);
    }

    /**
     * Create a MethodInfo for the specified literal class, literal method and
     * literal descriptor
     * 
     * @param owner Literal class name
     * @param method Literal method name
     * @param descriptor Literal descriptor (useful for methods which only
     *      accept primitive types and therefore have a fixed descriptor)
     */
    public MethodInfo(String owner, String method, String descriptor)
    {
        this(owner, owner, method, method, method, descriptor, descriptor);
    }

    /**
     * Create a MethodInfo for the specified class and method, with a literal
     * descriptor
     * 
     * @param owner Owner class name descriptor
     * @param method Method name descriptor
     * @param descriptor Literal descriptor (useful for methods which only
     *      accept primitive types and therefore have a fixed descriptor)
     */
    public MethodInfo(Obf owner, Obf method, String descriptor)
    {
        this(owner.name, owner.obf, method.name, method.srg, method.obf, descriptor, descriptor);
    }

    /**
     * <p>Create a MethodInfo for the specified class and literal method and
     * compute the descriptor using the supplied arguments, both the returnType
     * and args values can be one of four types:</p>
     * 
     * <ul>
     *     <li><b>Obf instances</b> - are converted to the appropriate class
     *     name for the obf type internally</li>
     *     <li><b>Strings</b> - are added directly to the descriptor</li>
     *     <li><b>Type instances</b> - are expanded to their bytecode literal
     *     </li>
     *     <li><b>Class instances</b> - are expanded to their bytecode
     *     descriptor via Type.getDescriptor</li>
     * </ul>
     * 
     * @param owner Owner name descriptor
     * @param method Literal method name
     * @param returnType Return type for the method (use Void.TYPE for void
     *      methods)
     * @param args (optional) list of method arguments as Obf/String/Type/Class
     *      instances
     */
    public MethodInfo(Obf owner, String method, Object returnType, Object... args)
    {
        this(owner.name, owner.obf, method, method, method,
                ByteCodeUtilities.generateDescriptor(Obf.MCP, returnType, args),
                ByteCodeUtilities.generateDescriptor(Obf.OBF, returnType, args));
    }

    /**
     * <p>Create a MethodInfo for the specified class and method names and
     * compute the descriptor using the supplied arguments, both the returnType
     * and args values can be one of four types:</p>
     * 
     * <ul>
     *     <li><b>Obf instances</b> - are converted to the appropriate class
     *     name for the obf type internally</li>
     *     <li><b>Strings</b> - are added directly to the descriptor</li>
     *     <li><b>Type instances</b> - are expanded to their bytecode literal
     *     </li>
     *     <li><b>Class instances</b> - are expanded to their bytecode
     *     descriptor via Type.getDescriptor</li>
     * </ul>
     * 
     * @param owner Owner name descriptor
     * @param method Method name descriptor
     * @param returnType Return type for the method (use Void.TYPE for void
     *      methods)
     * @param args (optional) list of method arguments as Obf/String/Type/Class
     *      instances
     */
    public MethodInfo(Obf owner, Obf method, Object returnType, Object... args)
    {
        this(owner.name, owner.obf, method.name, method.srg, method.obf,
                ByteCodeUtilities.generateDescriptor(Obf.MCP, returnType, args),
                ByteCodeUtilities.generateDescriptor(Obf.OBF, returnType, args));
    }

    /**
     * @param owner
     * @param ownerObf
     * @param name
     * @param nameSrg
     * @param nameObf
     * @param desc
     * @param descObf
     */
    MethodInfo(String owner, String ownerObf, String name, String nameSrg, String nameObf, String desc, String descObf)
    {
        this.owner    = owner.replace('/', '.');
        this.ownerRef = owner.replace('.', '/');
        this.ownerObf = ownerObf;
        this.name     = name;
        this.nameSrg  = nameSrg;
        this.nameObf  = nameObf;
        this.desc     = desc;
        this.descObf  = descObf;
        this.sig      = MethodInfo.generateSignature(this.name, this.desc);
        this.sigSrg   = MethodInfo.generateSignature(this.nameSrg, this.desc);
        this.sigObf   = MethodInfo.generateSignature(this.nameObf, this.descObf);
    }

    /**
     * Get the method's owning class
     */
    public String getOwner()
    {
        return this.owner;
    }

    /**
     * Get the method's owning class's obfuscated name (if it has one, otherwise
     * returns the same as getOwner())
     */
    public String getOwnerObf()
    {
        return this.ownerObf;
    }

    /**
     * Get all owner variants in an array
     */
    public String[] getOwners()
    {
        return new String[] { this.ownerObf, this.owner, this.owner }; 
    }

    /**
     * Get the method's name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Get the method name or inflects it using the supplied context if this
     * MethodInfo was created with inflection enabled
     */
    public String getOrInflectName(String context)
    {
        return this.name == MethodInfo.INFLECT ? context : this.name;
    }

    /**
     * Get the Searge name of the method (if it has one, otherwise returns the
     * base name)
     */
    public String getNameSrg()
    {
        return this.nameSrg;
    }

    /**
     * Get the obfuscated name of the method (if it has one, otherwise returns
     * the base name)
     */
    public String getNameObf()
    {
        return this.nameObf;
    }

    /**
     * Get all name variants in an array
     */
    public String[] getNames()
    {
        return new String[] { this.nameObf, this.nameSrg, this.name }; 
    }

    /**
     * Get the method descriptor
     */
    public String getDesc()
    {
        return this.desc;
    }

    /**
     * Get the method descriptor with obfuscated parameter types (if available,
     * otherwise returns the same as getDesc())
     */
    public String getDescObf()
    {
        return this.descObf;
    }

    /**
     * Get all descriptors in an array
     */
    public String[] getDescriptors()
    {
        return this.desc == null ? null : new String[] { this.descObf, this.desc, this.desc }; 
    }

    /**
     * Returns true if this MethodInfo has a descriptor
     */
    public boolean hasDesc()
    {
        return this.desc != null;
    }

    /**
     * Get the signature (combined method name and descriptor) for the method
     * represented by this methodInfo
     * 
     * @param type Obfuscation type to use 
     */
    public String getSignature(int type)
    {
        if (type == Obf.OBF) return this.sigObf;
        if (type == Obf.SRG) return this.sigSrg;
        return this.sig;
    }

    public boolean matches(String method, String desc)
    {
        return this.matches(method, desc, null);
    }

    public boolean matches(String method, String desc, String className)
    {
        if ((className == null || this.ownerRef.equals(className)) && (this.name.equals(method) || this.nameSrg.equals(method)))
        {
            return this.desc == null || this.desc.equals(desc);
        }
        else if ((className == null || this.ownerObf.equals(className)) && this.nameObf.equals(method))
        {
            return this.descObf == null || this.descObf.equals(desc);
        }

        return false;
    }

    static String generateSignature(String methodName, String methodSignature)
    {
        return String.format("%s%s", methodName, methodSignature == null ? "" : methodSignature);
    }

    @Override
    public boolean equals(Object other)
    {
        if (other == this) return true;
        if (other instanceof MethodInfo) return this.sig.equals(((MethodInfo)other).sig);
        if (other instanceof String) return this.sig.equals(other);
        return false;
    }

    @Override
    public String toString()
    {
        return this.sig;
    }

    @Override
    public int hashCode()
    {
        return this.sig.hashCode();
    }
}
