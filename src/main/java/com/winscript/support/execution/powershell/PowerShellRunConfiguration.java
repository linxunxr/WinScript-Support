package com.winscript.support.execution.powershell;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.winscript.support.execution.common.ScriptCommandLineBuilder;
import com.winscript.support.execution.common.ScriptRunConfigurationBase;
import com.winscript.support.settings.WinScriptSettings;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * PowerShell 运行配置：pwsh -NoLogo -NoProfile [-ExecutionPolicy X] -File <script> <args>。
 * 解释器由 {@link PowerShellInterpreterDetector} 决定（pwsh 优先，回退 powershell.exe）。
 */
public final class PowerShellRunConfiguration
        extends ScriptRunConfigurationBase<PowerShellRunConfigurationOptions> {

    PowerShellRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    public String getExecutionPolicy() {
        return getOptions().getExecutionPolicy();
    }

    public void setExecutionPolicy(String value) {
        getOptions().setExecutionPolicy(value);
    }

    @Override
    protected @NotNull GeneralCommandLine buildCommandLine() throws ExecutionException {
        String scriptPath = ScriptCommandLineBuilder.expand(getProject(), getScriptPath());
        File script = new File(scriptPath);
        if (!script.isFile()) {
            throw new ExecutionException("PowerShell script not found: " + scriptPath);
        }

        GeneralCommandLine commandLine = new GeneralCommandLine(PowerShellInterpreterDetector.detect());
        commandLine.addParameter("-NoLogo");
        commandLine.addParameter("-NoProfile");
        String policy = getExecutionPolicy();
        if (policy != null && !policy.isBlank()) {
            commandLine.addParameter("-ExecutionPolicy");
            commandLine.addParameter(policy);
        }
        commandLine.addParameter("-File");
        commandLine.addParameter(script.getAbsolutePath());
        List<String> parameters = ParametersListUtil.parse(
                ScriptCommandLineBuilder.expand(getProject(), getScriptParameters(), scriptPath));
        for (String parameter : parameters) {
            commandLine.addParameter(parameter);
        }

        String workingDirectory = getWorkingDirectory();
        if (workingDirectory != null && !workingDirectory.isBlank()) {
            commandLine.setWorkDirectory(new File(
                    ScriptCommandLineBuilder.expand(getProject(), workingDirectory, scriptPath)));
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
    protected @NotNull SettingsEditor<? extends ScriptRunConfigurationBase<PowerShellRunConfigurationOptions>> createSettingsEditor() {
        return new PowerShellSettingsEditor(getProject());
    }

    @Override
    public void writeExternal(@NotNull Element element) {
        super.writeExternal(element);
        element.setAttribute("executionPolicy", getExecutionPolicy());
    }

    @Override
    public void readExternal(@NotNull Element element) {
        super.readExternal(element);
        String policy = element.getAttributeValue("executionPolicy");
        setExecutionPolicy(policy == null ? "" : policy);
    }
}
