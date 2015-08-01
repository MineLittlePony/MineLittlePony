package com.voxelmodpack.common.status;

import java.util.Random;

import net.minecraft.client.gui.FontRenderer;

/**
 * A status message instance, this is returned from the manager to a mod to
 * allow the mod to control its own status message display
 * 
 * @author anangrybeaver
 */
public class StatusMessage implements Comparable<StatusMessage> {
    /**
     * Message format
     */
    private static final String MESSAGE_FORMAT = "\247%s%s\247f: \247%s%s\247r";

    private static final int MESSAGE_HEIGHT = 10;

    /**
     * Colour for the title text
     */
    private char titleColour = 'f';

    /**
     * Colour for the status text
     */
    private char textColour = 'a';

    /**
     * The label for this particular status message, not displayed
     */
    private final String label;

    /**
     * Status message title
     */
    private String title = "";

    /**
     * Status message text
     */
    private String text = "";

    /**
     * Priority, for choosing display order
     */
    private int priority = 0;

    /**
     * Order the message was added, this is used to order messages which have
     * the same priority
     */
    private int order = 0;

    /**
     * Show this message
     */
    private boolean visible = false;

    public StatusMessage(String label) {
        this.label = label;
        this.order = new Random().nextInt(Integer.MAX_VALUE);
    }

    public StatusMessage(String label, int priority, int order) {
        this(label);

        this.priority = priority;
        this.order = order;
    }

    public StatusMessage setTitleAndText(String title, String text) {
        this.title = title;
        this.text = text;
        return this;
    }

    public StatusMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    public StatusMessage setText(String text) {
        this.text = text;
        return this;
    }

    public StatusMessage setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public StatusMessage setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public StatusMessage setTextColour(char textColour) {
        this.textColour = textColour;
        return this;
    }

    public StatusMessage setTitleColour(char titleColour) {
        this.titleColour = titleColour;
        return this;
    }

    public StatusMessage setColours(char textColour, char titleColour) {
        this.textColour = textColour;
        this.titleColour = titleColour;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public String getText() {
        return this.text;
    }

    public String getLabel() {
        return this.label;
    }

    public int getPriority() {
        return this.priority;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public int drawStatus(FontRenderer fontRenderer, int xPos, int yPos) {
        if (this.isVisible()) {
            fontRenderer.drawStringWithShadow(
                    String.format(MESSAGE_FORMAT, this.titleColour, this.title, this.textColour, this.text), xPos, yPos,
                    0xffffff);
            return MESSAGE_HEIGHT;
        }

        return 0;
    }

    @Override
    public int compareTo(StatusMessage other) {
        if (other == null)
            return 0;

        if (this.priority == other.priority)
            return this.order - other.order;

        return this.priority - other.priority;
    }
}
