package com.winscript.support.execution.common;

import com.winscript.support.settings.WinScriptSettings;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shell 解释器路径解析：优先用户设置，其次系统环境。
 */
public final class ShellPathResolver {

    private ShellPathResolver() {
    }

    /**
     * Batch 解释器：设置覆盖 → %ComSpec% → cmd.exe。
     */
    public static @NotNull String resolveCmdExe() {
        String override = WinScriptSettings.getInstance().getBatchInterpreter();
        if (override != null && !override.isBlank()) {
            return override;
        }
        String comSpec = System.getenv("ComSpec");
        return comSpec != null && !comSpec.isBlank() ? comSpec : "cmd.exe";
    }

    /**
     * 在 PATH 上查找可执行文件（自动补 .exe），找不到返回 null。
     */
    public static String findOnPath(@NotNull String executable) {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null) {
            return null;
        }
        String candidate = executable.endsWith(".exe") ? executable : executable + ".exe";
        for (String dir : pathEnv.split(";")) {
            if (dir.isBlank()) {
                continue;
            }
            Path full = Path.of(dir, candidate);
            if (Files.isRegularFile(full)) {
                return full.toString();
            }
        }
        return null;
    }
}
