package com.mumfrey.liteloader.core.event;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.profiler.Profiler;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mumfrey.liteloader.api.Listener;
import com.mumfrey.liteloader.core.runtime.Obf;

/**
 * A HandlerList which calls Profiler.beginSection and Profiler.endSection
 * before every invocation.
 * 
 * @author Adam Mummery-Smith
 *
 * @param <T>
 */
public class ProfilingHandlerList<T extends Listener> extends HandlerList<T>
{
    private static final long serialVersionUID = 1L;

    /**
     * Profiler to pass in to baked handler lists
     */
    private final Profiler profiler;

    /**
     * @param type
     * @param profiler
     */
    public ProfilingHandlerList(Class<T> type, Profiler profiler)
    {
        super(type);
        this.profiler = profiler;
    }

    /**
     * @param type
     * @param logicOp
     * @param profiler
     */
    public ProfilingHandlerList(Class<T> type, ReturnLogicOp logicOp, Profiler profiler)
    {
        super(type, logicOp);
        this.profiler = profiler;
    }

    /**
     * @param type
     * @param logicOp
     * @param sorted
     * @param profiler
     */
    public ProfilingHandlerList(Class<T> type, ReturnLogicOp logicOp, boolean sorted, Profiler profiler)
    {
        super(type, logicOp, sorted);
        this.profiler = profiler;
    }

    /* (non-Javadoc)
     * @see com.mumfrey.liteloader.core.event.HandlerList#getDecorator()
     */
    @Override
    protected IHandlerListDecorator<T> getDecorator()
    {
        return new ProfilingHandlerListDecorator<T>(this.profiler);
    }

    /**
     * Decorator which adds the profiler section calls to the invocation lists
     */
    static class ProfilingHandlerListDecorator<T extends Listener> implements IHandlerListDecorator<T>
    {
        private final Profiler profiler;

        private final List<String> names = new ArrayList<String>();;

        protected ProfilingHandlerListDecorator(Profiler profiler)
        {
            this.profiler = profiler;
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #getTemplate()
         */
        @Override
        public Obf getTemplate()
        {
            return Obf.BakedProfilingHandlerList;
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #prepare(java.util.List)
         */
        @Override
        public void prepare(List<T> sortedList)
        {
            this.names.clear();

            for (Listener l : sortedList)
            {
                String name = l.getName();
                this.names.add(name != null ? name : l.getClass().getSimpleName());
            }
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #createInstance(java.lang.Class)
         */
        @Override
        public BakedHandlerList<T> createInstance(Class<BakedHandlerList<T>> handlerClass) throws Exception
        {
            try
            {
                Constructor<BakedHandlerList<T>> ctor = handlerClass.getDeclaredConstructor(Profiler.class);
                ctor.setAccessible(true);
                return ctor.newInstance(this.profiler);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                throw ex;
            }
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #populateClass(java.lang.String,
         *      org.objectweb.asm.tree.ClassNode)
         */
        @Override
        public void populateClass(String name, ClassNode classNode)
        {
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #processCtor(org.objectweb.asm.tree.ClassNode,
         *      org.objectweb.asm.tree.MethodNode)
         */
        @Override
        public void processCtor(ClassNode classNode, MethodNode method)
        {
            // Actually replace the ctor code because it's easier
            method.instructions.clear();
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Obf.BakedProfilingHandlerList.ref, Obf.constructor.name,
                    method.desc, false));
            method.instructions.add(new InsnNode(Opcodes.RETURN));
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #preInvokeInterfaceMethod(int, org.objectweb.asm.tree.ClassNode,
         *      org.objectweb.asm.tree.MethodNode, org.objectweb.asm.Type[])
         */
        @Override
        public void preInvokeInterfaceMethod(int handlerIndex, ClassNode classNode, MethodNode method, Type[] args)
        {
            // Call this.startSection
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new LdcInsnNode(this.names.get(handlerIndex)));
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, classNode.superName, "startSection", "(Ljava/lang/String;)V", false));
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *     #postInvokeInterfaceMethod(int, org.objectweb.asm.tree.ClassNode,
         *     org.objectweb.asm.tree.MethodNode, org.objectweb.asm.Type[])
         */
        @Override
        public void postInvokeInterfaceMethod(int handlerIndex, ClassNode classNode, MethodNode method, Type[] args)
        {
            // Call this.endSection
            method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            method.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, classNode.superName, "endSection", "()V", false));
        }

        /* (non-Javadoc)
         * @see com.mumfrey.liteloader.core.event.IHandlerListDecorator
         *      #populateInterfaceMethod(org.objectweb.asm.tree.ClassNode,
         *      org.objectweb.asm.tree.MethodNode)
         */
        @Override
        public void populateInterfaceMethod(ClassNode classNode, MethodNode method)
        {
        }
    }

    /**
     * Template class for the profiling handler lists
     * 
     * @author Adam Mummery-Smith
     *
     * @param <T>
     */
    public abstract static class BakedList<T> extends HandlerList.BakedHandlerList<T>
    {
        private final Profiler profiler;

        public BakedList(Profiler profiler)
        {
            this.profiler = profiler;
        }

        @Override
        public abstract T get();

        @Override
        public abstract BakedHandlerList<T> populate(List<T> listeners);

        protected void startSection(String name)
        {
            this.profiler.startSection(name);
        }

        protected void endSection()
        {
            this.profiler.endSection();
        }
    }
}
