package com.winscript.support.execution.command;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 空运行配置编辑器：命令行 + 工作目录。
 */
public final class CommandSettingsEditor extends SettingsEditor<CommandRunConfiguration> {

    private final JTextField commandLineField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JPanel rootPanel;

    public CommandSettingsEditor() {
        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Command line:", commandLineField)
                .addTooltip("Executed via cmd.exe /c; supports $ProjectFileDir$ macros and quoting")
                .addLabeledComponent("Working directory:", workingDirectoryField)
                .addTooltip("Empty = project root")
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    @Override
    protected void resetEditorFrom(CommandRunConfiguration configuration) {
        commandLineField.setText(configuration.getCommandLine());
        workingDirectoryField.setText(configuration.getWorkingDirectory());
    }

    @Override
    protected void applyEditorTo(CommandRunConfiguration configuration) {
        configuration.setCommandLine(commandLineField.getText().trim());
        configuration.setWorkingDirectory(workingDirectoryField.getText().trim());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return rootPanel;
    }
}
