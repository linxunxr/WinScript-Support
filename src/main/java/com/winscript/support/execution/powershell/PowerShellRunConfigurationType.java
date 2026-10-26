package com.winscript.support.execution.powershell;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * PowerShell 运行配置类型（winscript.powershell）。
 */
public final class PowerShellRunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "winscript.powershell";

    public PowerShellRunConfigurationType() {
        super(ID, "PowerShell Script", "Run a PowerShell script (.ps1)", AllIcons.FileTypes.Text);
        addFactory(new PowerShellConfigurationFactory(this));
    }

    public static PowerShellRunConfigurationType getInstance() {
        return ConfigurationType.CONFIGURATION_TYPE_EP.findExtensionOrFail(PowerShellRunConfigurationType.class);
    }

    public static final class PowerShellConfigurationFactory extends ConfigurationFactory {

        PowerShellConfigurationFactory(PowerShellRunConfigurationType type) {
            super(type);
        }

        @Override
        public @NotNull String getId() {
            return "winscript.powershell.factory";
        }

        @Override
        public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new PowerShellRunConfiguration(project, this, "PowerShell Script");
        }
    }
}
