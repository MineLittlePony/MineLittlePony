package com.voxelmodpack.hdskins.skins;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IndentedToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 2031593562293731492L;

    public static final ToStringStyle INSTANCE = new IndentedToStringStyle();

    private IndentedToStringStyle() {
        this.setContentStart(null);
        this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
        this.setFieldSeparatorAtStart(true);
        this.setContentEnd(null);
        this.setUseIdentityHashCode(false);
        this.setUseShortClassName(true);
    }
}
