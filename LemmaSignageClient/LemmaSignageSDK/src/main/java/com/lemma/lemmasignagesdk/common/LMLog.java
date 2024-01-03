package com.lemma.lemmasignagesdk.common;

import org.jetbrains.annotations.NonNls;

public class LMLog {

    public static void i(String tag, String message) {
        LMLogger.i(message);
    }

    public static void i(@NonNls String message, Object... args) {
        LMLogger.i(message, args);
    }

    public static void e(String tag, String message) {
        LMLogger.e(message);
    }

    public static void e(@NonNls String message, Object... args) {
        LMLogger.e(message, args);
    }

    public static void d(String tag, String message) {
        LMLogger.d(message);
    }

    public static void d(@NonNls String message, Object... args) {
        LMLogger.d(message, args);
    }

    public static void w(String tag, String message) {
        LMLogger.w(message);
    }

    public static void w(@NonNls String message, Object... args) {
        LMLogger.w(message, args);
    }

    public static void v(String tag, String message) {
        LMLogger.v(message);
    }

    public static void v(@NonNls String message, Object... args) {
        LMLogger.v(message, args);
    }

}
