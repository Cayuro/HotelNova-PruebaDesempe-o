package com.app.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class AppLogger {

    private static final Logger LOGGER = Logger.getLogger("HotelNovaLogger");
    private static boolean initialized = false;

    private AppLogger() {
    }

    /**
     * Lazy logger initialization keeps startup simple and avoids configuring
     * handlers until logging is actually needed.
     */
    private static synchronized void init() {
        if (initialized) {
            return;
        }
        try {
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    return String.format("%1$tF %1$tT [%2$s] %3$s%n",
                            record.getMillis(),
                            record.getLevel().getName(),
                            formatMessage(record));
                }
            });
            LOGGER.setUseParentHandlers(false);
            LOGGER.addHandler(fileHandler);
            initialized = true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not initialize file logger.", e);
            initialized = true;
        }
    }

    public static void info(String message) {
        init();
        LOGGER.info(message);
    }

    public static void warn(String message) {
        init();
        LOGGER.warning(message);
    }

    public static void error(String message, Throwable throwable) {
        init();
        LOGGER.log(Level.SEVERE, message, throwable);
    }
}
