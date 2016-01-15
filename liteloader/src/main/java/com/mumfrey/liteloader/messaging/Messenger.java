package com.mumfrey.liteloader.messaging;

import java.util.List;

import com.mumfrey.liteloader.api.Listener;

/**
 * Interface for listeners that want to receive (or send) 
 * 
 * @author Adam Mummery-Smith
 */
public interface Messenger extends Listener
{
    /**
     * <p>Get listening channels for this Messenger. Channel names must follow
     * the format:</p>
     * 
     * <code>{category}:{channel}</code>
     *   
     * <p>where both <tt>{category}</tt> and <tt>{channel}</tt> are
     * alpha-numeric identifiers which can contain underscore or dash but must
     * begin and end with only alpha-numeric characters: for example the
     * following channel names are valid:
     * 
     * <ul>
     *     <li>foo:bar</li>
     *     <li>foo-bar:baz</li>
     *     <li>foo-bar:baz_derp</li>
     * </ul>
     *  
     * <p>The following are <b>invalid</b>:</p>
     * 
     * <ul>
     *     <li>foo</li>
     *     <li>foo_:bar</li>
     *     <li>_foo:bar</li>
     * </ul>
     * 
     * <p>In general, your listener should listen on channels all beginning with
     * the same category, which may match your mod id. Channel names and
     * categories are case-sensitive.</p>
     * 
     * @return List of channels to listen on
     */
    public abstract List<String> getMessageChannels();

    /**
     * Called when a message matching a channel you have elected to listen on is
     * dispatched by any agent. <b>WARNING</b> this method is called if you
     * dispatch a message on a channel you are listening to, thus you should
     * <b>avoid</b> replying on channels you are listening to <b>unless</b> you
     * specifically filter messages based on their sender:
     * 
     * <code>if (message.getSender() == this) return;</code>
     *   
     * <p>Messages may have a null sender or payload but will never have a null
     * channel.</p>
     * 
     * @param message
     */
    public abstract void receiveMessage(Message message);
}
