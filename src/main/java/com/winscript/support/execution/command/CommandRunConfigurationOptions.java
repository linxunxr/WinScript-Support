package com.winscript.support.execution.command;

import com.winscript.support.execution.common.ScriptRunConfigurationOptions;

/**
 * 空运行配置的持久化字段：完整命令行 + 工作目录（scriptPath 字段闲置不用）。
 */
public class CommandRunConfigurationOptions extends ScriptRunConfigurationOptions {

    private String commandLine = "";

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String value) {
        commandLine = value == null ? "" : value;
    }
}
