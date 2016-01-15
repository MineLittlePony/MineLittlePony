package com.mumfrey.liteloader.transformers;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * ClassWriter isolated from ASM so that it exists in the LaunchClassLoader
 * 
 * @author Adam Mummery-Smith
 */
public class IsolatedClassWriter extends ClassWriter
{
    public IsolatedClassWriter(int flags)
    {
        super(flags);
    }

    public IsolatedClassWriter(ClassReader classReader, int flags)
    {
        super(classReader, flags);
    }
}
