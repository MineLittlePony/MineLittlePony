package com.brohoof.minelittlepony.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mumfrey.liteloader.util.log.LiteLoaderLogger;

public class MineLPLogger {
   private static Logger logger = LogManager.getLogger("MineLittlePony");

   public static Logger getLogger() {
      return logger;
   }

   private static void log(Level level, String format, Object... data) {
      logger.log(level, "MineLittlePony> " + String.format(format, data));
   }

   private static void log(Level level, Throwable t, String format, Object... data) {
      logger.log(level, "MineLittlePony> " + String.format(format, data), t);
   }

   public static void info(String message) {
      log(Level.INFO, message, new Object[0]);
   }

   public static void info(String message, Object... data) {
      log(Level.INFO, message, data);
   }

   public static void debug(String message) {
      if(LiteLoaderLogger.DEBUG) {
         log(Level.INFO, message, new Object[0]);
      }

   }

   public static void debug(String message, Object... data) {
      if(LiteLoaderLogger.DEBUG) {
         log(Level.INFO, message, data);
      }

   }

   public static void debug(Throwable t, String message, Object... data) {
      if(LiteLoaderLogger.DEBUG) {
         log(Level.INFO, message, new Object[]{data, t});
      }

   }

   public static void warn(String message) {
      log(Level.WARN, message, new Object[0]);
   }

   public static void warn(String message, Object... data) {
      log(Level.WARN, message, data);
   }

   public static void warn(Throwable t, String message, Object... data) {
      log(Level.WARN, t, message, data);
   }

   public static void error(String message) {
      log(Level.ERROR, message, new Object[0]);
   }

   public static void error(String message, Object... data) {
      log(Level.ERROR, message, data);
   }

   public static void error(Throwable t, String message, Object... data) {
      log(Level.ERROR, t, message, data);
   }
}
