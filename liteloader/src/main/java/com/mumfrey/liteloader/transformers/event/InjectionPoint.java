package com.mumfrey.liteloader.transformers.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import com.google.common.base.Joiner;

/**
 * Base class for injection point discovery classes. Each subclass describes a
 * strategy for locating code injection points within a method, with the
 * {@link #find} method populating a collection with insn nodes from the method
 * which satisfy its strategy.
 * 
 * <p>This base class also contains composite strategy factory methods such as
 * {@code and} and {@code or} which allow strategies to be combined using
 * intersection (and) or union (or) relationships to allow multiple strategies
 * to be easily combined.</p>
 * 
 * <p>You are free to create your own injection point subclasses, but take note
 * that it is allowed for a single InjectionPoint instance to be used for
 * multiple injections and thus implementing classes MUST NOT cache the insn
 * list, event, or nodes instance passed to the {@code find} method, as each
 * call to {@code find} must be considered a separate functional contract and
 * the InjectionPoint's lifespan is not linked to the discovery lifespan,
 * therefore it is important that the InjectionPoint implementation is fully
 * STATELESS.</p>
 * 
 * @author Adam Mummery-Smith
 */
public abstract class InjectionPoint
{
    /**
     * Capture locals as well as args
     */
    protected boolean captureLocals;

    protected boolean logLocals;

