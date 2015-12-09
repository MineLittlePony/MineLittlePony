package com.mumfrey.liteloader.core.api;

import com.mumfrey.liteloader.core.api.LoadableModFile;
import com.mumfrey.liteloader.interfaces.LoadableMod;
import java.io.File;

public class LoadableModFileFriend {
   public static LoadableMod<?> getLoadableModFile(File jarFile) {
      return new LoadableModFile(jarFile, (String)null);
   }
}
