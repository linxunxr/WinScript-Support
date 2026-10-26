package com.winscript.support.execution.common;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.process.KillableProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.winscript.support.settings.WinScriptSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;

/**
 * Batch / PowerShell 运行配置的公共骨架：字段存取、宏解析、进程启动路径、持久化。
 * 子类通过 Options 子类扩展语言特有字段（需同步覆写 readExternal/writeExternal），
 * 并实现 {@link #buildCommandLine()} 与 {@link #createSettingsEditor()}。
 */
public abstract class ScriptRunConfigurationBase<O extends ScriptRunConfigurationOptions>
        extends RunConfigurationBase<O> {

    protected ScriptRunConfigurationBase(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected @NotNull O getOptions() {
        @SuppressWarnings("unchecked")
        O options = (O) super.getOptions();
        return options;
    }

    public String getScriptPath() {
        return getOptions().getScriptPath();
    }

    public void setScriptPath(String value) {
        getOptions().setScriptPath(value);
    }

    public String getScriptParameters() {
        return getOptions().getScriptParameters();
    }

    public void setScriptParameters(String value) {
        getOptions().setScriptParameters(value);
    }

    public String getWorkingDirectory() {
        return getOptions().getWorkingDirectory();
    }

    public void setWorkingDirectory(String value) {
        getOptions().setWorkingDirectory(value);
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (getScriptPath().isBlank()) {
            throw new RuntimeConfigurationError("Script path is not set");
        }
    }

    /**
     * 控制台编码：取全局设置，未配置或非法时返回 null（跟随系统）。
     */
    protected final Charset resolveConsoleCharset() {
        String encoding = WinScriptSettings.getInstance().getConsoleEncoding();
        if (encoding == null || encoding.isBlank()) {
            return null;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return createSettingsEditor();
    }

    protected abstract @NotNull SettingsEditor<? extends ScriptRunConfigurationBase<O>> createSettingsEditor();

    /**
     * 构造待执行的完整命令行（含解释器、脚本与参数）。
     */
    protected abstract @NotNull GeneralCommandLine buildCommandLine() throws ExecutionException;

    @Override
    public @NotNull RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment)
            throws ExecutionException {
        return new CommandLineState(environment) {
            @Override
            protected @NotNull ProcessHandler startProcess() throws ExecutionException {
                return new KillableProcessHandler(buildCommandLine());
            }
        };
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        element.setAttribute("scriptPath", getScriptPath());
        element.setAttribute("scriptParameters", getScriptParameters());
        element.setAttribute("workingDirectory", getWorkingDirectory());
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        setScriptPath(notNullize(element.getAttributeValue("scriptPath")));
        setScriptParameters(notNullize(element.getAttributeValue("scriptParameters")));
        setWorkingDirectory(notNullize(element.getAttributeValue("workingDirectory")));
    }

    private static String notNullize(String value) {
        return value == null ? "" : value;
    }
}
