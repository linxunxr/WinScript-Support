package com.winscript.support.execution.command;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * 空运行配置类型（winscript.command）：执行任意命令行。
 */
public final class CommandRunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "winscript.command";

    public CommandRunConfigurationType() {
        super(ID, "Shell Command", "Run an arbitrary shell command", AllIcons.Nodes.Console);
        addFactory(new CommandConfigurationFactory(this));
    }

    public static CommandRunConfigurationType getInstance() {
        return ConfigurationType.CONFIGURATION_TYPE_EP.findExtensionOrFail(CommandRunConfigurationType.class);
    }

    public static final class CommandConfigurationFactory extends ConfigurationFactory {

        CommandConfigurationFactory(CommandRunConfigurationType type) {
            super(type);
        }

        @Override
        public @NotNull String getId() {
            return "winscript.command.factory";
        }

        @Override
        public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new CommandRunConfiguration(project, this, "Shell Command");
        }
    }
}
