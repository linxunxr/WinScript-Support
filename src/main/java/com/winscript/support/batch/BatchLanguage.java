package com.winscript.support.batch;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * Batch 语言定义（.bat / .cmd）。
 */
public final class BatchLanguage extends Language {

    public static final BatchLanguage INSTANCE = new BatchLanguage();

    private BatchLanguage() {
        super("Batch");
    }

    public static @NotNull BatchLanguage getInstance() {
        return INSTANCE;
    }
}
