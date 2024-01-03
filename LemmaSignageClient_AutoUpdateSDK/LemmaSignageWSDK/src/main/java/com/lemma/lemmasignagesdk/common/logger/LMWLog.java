package com.lemma.lemmasignagesdk.common.logger;

import org.jetbrains.annotations.NonNls;

public class LMWLog {

    public static void i(@NonNls String message, Object... args) {
        LMWLogger.i(message, args);
    }

    public static void e(@NonNls String message, Object... args) {
        LMWLogger.e(message, args);
    }

    public static void d(@NonNls String message, Object... args) {
        LMWLogger.d(message, args);
    }

    public static void w(@NonNls String message, Object... args) {
        LMWLogger.w(message, args);
    }

    public static void v(@NonNls String message, Object... args) {
        LMWLogger.v(message, args);
    }

}
