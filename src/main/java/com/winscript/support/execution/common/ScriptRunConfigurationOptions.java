package com.winscript.support.execution.common;

import com.intellij.execution.configurations.RunConfigurationOptions;

/**
 * 脚本类运行配置的持久化字段：脚本路径、参数、工作目录。
 * 持久化由 {@code ScriptRunConfigurationBase#writeExternal/readExternal} 手写完成。
 */
public class ScriptRunConfigurationOptions extends RunConfigurationOptions {

    private String scriptPath = "";
    private String scriptParameters = "";
    private String workingDirectory = "";

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String value) {
        scriptPath = value == null ? "" : value;
    }

    public String getScriptParameters() {
        return scriptParameters;
    }

    public void setScriptParameters(String value) {
        scriptParameters = value == null ? "" : value;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String value) {
        workingDirectory = value == null ? "" : value;
    }
}
