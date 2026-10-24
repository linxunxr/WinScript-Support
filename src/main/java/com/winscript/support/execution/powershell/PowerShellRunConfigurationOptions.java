package com.winscript.support.execution.powershell;

import com.winscript.support.execution.common.ScriptRunConfigurationOptions;

/**
 * PowerShell 运行配置的持久化字段：公共字段 + 执行策略。
 */
public class PowerShellRunConfigurationOptions extends ScriptRunConfigurationOptions {

    private String executionPolicy = "";

    /** 空串表示不干预（尊重系统策略）。 */
    public String getExecutionPolicy() {
        return executionPolicy;
    }

    public void setExecutionPolicy(String value) {
        executionPolicy = value == null ? "" : value;
    }
}
