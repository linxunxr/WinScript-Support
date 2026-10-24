package com.winscript.support.powershell;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

/**
 * PowerShell 语言定义（.ps1）。
 */
public final class PowerShellLanguage extends Language {

    public static final PowerShellLanguage INSTANCE = new PowerShellLanguage();

    private PowerShellLanguage() {
        super("PowerShell");
    }

    public static @NotNull PowerShellLanguage getInstance() {
        return INSTANCE;
    }
}
