package com.mumfrey.liteloader.util;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;

/**
 * A handle to an input event, this handle will be used to call back the handler
 * for the specified component's events. This class represents a singly-linked
 * list of delegates with each delegate's next field pointing to the next
 * delegate in the chain.
 * 
 * @author Adam Mummery-Smith
 */
public class InputEvent
{
    /**
     * Controller this event is delegating events for
     */
    private final Controller controller;

    /**
     * Component this event is delegating events for
     */
    private final Component component;

    /**
     * Event handler
     */
    private final InputHandler handler;

    /**
     * Next event handler in the chain 
     */
    private InputEvent next;

    /**
     * @param controller
     * @param component
     * @param handler
     */
    InputEvent(Controller controller, Component component, InputHandler handler)
    {
        this.controller = controller;
        this.component = component;
        this.handler = handler;
    }

    /**
     * Link this delegate to the specified delegate and return the start of the
     * delegate chain.
     * 
     * @param chain delegate to link to (will be null if the chain is empty)
     */
    InputEvent link(InputEvent chain)
    {
        if (chain == null) return this; // Chain is empty, return self
        return chain.append(this); // Append self to the start of the chain
    }

    /**
     * Append specified delegate to the end of the delegate chain 
     * 
     * @param delegate
     */
    private InputEvent append(InputEvent delegate)
    {
        InputEvent tail = this; // Start here

        while (tail.next != null) // Find the end of the chain
        {
            tail = tail.next;
        }

        tail.next = delegate; // Append the new delegate
        return this; // Return the start of the delegate chain (eg. this node)
    }

    /**
     * @param event
     */
    void onEvent(Event event)
    {
        if (this.component.isAnalog())
        {
            this.onAxisEvent(event.getValue(), event.getNanos());
        }
        else if (this.component.getIdentifier() == Component.Identifier.Axis.POV)
        {
            this.onPovEvent(event.getValue(), event.getNanos());
        }
        else
        {
            this.onButtonEvent(event.getValue() == 1.0F);
        }
    }

    /**
     * @param value
     * @param nanos
     */
    private void onAxisEvent(float value, long nanos)
    {
        this.handler.onAxisEvent(this, value, nanos);
        if (this.next != null) this.next.onAxisEvent(value, nanos);
    }

    /**
     * @param value
     * @param nanos
     */
    private void onPovEvent(float value, long nanos)
    {
        this.handler.onPovEvent(this, value, nanos);
        if (this.next != null) this.next.onPovEvent(value, nanos);
    }

    /**
     * @param pressed
     */
    private void onButtonEvent(boolean pressed)
    {
        this.handler.onButtonEvent(this, pressed);
        if (this.next != null) this.next.onButtonEvent(pressed);
    }

    public Controller getController()
    {
        return this.controller;
    }

    public Component getComponent()
    {
        return this.component;
    }
}
