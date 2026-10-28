package com.winscript.support.execution.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 运行配置命令行构造辅助：项目宏与文件宏展开。
 * <p>
 * 2025.3 平台已移除 PathMacroManager 的公开 API，此处自行展开常用宏集合：
 * 项目级 $ProjectFileDir$ / $ProjectName$；文件级 $FileDir$ / $FileName$ /
 * $FileNameWithoutExtension$ / $FileExtension$（基于脚本路径派生）；
 * 系统级 $USER_HOME$ / $USER_NAME$。未识别的宏原样保留。
 */
public final class ScriptCommandLineBuilder {

    private ScriptCommandLineBuilder() {
    }

    /**
     * 展开项目级与系统级宏（不含文件级宏，用于脚本路径与空配置命令行自身）。
     */
    public static @NotNull String expand(@NotNull Project project, @NotNull String text) {
        return expand(project, text, null);
    }

    /**
     * 展开全部宏；提供脚本路径时，文件级宏（$FileDir$ 等）基于该脚本派生。
     */
    public static @NotNull String expand(@NotNull Project project, @NotNull String text,
                                         @Nullable String scriptPath) {
        String baseDir = project.getBasePath();
        return expandMacros(text, baseDir, project.getName(),
                scriptPath == null || scriptPath.isBlank() ? null : scriptPath);
    }

    static @NotNull String expandMacros(@NotNull String text, @Nullable String projectDir,
                                        @Nullable String projectName, @Nullable String scriptPath) {
        String result = text;

        String projectFileDir = projectDir != null ? projectDir : "";
        result = result.replace("$ProjectFileDir$", projectFileDir);
        if (projectName != null) {
            result = result.replace("$ProjectName$", projectName);
        }
        result = result.replace("$USER_HOME$", System.getProperty("user.home", ""));
        result = result.replace("$USER_NAME$", System.getProperty("user.name", ""));

        String fileDir = projectDir != null ? projectDir : "";
        String fileName = "";
        String fileNameNoExt = "";
        String fileExt = "";
        if (scriptPath != null) {
            String normalized = scriptPath.replace('\\', '/');
            int slash = normalized.lastIndexOf('/');
            fileName = slash >= 0 ? normalized.substring(slash + 1) : normalized;
            fileDir = slash >= 0 ? normalized.substring(0, slash) : "";
            int dot = fileName.lastIndexOf('.');
            fileNameNoExt = dot > 0 ? fileName.substring(0, dot) : fileName;
            fileExt = dot > 0 ? fileName.substring(dot + 1) : "";
        }
        result = result.replace("$FileDir$", fileDir);
        result = result.replace("$FileName$", fileName);
        result = result.replace("$FileNameWithoutExtension$", fileNameNoExt);
        result = result.replace("$FileExtension$", fileExt);

        return FileUtil.toSystemDependentName(result);
    }
}
