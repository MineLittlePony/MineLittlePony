package com.mumfrey.liteloader.transformers.event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.mumfrey.liteloader.core.runtime.Obf;
import com.mumfrey.liteloader.transformers.ClassTransformer;

/**
 * Transformer responsible for transforming/generating the EventProxy inner
 * classes, separated from the Event Transformer itself so that we can place it
 * higher up the tranformer chain to avoid broken mod transformers screwing
 * things up.
 * 
 * @author Adam Mummery-Smith
 */
public class EventProxyTransformer extends ClassTransformer
{
    public EventProxyTransformer()
    {
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (transformedName != null && transformedName.startsWith(Obf.EventProxy.name))
        {
            int dollarPos = transformedName.indexOf('$');
            int proxyIndex = (dollarPos > -1) ? Integer.parseInt(transformedName.substring(dollarPos + 1)) : 0;
            if (proxyIndex != 1)
            {
                try
                {
                    return this.transformEventProxy(transformedName, basicClass, proxyIndex);
                }
                catch (Throwable th)
                {
                    th.printStackTrace();
                }
            }
        }

        return basicClass;
    }

    private byte[] transformEventProxy(String transformedName, byte[] basicClass, int proxyIndex)
    {
        ClassNode classNode = this.getProxyByteCode(transformedName, basicClass, proxyIndex);
        return this.writeClass(Event.populateProxy(classNode, proxyIndex == 0 ? 1 : proxyIndex));
    }

    private ClassNode getProxyByteCode(String transformedName, byte[] basicClass, int proxyIndex)
    {
        if (proxyIndex == 0 || basicClass != null)
        {
            ClassNode classNode = this.readClass(basicClass, true);

            for (MethodNode method : classNode.methods)
            {
                // Strip the sanity code out of the EventProxy class initialiser
                if ("<clinit>".equals(method.name))
                {
                    method.instructions.clear();
                    method.instructions.add(new InsnNode(Opcodes.RETURN));
                }
            }

            return classNode;
        }

        ClassNode classNode = new ClassNode();
        classNode.visit(50, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, transformedName.replace('.', '/'), null, "java/lang/Object", null);
        return classNode;
    }
}