    /**
     * Find injection points in the supplied insn list
     * 
     * @param desc Method descriptor, supplied to allow return types and
     *      arguments etc. to be determined
     * @param insns Insn list to search in, the strategy MUST ONLY add nodes
     *      from this list to the {@code nodes} collection
     * @param nodes Collection of nodes to populate. Injectors should NOT make
     *      any assumptions about the state of this collection and should only
     *      call add()
     * @param event Event being injected here, supplied to allow alteration of
     *      behaviour for specific event configurations (eg. cancellable)
     * @return true if one or more injection points were found
     */
    public abstract boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event);

    /**
     * Set whether this injection point should capture local variables as well
     * as method arguments.
     * 
     * @param captureLocals
     * @return this, for fluent interface
     */
    public InjectionPoint setCaptureLocals(boolean captureLocals)
    {
        this.captureLocals = captureLocals;
        return this;
    }

    /**
     * Get whether capture locals is enabled
     */
    public boolean captureLocals()
    {
        return this.captureLocals;
    }

    /**
     * Since it's virtually impossible to know what locals are available at a
     * given injection point by reading the source, this method causes the
     * injection point to dump the locals to the debug log at injection time.
     * 
     * @param logLocals
     * @return this, for fluent interface
     */
    public InjectionPoint setLogLocals(boolean logLocals)
    {
        this.logLocals = logLocals;
        return this;
    }

    /**
     * Get whether log locals is enabled 
     */
    public boolean logLocals()
    {
        return this.logLocals;
    }

    @Override
    public String toString()
    {
        return "InjectionPoint(" + this.getClass().getSimpleName() + ")";
    }

    /**
     * Composite injection point
     * 
     * @author Adam Mummery-Smith
     */
    abstract static class CompositeInjectionPoint extends InjectionPoint
    {
        protected final InjectionPoint[] components;

        protected CompositeInjectionPoint(InjectionPoint... components)
        {
            if (components == null || components.length < 2)
            {
                throw new IllegalArgumentException("Must supply two or more component injection points for composite point!");
            }

            this.components = components;

            for (InjectionPoint component : this.components)
            {
                this.captureLocals |= component.captureLocals;
                this.logLocals |= component.logLocals;
            }
        }

        @Override
        public String toString()
        {
            return "CompositeInjectionPoint(" + this.getClass().getSimpleName() + ")[" + Joiner.on(',').join(this.components) + "]";
        }
    }

    static final class Intersection extends InjectionPoint.CompositeInjectionPoint
    {
        public Intersection(InjectionPoint... points)
        {
            super(points);
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
        {
            boolean found = false;

            @SuppressWarnings("unchecked")
            ArrayList<AbstractInsnNode>[] allNodes = new ArrayList[this.components.length];

            for (int i = 0; i < this.components.length; i++)
            {
                allNodes[i] = new ArrayList<AbstractInsnNode>();
                this.components[i].find(desc, insns, allNodes[i], event);
            }

            ArrayList<AbstractInsnNode> alpha = allNodes[0];
            for (int nodeIndex = 0; nodeIndex < alpha.size(); nodeIndex++)
            {
                AbstractInsnNode node = alpha.get(nodeIndex);
                boolean in = true;

                for (int b = 1; b < allNodes.length; b++)
                {
                    if (!allNodes[b].contains(node))
                    {
                        break;
                    }
                }

                if (!in) continue;

                nodes.add(node);
                found = true;
            }

            return found;
        }
    }

    static final class Union extends InjectionPoint.CompositeInjectionPoint
    {
        public Union(InjectionPoint... points)
        {
            super(points);
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
        {
            LinkedHashSet<AbstractInsnNode> allNodes = new LinkedHashSet<AbstractInsnNode>();

            for (int i = 0; i < this.components.length; i++)
            {
                this.components[i].find(desc, insns, allNodes, event);
            }

            nodes.addAll(allNodes);

            return allNodes.size() > 0;
        }
    }

    static final class Shift extends InjectionPoint
    {
        private final InjectionPoint input;
        private final int shift;

        public Shift(InjectionPoint input, int shift)
        {
            if (input == null)
            {
                throw new IllegalArgumentException("Must supply an input injection point for SHIFT");
            }

            this.input = input;
            this.shift = shift;
        }

        @Override
        public InjectionPoint setCaptureLocals(boolean captureLocals)
        {
            return this.input.setCaptureLocals(captureLocals);
        }

        @Override
        public boolean captureLocals()
        {
            return this.input.captureLocals();
        }

        @Override
        public InjectionPoint setLogLocals(boolean logLocals)
        {
            return this.input.setLogLocals(logLocals);
        }

        @Override
        public boolean logLocals()
        {
            return this.input.logLocals();
        }

        @Override
        public String toString()
        {
            return "InjectionPoint(" + this.getClass().getSimpleName() + ")[" + this.input + "]";
        }

        @Override
        public boolean find(String desc, InsnList insns, Collection<AbstractInsnNode> nodes, Event event)
        {
            List<AbstractInsnNode> list = (nodes instanceof List) ? (List<AbstractInsnNode>)nodes : new ArrayList<AbstractInsnNode>(nodes); 

            this.input.find(desc, insns, nodes, event);

            for (int i = 0; i < list.size(); i++)
            {
                list.set(i, insns.get(insns.indexOf(list.get(i)) + this.shift));
            }

            if (nodes != list)
            {
                nodes.clear();
                nodes.addAll(list);
            }

            return nodes.size() > 0;
        }
    }

    /**
     * Returns a composite injection point which returns the intersection of
     * nodes from all component injection points 
     * 
     * @param operands
     */
    public static InjectionPoint and(InjectionPoint... operands)
    {
        return new InjectionPoint.Intersection(operands);
    }

    /**
     * Returns a composite injection point which returns the union of nodes from
     * all component injection points. 
     * 
     * @param operands
     */
    public static InjectionPoint or(InjectionPoint... operands)
    {
        return new InjectionPoint.Union(operands);
    }

    /**
     * Returns an injection point which returns all insns immediately following
     * insns from the supplied injection point.
     * 
     * @param point
     */
    public static InjectionPoint after(InjectionPoint point)
    {
        return new InjectionPoint.Shift(point, 1);
    }

    /**
     * Returns an injection point which returns all insns immediately prior to
     * insns from the supplied injection point.
     * 
     * @param point
     */
    public static InjectionPoint before(InjectionPoint point)
    {
        return new InjectionPoint.Shift(point, -1);
    }

    /**
     * Returns an injection point which returns all insns offset by the
     * specified "count" from insns from the supplied injection point.
     * 
     * @param point
     */
    public static InjectionPoint shift(InjectionPoint point, int count)
    {
        return new InjectionPoint.Shift(point, count);
    }
}
