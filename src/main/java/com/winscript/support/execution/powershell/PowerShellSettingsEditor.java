package com.winscript.support.execution.powershell;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import com.winscript.support.powershell.PowerShellFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * PowerShell 运行配置编辑器：脚本路径、参数、工作目录（带浏览按钮）、执行策略。
 */
public final class PowerShellSettingsEditor extends SettingsEditor<PowerShellRunConfiguration> {

    private static final String[] EXECUTION_POLICIES = {"", "Bypass", "RemoteSigned"};

    private final JTextField scriptPathField = new JTextField();
    private final JTextField parametersField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JComboBox<String> executionPolicyBox = new JComboBox<>(EXECUTION_POLICIES);
    private final JPanel rootPanel;

    public PowerShellSettingsEditor(Project project) {
        JPanel scriptPanel = pathWithBrowse(scriptPathField, project, "Select PowerShell Script", false);
        JPanel workDirPanel = pathWithBrowse(workingDirectoryField, project, "Select Working Directory", true);
        executionPolicyBox.setSelectedIndex(0);
        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Script path:", scriptPanel)
                .addLabeledComponent("Script parameters:", parametersField)
                .addTooltip("Passed to the script; supports $ProjectFileDir$ / $FileDir$ macros")
                .addLabeledComponent("Working directory:", workDirPanel)
                .addTooltip("Empty = directory of the script")
                .addLabeledComponent("Execution policy (empty = system default):", executionPolicyBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private static JPanel pathWithBrowse(JTextField field, Project project, String title, boolean directoryOnly) {
        JPanel panel = new JPanel(new java.awt.BorderLayout(4, 0));
        JButton browseButton = new JButton("...");
        browseButton.addActionListener(e -> {
            VirtualFile chosen = FileChooser.chooseFile(
                    directoryOnly
                            ? FileChooserDescriptorFactory.createSingleFolderDescriptor()
                            : FileChooserDescriptorFactory.createSingleFileDescriptor(PowerShellFileType.INSTANCE),
                    project, null);
            if (chosen != null) {
                field.setText(chosen.getPath());
            }
        });
        panel.add(field, java.awt.BorderLayout.CENTER);
        panel.add(browseButton, java.awt.BorderLayout.EAST);
        return panel;
    }

    @Override
    protected void resetEditorFrom(PowerShellRunConfiguration configuration) {
        scriptPathField.setText(configuration.getScriptPath());
        parametersField.setText(configuration.getScriptParameters());
        workingDirectoryField.setText(configuration.getWorkingDirectory());
        String policy = configuration.getExecutionPolicy();
        executionPolicyBox.setSelectedItem(policy == null ? "" : policy);
    }

    @Override
    protected void applyEditorTo(PowerShellRunConfiguration configuration) {
        configuration.setScriptPath(scriptPathField.getText().trim());
        configuration.setScriptParameters(parametersField.getText());
        configuration.setWorkingDirectory(workingDirectoryField.getText().trim());
        Object selected = executionPolicyBox.getSelectedItem();
        configuration.setExecutionPolicy(selected == null ? "" : selected.toString());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return rootPanel;
    }
}
