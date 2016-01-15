package net.eq2online.permissions;

import java.io.*;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.network.PacketBuffer;

/**
 * Serializable container object
 * 
 * @author Adam Mummery-Smith
 */
public class ReplicatedPermissionsContainer implements Serializable
{
    /**
     * Serial version UID to suppoer Serializable interface 
     */
    private static final long serialVersionUID = -764940324881984960L;

    /**
     * Mod name
     */
    public String modName = "all";

    /**
     * Mod version
     */
    public Float modVersion = 0.0F;

    /**
     * List of permissions to replicate, prepend "-" for a negated permission
     * and "+" for a granted permission.
     */
    public Set<String> permissions = new TreeSet<String>();

    /**
     * Amount of time in seconds that the client will trust these permissions
     * for before requesting an update. 
     */
    public long remoteCacheTimeSeconds = 600L;  // 10 minutes

    public static final String CHANNEL = "PERMISSIONSREPL";

    public ReplicatedPermissionsContainer()
    {
    }

    public ReplicatedPermissionsContainer(String modName, Float modVersion, Collection<String> permissions)
    {
        this.modName = modName;
        this.modVersion = modVersion;
        this.permissions.addAll(permissions);
    }

    /**
     * Add all of the listed permissions to this container
     * 
     * @param permissions
     */
    public void addAll(Collection<String> permissions)
    {
        this.permissions.addAll(permissions);
    }

    /**
     * Check and correct  
     */
    public void sanitise()
    {
        if (this.modName == null || this.modName.length() < 1) this.modName = "all";
        if (this.modVersion == null || this.modVersion < 0.0F) this.modVersion = 0.0F;
        if (this.remoteCacheTimeSeconds < 0) this.remoteCacheTimeSeconds = 600L;
    }

    /**
     * Serialise this container to a byte array for transmission to a remote
     * host.
     */
    public byte[] getBytes()
    {
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            new ObjectOutputStream(byteStream).writeObject(this);
            return byteStream.toByteArray();
        }
        catch (IOException e) {}

        return new byte[0];
    }

    /**
     * Deserialises a replicated permissions container from a byte array
     * 
     * @param data Byte array containing the serialised data
     * @return new container or null if deserialisation failed
     */
    public static ReplicatedPermissionsContainer fromPacketBuffer(PacketBuffer data)
    {
        try
        {
            int readableBytes = data.readableBytes();
            if (readableBytes == 0) return null;

            byte[] payload = new byte[readableBytes];
            data.readBytes(payload);

            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(payload));
            ReplicatedPermissionsContainer object = (ReplicatedPermissionsContainer)inputStream.readObject();
            return object;
        }
        catch (IOException e)
        {
            // Don't care
        }
        catch (ClassNotFoundException e)
        {
            // Don't care
        }
        catch (ClassCastException e)
        {
            // Don't care
        }

        return null;
    }
}
