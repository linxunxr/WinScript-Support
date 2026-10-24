package com.winscript.support.execution.powershell;

import com.winscript.support.execution.common.ShellPathResolver;
import com.winscript.support.settings.WinScriptSettings;
import org.jetbrains.annotations.NotNull;

/**
 * PowerShell 解释器探测：设置覆盖 → PATH 上的 pwsh.exe（PowerShell 7+）→
 * PATH 上的 powershell.exe（Windows PowerShell 5.1）。
 */
public final class PowerShellInterpreterDetector {

    private PowerShellInterpreterDetector() {
    }

    public static @NotNull String detect() {
        String override = WinScriptSettings.getInstance().getPowerShellInterpreter();
        if (override != null && !override.isBlank()) {
            return override;
        }
        String pwsh = ShellPathResolver.findOnPath("pwsh.exe");
        if (pwsh != null) {
            return pwsh;
        }
        String windowsPowerShell = ShellPathResolver.findOnPath("powershell.exe");
        return windowsPowerShell != null ? windowsPowerShell : "powershell.exe";
    }
}
