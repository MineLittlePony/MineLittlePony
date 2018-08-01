package com.voxelmodpack.hdskins.skins;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ToStringStyle;

public class IndentedToStringStyle extends ToStringStyle {

    private static final long serialVersionUID = 2031593562293731492L;

    public static final ToStringStyle INSTANCE = new IndentedToStringStyle();

    private IndentedToStringStyle() {
        setContentStart(null);
        setFieldSeparator(SystemUtils.LINE_SEPARATOR + "  ");
        setFieldSeparatorAtStart(true);
        setContentEnd(null);
        setUseIdentityHashCode(false);
        setUseShortClassName(true);
    }
}
