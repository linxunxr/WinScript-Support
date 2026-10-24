package com.winscript.support.powershell;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * PowerShell 文件类型，扩展名 .ps1。
 */
public final class PowerShellFileType extends LanguageFileType {

    public static final PowerShellFileType INSTANCE = new PowerShellFileType();

    private PowerShellFileType() {
        super(PowerShellLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "PowerShell Script";
    }

    @Override
    public @NotNull String getDescription() {
        return "PowerShell script";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "ps1";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }
}
