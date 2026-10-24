package com.winscript.support.execution.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 运行配置命令行构造辅助：项目宏展开与公共参数处理。
 * <p>
 * 2025.3 平台已移除 PathMacroManager 的公开 API，此处自行展开最常用的项目宏，
 * 其余内容原样返回（M2 里程碑再完善宏集合）。
 */
public final class ScriptCommandLineBuilder {

    private ScriptCommandLineBuilder() {
    }

    /**
     * 展开路径中的项目宏（$ProjectFileDir$、$ProjectName$）。
     */
    public static @NotNull String expand(@NotNull Project project, @NotNull String path) {
        String result = path;
        String baseDir = project.getBasePath();
        if (baseDir != null) {
            result = result.replace("$ProjectFileDir$", baseDir);
        }
        result = result.replace("$ProjectName$", project.getName());
        return FileUtil.toSystemDependentName(result);
    }
}
