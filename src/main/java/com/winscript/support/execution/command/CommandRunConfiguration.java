package com.winscript.support.execution.command;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import com.winscript.support.execution.common.ScriptCommandLineBuilder;
import com.winscript.support.execution.common.ScriptRunConfigurationBase;
import com.winscript.support.execution.common.ShellPathResolver;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 空运行配置：不绑定脚本文件，执行任意命令行（cmd.exe /c <command>），
 * 用于调用外部工具或临时命令。工作目录默认项目根目录。
 */
public final class CommandRunConfiguration extends ScriptRunConfigurationBase<CommandRunConfigurationOptions> {

    CommandRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected @NotNull Class<CommandRunConfigurationOptions> getOptionsClass() {
        return CommandRunConfigurationOptions.class;
    }

    public String getCommandLine() {
        return getOptions().getCommandLine();
    }

    public void setCommandLine(String value) {
        getOptions().setCommandLine(value);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (getCommandLine().isBlank()) {
            throw new RuntimeConfigurationError("Command line is not set");
        }
    }

    @Override
    protected @NotNull GeneralCommandLine buildCommandLine() throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine(ShellPathResolver.resolveCmdExe());
        commandLine.addParameter("/c");
        List<String> parts = ParametersListUtil.parse(
                ScriptCommandLineBuilder.expand(getProject(), getCommandLine()));
        if (parts.isEmpty()) {
            throw new ExecutionException("Command line is not set");
        }
        for (String part : parts) {
            commandLine.addParameter(part);
        }

        String workingDirectory = getWorkingDirectory();
        if (workingDirectory != null && !workingDirectory.isBlank()) {
            commandLine.setWorkDirectory(new File(ScriptCommandLineBuilder.expand(getProject(), workingDirectory)));
        } else {
            String basePath = getProject().getBasePath();
            if (basePath != null) {
                commandLine.setWorkDirectory(new File(basePath));
            }
        }

        Charset charset = resolveConsoleCharset();
        commandLine.setCharset(charset);
        return commandLine;
    }

    /**
     * 与 cmd.exe 一致：系统控制台代码页。
     */
    @Override
    protected Charset defaultConsoleCharset() {
        return nativeConsoleCharset();
    }

    @Override
    protected @NotNull com.intellij.openapi.options.SettingsEditor<? extends ScriptRunConfigurationBase<CommandRunConfigurationOptions>> createSettingsEditor() {
        return new CommandSettingsEditor(getProject());
    }

    @Override
    public void writeExternal(@NotNull org.jdom.Element element) {
        super.writeExternal(element);
        element.setAttribute("commandLine", getCommandLine());
    }

    @Override
    public void readExternal(@NotNull org.jdom.Element element) {
        super.readExternal(element);
        String command = element.getAttributeValue("commandLine");
        setCommandLine(command == null ? "" : command);
    }
}
