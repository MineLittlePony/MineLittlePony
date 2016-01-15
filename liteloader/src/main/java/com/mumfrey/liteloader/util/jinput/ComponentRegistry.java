package com.mumfrey.liteloader.util.jinput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * Registry which keeps track of mappings of JInput descriptors to the
 * controller and component references.
 *
 * @author Adam Mummery-Smith
 */
public class ComponentRegistry
{
    /**
     * List of components
     */
    private static HashMap<String, Component> components = new HashMap<String, Component>();

    /**
     * List of controllers
     */
    private static HashMap<String, Controller> controllers = new HashMap<String, Controller>();

    /**
     * Constructor
     */
    public ComponentRegistry()
    {
    }

    public void enumerate()
    {
        try
        {
            LiteLoaderLogger.info("JInput Component Registry is initialising...");
            this.enumerate(ControllerEnvironment.getDefaultEnvironment());
            LiteLoaderLogger.info("JInput Component Registry initialised, found %d controller(s) %d component(s)",
                    ControllerEnvironment.getDefaultEnvironment().getControllers().length, components.size());
        }
        catch (Throwable th)
        {
        }
    }

    public void enumerate(ControllerEnvironment environment)
    {
        components.clear();
        controllers.clear();

        for (Controller controller : environment.getControllers())
        {
            LiteLoaderLogger.info("Inspecting %s controller %s on %s...", controller.getType(), controller.getName(), controller.getPortType()); 
            for (Component component : controller.getComponents())
            {
                this.addComponent(controller, component);
            }
        }
    }

    private String addComponent(Controller controller, Component component)
    {
        String descriptor = ComponentRegistry.getDescriptor(controller, component);
        components.put(descriptor, component);
        controllers.put(descriptor, controller);
        return descriptor;
    }

    public ArrayList<Component> getComponents(String descriptor)
    {
        ArrayList<Component> components = new ArrayList<Component>();

        int offset = 0;
        Component component = null;

        do
        {
            component = this.getComponent(descriptor, offset++);

            if (components.contains(component))
            {
                component = null;
            }

            if (component != null)
            {
                components.add(component);
            }

        } while (component != null && components.size() < 32);

        return components;
    }

    public Component getComponent(String descriptor)
    {
        return this.getComponent(descriptor, 0);
    }

    protected Component getComponent(String descriptor, int offset)
    {
        if (components.containsKey(descriptor))
        {
            return components.get(descriptor);
        }

        for (Entry<String, Component> entry : components.entrySet())
        {
            if (matches(entry.getKey(), descriptor))
            {
                if (--offset < 0)
                {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public ArrayList<Controller> getControllers(String descriptor)
    {
        ArrayList<Controller> controllers = new ArrayList<Controller>();

        int offset = 0;
        Controller controller = null;

        do
        {
            controller = this.getController(descriptor, offset++); 

            if (controllers.contains(controller))
            {
                controller = null;
            }

            if (controller != null)
            {
                controllers.add(controller);
            }

        } while (controller != null && controllers.size() < 32);

        return controllers;
    }

    public Controller getController(String descriptor)
    {
        return this.getController(descriptor, 0);
    }

    protected Controller getController(String descriptor, int offset)
    {
        if (controllers.containsKey(descriptor))
        {
            return controllers.get(descriptor);
        }

        for (Entry<String, Controller> entry : controllers.entrySet())
        {
            if (matches(entry.getKey(), descriptor))
            {
                if (--offset < 0)
                {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public static String getDescriptor(Controller controller, Component component)
    {
        int index = 0;
        String controllerPath = ComponentRegistry.getControllerPath(controller);
        String componentId = component.getIdentifier().getName();

        String descriptor = ComponentRegistry.getDescriptor(controllerPath, componentId, index);

        while (components.containsKey(descriptor) && components.get(descriptor) != component)
        {
            descriptor = ComponentRegistry.getDescriptor(controllerPath, componentId, ++index);
        }

        return descriptor;
    }

    /**
     * @param type
     * @param name
     * @param portType
     * @param portNumber
     * @param component
     * @param index
     */
    public static String getDescriptor(String type, String name, String portType, int portNumber, String component, int index)
    {
        String controllerPath = ComponentRegistry.getControllerPath(type, name, portType, portNumber);
        return ComponentRegistry.getDescriptor(controllerPath, component, index);
    }

    /**
     * @param controller
     * @param component
     * @param index 
     */
    private static String getDescriptor(String controller, String component, int index)
    {
        String descriptor = String.format("jinput:%s/%s/%d", controller, ComponentRegistry.format(component), index);
        return descriptor;
    }

    /**
     * @param controller
     */
    private static String getControllerPath(Controller controller)
    {
        return ComponentRegistry.getControllerPath(
                controller.getType().toString().toLowerCase(),
                controller.getName(),
                controller.getPortType().toString(),
                controller.getPortNumber()
                );
    }

    /**
     * @param type
     * @param name
     * @param portType
     * @param portNumber
     */
    public static String getControllerPath(String type, String name, String portType, int portNumber)
    {
        return String.format("%s/%s/%s/%d",
                ComponentRegistry.format(type),
                ComponentRegistry.format(name),
                ComponentRegistry.format(portType),
                portNumber
                );
    }

    public static boolean matches(String descriptor, String wildDescriptor)
    {
        String[] descriptorParts = ComponentRegistry.splitDescriptor(descriptor.trim());
        String[] wildDescriptorParts = ComponentRegistry.splitDescriptor(wildDescriptor.trim());

        if (descriptorParts.length != wildDescriptorParts.length) return false;

        for (int i = 0; i < descriptorParts.length; i++)
        {
            if (wildDescriptorParts[i].length() > 0 && descriptorParts[i].length() > 0
                    && !wildDescriptorParts[i].equals(descriptorParts[i])
                    && !wildDescriptorParts[i].equals("*"))
            {
                return false;
            }
        }

        return true;
    }

    public static String[] splitDescriptor(String descriptor)
    {
        if (descriptor.startsWith("jinput:"))
        {
            String[] path = descriptor.split("(?<!\\\\)/");
            for (int i = 0; i < path.length; i++) path[i] = path[i].replaceAll("\\\\/", "/");
            return path;
        }

        return new String[0];
    }

    public static String format(String descriptorPart)
    {
        return descriptorPart == null ? "0" : descriptorPart.replaceAll("/", "\\\\/");
    }
}
