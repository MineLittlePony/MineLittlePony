package com.mumfrey.liteloader.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.profiler.Profiler;

import com.mumfrey.liteloader.common.GameEngine;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.launch.LoaderEnvironment;
import com.mumfrey.liteloader.launch.LoaderProperties;
import com.mumfrey.liteloader.util.jinput.ComponentRegistry;

/**
 * Mod input class, aggregates functionality from LiteLoader's mod key
 * registration functions and JInputLib.
 *
 * @author Adam Mummery-Smith
 */
public final class InputManager extends Input
{
    private GameEngine<?, ?> engine;

    /**
     * 
     */
    private Profiler profiler;

    /**
     * File in which we will store mod key mappings
     */
    private final File keyMapSettingsFile;

    /**
     * Properties object which stores mod key mappings
     */
    private final Properties keyMapSettings = new Properties();

    /**
     * List of all registered mod keys
     */
    private final List<KeyBinding> modKeyBindings = new ArrayList<KeyBinding>();

    /**
     * Map of mod key bindings to their key codes, stored so that we don't need
     * to cast from string in the properties file every tick.
     */
    private final Map<KeyBinding, Integer> storedModKeyBindings = new HashMap<KeyBinding, Integer>();

    /**
     * JInput component registry 
     */
    private final ComponentRegistry jInputComponentRegistry;

    /**
     * List of handlers for JInput components
     */
    private final Map<Component, InputEvent> componentEvents = new HashMap<Component, InputEvent>();

    /**
     * JInput Controllers to poll
     */
    private Controller[] pollControllers = new Controller[0];

    /**
     * 
     */
    public InputManager(LoaderEnvironment environment, LoaderProperties properties)
    {
        if (LiteLoader.getInstance() != null && LiteLoader.getInput() != null)
        {
            throw new IllegalStateException("Only one instance of Input is allowed, use LiteLoader.getInput() to get the active instance");
        }

        this.keyMapSettingsFile = new File(environment.getCommonConfigFolder(), "liteloader.keys.properties");
        this.jInputComponentRegistry = new ComponentRegistry();

        if (!properties.getAndStoreBooleanProperty(LoaderProperties.OPTION_JINPUT_DISABLE, false))
        {
            this.jInputComponentRegistry.enumerate();
        }
    }

    @Override
    public void onInit()
    {
        if (this.keyMapSettingsFile.exists())
        {
            try
            {
                this.keyMapSettings.load(new FileReader(this.keyMapSettingsFile));
            }
            catch (Exception ex) {}
        }
    }

    @Override
    public void onPostInit(GameEngine<?, ?> engine)
    {
        this.engine = engine;
        this.profiler = engine.getProfiler();
    }

    /**
     * Register a key for a mod
     * 
     * @param binding
     */
    @Override
    public void registerKeyBinding(KeyBinding binding)
    {
        List<KeyBinding> keyBindings = this.engine.getKeyBindings();

        if (!keyBindings.contains(binding))
        {
            if (this.keyMapSettings.containsKey(binding.getKeyDescription()))
            {
                try
                {
                    int code = Integer.parseInt(this.keyMapSettings.getProperty(binding.getKeyDescription(), String.valueOf(binding.getKeyCode())));
                    binding.setKeyCode(code);
                }
                catch (NumberFormatException ex) {}
            }

            keyBindings.add(binding);

            this.engine.setKeyBindings(keyBindings);
            this.modKeyBindings.add(binding);

            this.updateBinding(binding);
            this.storeBindings();

            KeyBinding.resetKeyBindingArrayAndHash();
        }
    }

    /**
     * Unregisters a registered keybind with the game settings class, thus
     * removing it from the "controls" screen.
     * 
     * @param binding
     */
    @Override
    public void unRegisterKeyBinding(KeyBinding binding)
    {
        List<KeyBinding> keyBindings = this.engine.getKeyBindings();

        if (keyBindings.contains(binding))
        {
            keyBindings.remove(binding);
            this.engine.setKeyBindings(keyBindings);

            this.modKeyBindings.remove(binding);

            KeyBinding.resetKeyBindingArrayAndHash();
        }
    }

