package com.mumfrey.liteloader.modconfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

/**
 * Manages serialisation of exposable properties to a JSON config file via Gson 
 *
 * @author Adam Mummery-Smith
 */
public final class ExposableConfigWriter implements InstanceCreator<Exposable>
{
    /**
     * Minimum number of milliseconds that must elapse between writes
     */
    private static final long ANTI_HAMMER_DELAY = 1000L;

    /**
     * Exposable instance which we will serialise exposed properties 
     */
    private final Exposable exposable;

    /**
     * JSON file to write to
     */
    private final File configFile;

    /**
     * True if this is a versioned config strategy
     */
    private final boolean versioned;

    /**
     * Disable anti-hammer and always save when requested 
     */
    private final boolean aggressive; 

    /**
     * Gson instance
     */
    private final Gson gson;

    /**
     * True if a config write has been requested but anti-hammer has prevented
     * the write from occurring.
     */
    private volatile boolean dirty = false;

    /**
     * Last time the config was written, used for anti-hammer
     */
    private volatile long lastWrite = 0L;

    /**
     * It's possible that writes may be requested from different threads, lock
     * object to prevent cross-thread derp.
     */
    private Object readWriteLock = new Object();

    /**
     * @param exposable
     * @param configFile
     */
    private ExposableConfigWriter(Exposable exposable, File configFile, boolean versioned, boolean aggressive)
    {
        this.exposable = exposable;
        this.configFile = configFile;
        this.versioned = versioned;
        this.aggressive = aggressive;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.serializeNulls();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(exposable.getClass(), this);

        if (this.exposable instanceof AdvancedExposable)
        {
            ((AdvancedExposable)this.exposable).setupGsonSerialiser(gsonBuilder);
        }

        this.gson = gsonBuilder.create();
    }

    /**
     * Get the config file underlying this writer
     */
    File getConfigFile()
    {
        return this.configFile;
    }

    /**
     * Returns true if this writer is using a versioned strategy
     */
    boolean isVersioned()
    {
        return this.versioned;
    }

    /**
     * Returns true if anti-hammer is disabled for this writer
     */
    public boolean isAggressive()
    {
        return this.aggressive;
    }

    /**
     * Returns true if this writer has been invalidated but not yet been flushed
     * to disk.
     */
    boolean isDirty()
    {
        return this.dirty;
    }

    /* (non-Javadoc)
     * @see com.google.gson.InstanceCreator
     *      #createInstance(java.lang.reflect.Type)
     */
    @Override
    public Exposable createInstance(Type type)
    {
        return this.exposable;
    }

    /**
     * Initialise the config, reads from file and writes the initial config file
     * if not present.
     */
    void init()
    {
        // Read the config
        this.read();

        // If the config doesn't exist yet, seed the config
        if (!this.configFile.exists())
        {
            this.write();
        }
    }

    /**
     * Read the config from the file
     */
    void read()
    {
        synchronized (this.readWriteLock)
        {
            if (this.configFile.exists())
            {
                FileReader reader = null;

                try
                {
                    reader = new FileReader(this.configFile);

                    // Normally GSON would produce a new object by calling the default constructor, but we
                    // trick it into deserialising properties on the existing object instance by implementing
                    // an InstanceCreator which just returns the object instance which we already have
                    this.gson.fromJson(reader, this.exposable.getClass());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if (reader != null)
                        {
                            reader.close();
                        }
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Write the config to the file
     */
    void write()
    {
        synchronized (this.readWriteLock)
        {
            FileWriter writer = null;
            try
            {
                writer = new FileWriter(this.configFile);
                this.gson.toJson(this.exposable, writer);

                this.dirty = false;
                this.lastWrite = System.currentTimeMillis();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                try
                {
                    if (writer != null)
                    {
                        writer.close();
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Write the config to file, respecting anti-hammer and queuing the write if
     * not enough time has elapsed.
     */
    void invalidate()
    {
        if (!this.aggressive)
        {
            long sinceLastWrite = System.currentTimeMillis() - this.lastWrite;
            if (sinceLastWrite < ANTI_HAMMER_DELAY)
            {
                this.dirty = true;
                return;
            }
        }

        this.write();
    }

    /**
     * Handle latent writes if the config was previously invalidated
     */
    void onTick()
    {
        if (!this.aggressive && this.dirty)
        {
            long sinceLastWrite = System.currentTimeMillis() - this.lastWrite;
            if (sinceLastWrite >= ANTI_HAMMER_DELAY)
            {
                this.write();
            }
        }
    }

    /**
     * Force a write if dirty
     */
    void sync()
    {
        if (this.dirty || this.aggressive)
        {
            this.write();
        }
    }

    /**
     * Factory method which creates and intialises a new ExposableConfigWriter
     * for the specified exposable object and strategy.
     * 
     * @param exposable
     * @param strategy
     * @param fileName
     */
    static ExposableConfigWriter create(Exposable exposable, ConfigStrategy strategy, String fileName, boolean aggressive)
    {
        if (!fileName.toLowerCase().endsWith(".json"))
        {
            fileName = fileName + ".json";
        }

        File configFile = strategy.getFileForStrategy(fileName);

        if (exposable instanceof AdvancedExposable)
        {
            File customConfigFile = ((AdvancedExposable)exposable).getConfigFile(configFile, configFile.getParentFile(), fileName);
            if (customConfigFile != null)
            {
                configFile = customConfigFile;
            }
        }

        ExposableConfigWriter writer = new ExposableConfigWriter(exposable, configFile, strategy == ConfigStrategy.Versioned, aggressive);

        return writer;
    }
}