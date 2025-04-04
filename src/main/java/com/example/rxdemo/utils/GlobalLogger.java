package com.example.rxdemo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalLogger {
    public static final Logger logger = LoggerFactory.getLogger(GlobalLogger.class);

    private GlobalLogger() {
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public static void debug(String message) {
        logger.debug(message);
    }

    public static void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public static void warn(String message) {
        logger.warn(message);
    }

    public static void error(String message) {
        logger.error(message);
    }

    public static void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public static void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

}