    /**
     * Checks for changed mod keybindings and stores any that have changed 
     */
    @Override
    public void onTick(boolean clock, float partialTicks, boolean inGame)
    {
        this.profiler.startSection("keybindings");
        if (clock)
        {
            boolean updated = false;

            for (KeyBinding binding : this.modKeyBindings)
            {
                if (binding.getKeyCode() != this.storedModKeyBindings.get(binding))
                {
                    this.updateBinding(binding);
                    updated = true;
                }
            }

            if (updated) this.storeBindings();
        }

        this.pollControllers();
        this.profiler.endSection();
    }

    /**
     * @param binding
     */
    private void updateBinding(KeyBinding binding)
    {
        this.keyMapSettings.setProperty(binding.getKeyDescription(), String.valueOf(binding.getKeyCode()));
        this.storedModKeyBindings.put(binding, Integer.valueOf(binding.getKeyCode()));
    }

    @Override
    public void onShutDown()
    {
        this.storeBindings();
    }

    /**
     * Writes mod bindings to disk
     */
    @Override
    public void storeBindings()
    {
        try
        {
            this.keyMapSettings.store(new FileWriter(this.keyMapSettingsFile),
                    "Mod key mappings for LiteLoader mods, stored here to avoid losing settings stored in options.txt");
        }
        catch (IOException ex) {}
    }

    /**
     * Gets the underlying JInput component registry
     */
    @Override
    public ComponentRegistry getComponentRegistry()
    {
        return this.jInputComponentRegistry;
    }

    /**
     * Returns a handle to the event described by descriptor (or null if no
     * component is found matching the descriptor. Retrieving an event via this
     * method adds the controller (if found) to the polling list and causes it
     * to raise events against the specified handler.
     * 
     * <p>This method returns an {@link InputEvent} which is passed as an
     * argument to the relevant callback on the supplied handler in order to
     * identify the event. For example:</p>
     * 
     * <code>this.joystickButton = input.getEvent(descriptor, this);</code>
     * 
     * <p>then in onAxisEvent</p>
     * 
     * <code>if (source == this.joystickButton) // do something with button
     * </code>
     * 
     * @param descriptor
     * @param handler
     */
    @Override
    public InputEvent getEvent(String descriptor, InputHandler handler)
    {
        if (handler == null) return null;
        Component component = this.jInputComponentRegistry.getComponent(descriptor);
        Controller controller = this.jInputComponentRegistry.getController(descriptor);
        return this.addEventHandler(controller, component, handler);
    }

    /**
     * Get events for all components which match the supplied descriptor 
     * 
     * @param descriptor
     * @param handler
     */
    @Override
    public InputEvent[] getEvents(String descriptor, InputHandler handler)
    {
        List<InputEvent> events = new ArrayList<InputEvent>();
        Controller controller = this.jInputComponentRegistry.getController(descriptor);
        if (controller != null)
        {
            for (Component component : controller.getComponents())
            {
                events.add(this.addEventHandler(controller, component, handler));
            }
        }

        return events.toArray(new InputEvent[0]);
    }

    /**
     * @param controller
     * @param component
     * @param handler
     */
    private InputEvent addEventHandler(Controller controller, Component component, InputHandler handler)
    {
        if (controller != null && component != null && handler != null)
        {
            this.addController(controller);

            InputEvent event = new InputEvent(controller, component, handler);
            this.componentEvents.put(component, event.link(this.componentEvents.get(component)));

            return event;
        }

        return null;
    }

    /**
     * @param controller
     */
    private void addController(Controller controller)
    {
        Set<Controller> controllers = this.getActiveControllers();
        controllers.add(controller);
        this.setActiveControllers(controllers);
    }

    /**
     * 
     */
    private Set<Controller> getActiveControllers()
    {
        Set<Controller> allControllers = new HashSet<Controller>();
        for (Controller controller : this.pollControllers)
            allControllers.add(controller);
        return allControllers;
    }

    /**
     * @param controllers
     */
    private void setActiveControllers(Set<Controller> controllers)
    {
        this.pollControllers = controllers.toArray(new Controller[controllers.size()]);
    }

    /**
     * 
     */
    private void pollControllers()
    {
        for (Controller controller : this.pollControllers)
        {
            controller.poll();
            EventQueue controllerQueue = controller.getEventQueue();

            for (Event event = new Event(); controllerQueue.getNextEvent(event); )
            {
                Component cmp = event.getComponent();

                InputEvent inputEvent = this.componentEvents.get(cmp);
                if (inputEvent != null)
                {
                    inputEvent.onEvent(event);
                }
            }
        }
    }
}
