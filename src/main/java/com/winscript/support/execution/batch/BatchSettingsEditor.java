package com.winscript.support.execution.batch;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Batch 运行配置编辑器：脚本路径、参数、工作目录。
 * 浏览按钮组件待 M2 里程碑接入（2025.3 平台已移除 TextFieldWithBrowseButton 公开 API）。
 */
public final class BatchSettingsEditor extends SettingsEditor<BatchRunConfiguration> {

    private final JTextField scriptPathField = new JTextField();
    private final JTextField parametersField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JPanel rootPanel;

    public BatchSettingsEditor() {
        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Script path:", scriptPathField)
                .addLabeledComponent("Script parameters:", parametersField)
                .addLabeledComponent("Working directory:", workingDirectoryField)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
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
