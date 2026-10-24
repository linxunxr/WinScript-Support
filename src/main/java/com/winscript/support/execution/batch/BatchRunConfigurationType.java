package com.winscript.support.execution.batch;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Batch 运行配置类型（winscript.batch）。
 */
public final class BatchRunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "winscript.batch";

    public BatchRunConfigurationType() {
        super(ID, "Batch Script", "Run a Windows Batch script (.bat / .cmd)", AllIcons.FileTypes.Text);
        addFactory(new BatchConfigurationFactory(this));
    }

    public static final class BatchConfigurationFactory extends ConfigurationFactory {

        BatchConfigurationFactory(BatchRunConfigurationType type) {
            super(type);
        }

        @Override
        public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
            return new BatchRunConfiguration(project, this, "Batch Script");
        }
    }
}
