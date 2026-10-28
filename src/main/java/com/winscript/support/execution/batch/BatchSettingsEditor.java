package com.winscript.support.execution.batch;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.FormBuilder;
import com.winscript.support.batch.BatchFileType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Batch 运行配置编辑器：脚本路径、参数、工作目录（带文件浏览按钮）。
 */
public final class BatchSettingsEditor extends SettingsEditor<BatchRunConfiguration> {

    private final JTextField scriptPathField = new JTextField();
    private final JTextField parametersField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JPanel rootPanel;

    public BatchSettingsEditor(Project project) {
        JPanel scriptPanel = pathWithBrowse(scriptPathField, project, "Select Batch Script", false);
        JPanel workDirPanel = pathWithBrowse(workingDirectoryField, project, "Select Working Directory", true);
        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Script path:", scriptPanel)
                .addLabeledComponent("Script parameters:", parametersField)
                .addTooltip("Passed to the script; supports $ProjectFileDir$ / $FileDir$ macros")
                .addLabeledComponent("Working directory:", workDirPanel)
                .addTooltip("Empty = directory of the script")
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
                            : FileChooserDescriptorFactory.createSingleFileDescriptor(BatchFileType.INSTANCE),
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
    protected void resetEditorFrom(BatchRunConfiguration configuration) {
        scriptPathField.setText(configuration.getScriptPath());
        parametersField.setText(configuration.getScriptParameters());
        workingDirectoryField.setText(configuration.getWorkingDirectory());
    }

    @Override
    protected void applyEditorTo(BatchRunConfiguration configuration) {
        configuration.setScriptPath(scriptPathField.getText().trim());
        configuration.setScriptParameters(parametersField.getText());
        configuration.setWorkingDirectory(workingDirectoryField.getText().trim());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return rootPanel;
    }
}
