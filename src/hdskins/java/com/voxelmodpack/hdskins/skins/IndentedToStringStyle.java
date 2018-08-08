package com.voxelmodpack.hdskins.skins;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static net.minecraft.util.text.TextFormatting.*;

public class IndentedToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 2031593562293731492L;

    private static final ToStringStyle INSTANCE = new IndentedToStringStyle();

    private IndentedToStringStyle() {
        this.setFieldNameValueSeparator(": " + RESET + ITALIC);
        this.setContentStart(null);
        this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  " + RESET + YELLOW);
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd(null);
        this.setUseIdentityHashCode(false);
        this.setUseShortClassName(true);
    }

    public static class Builder extends ToStringBuilder {
        public Builder(Object o) {
            super(o, IndentedToStringStyle.INSTANCE);
        }

        public String build() {
            return YELLOW + super.build();
        }
    }
}
