package com.winscript.support.execution.command;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 空运行配置编辑器：命令行 + 工作目录（带浏览按钮）。
 */
public final class CommandSettingsEditor extends SettingsEditor<CommandRunConfiguration> {

    private final JTextField commandLineField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JPanel rootPanel;

    public CommandSettingsEditor(Project project) {
        JPanel workDirPanel = new JPanel(new java.awt.BorderLayout(4, 0));
        JButton browseButton = new JButton("...");
        browseButton.addActionListener(e -> {
            VirtualFile chosen = FileChooser.chooseFile(
                    FileChooserDescriptorFactory.createSingleFolderDescriptor(), project, null);
            if (chosen != null) {
                workingDirectoryField.setText(chosen.getPath());
            }
        });
        workDirPanel.add(workingDirectoryField, java.awt.BorderLayout.CENTER);
        workDirPanel.add(browseButton, java.awt.BorderLayout.EAST);

        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Command line:", commandLineField)
                .addTooltip("Executed via cmd.exe /c; supports $ProjectFileDir$ macros and quoting")
                .addLabeledComponent("Working directory:", workDirPanel)
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
