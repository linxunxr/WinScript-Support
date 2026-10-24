package com.winscript.support.batch;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Batch 文件类型，扩展名 .bat / .cmd（多扩展名关联在 plugin.xml 中注册）。
 */
public final class BatchFileType extends LanguageFileType {

    public static final BatchFileType INSTANCE = new BatchFileType();

    private BatchFileType() {
        super(BatchLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Batch Script";
    }

    @Override
    public @NotNull String getDescription() {
        return "Windows Batch script";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "bat";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }
}
