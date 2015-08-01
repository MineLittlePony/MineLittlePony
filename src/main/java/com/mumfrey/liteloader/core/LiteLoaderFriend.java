package com.mumfrey.liteloader.core;

import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.api.LoadableModFileFriend;
import java.io.File;

public abstract class LiteLoaderFriend {
   public static void loadMod(String identifier, Class<? extends LiteMod> mod, File jarFile) throws InstantiationException, IllegalAccessException {
      LiteLoader.getInstance().mods.loadMod(identifier, mod, LoadableModFileFriend.getLoadableModFile(jarFile));
   }
}
