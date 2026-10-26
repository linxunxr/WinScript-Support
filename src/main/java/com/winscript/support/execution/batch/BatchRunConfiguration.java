package com.winscript.support.execution.batch;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.openapi.project.Project;
import com.winscript.support.execution.common.ScriptCommandLineBuilder;
import com.winscript.support.execution.common.ScriptRunConfigurationBase;
import com.winscript.support.execution.common.ScriptRunConfigurationOptions;
import com.winscript.support.execution.common.ShellPathResolver;
import com.winscript.support.settings.WinScriptSettings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Batch 运行配置：cmd.exe /c call <script> <args>。
 * 工作目录默认脚本所在目录，控制台编码取全局设置（未配置则跟随系统）。
 */
public final class BatchRunConfiguration extends ScriptRunConfigurationBase<ScriptRunConfigurationOptions> {

    BatchRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected @NotNull GeneralCommandLine buildCommandLine() throws ExecutionException {
        String scriptPath = ScriptCommandLineBuilder.expand(getProject(), getScriptPath());
        File script = new File(scriptPath);
        if (!script.isFile()) {
            throw new ExecutionException("Batch script not found: " + scriptPath);
        }

        GeneralCommandLine commandLine = new GeneralCommandLine();
        commandLine.setExePath(ShellPathResolver.resolveCmdExe());
        commandLine.addParameter("/c");
        commandLine.addParameter("call");
        commandLine.addParameter(script.getAbsolutePath());
        List<String> parameters = ParametersListUtil.parse(getScriptParameters());
        for (String parameter : parameters) {
            commandLine.addParameter(parameter);
        }

        String workingDirectory = getWorkingDirectory();
        if (workingDirectory != null && !workingDirectory.isBlank()) {
            commandLine.setWorkDirectory(new File(ScriptCommandLineBuilder.expand(getProject(), workingDirectory)));
        } else {
            commandLine.setWorkDirectory(script.getParentFile());
        }

        Charset charset = resolveConsoleCharset();
        if (charset != null) {
            commandLine.setCharset(charset);
        }
        return commandLine;
    }

    @Override
    protected @NotNull com.intellij.openapi.options.SettingsEditor<? extends ScriptRunConfigurationBase<ScriptRunConfigurationOptions>> createSettingsEditor() {
        return new BatchSettingsEditor();
    }
}
