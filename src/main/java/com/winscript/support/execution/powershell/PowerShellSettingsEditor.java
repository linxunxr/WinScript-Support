package com.winscript.support.execution.powershell;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * PowerShell 运行配置编辑器：脚本路径、参数、工作目录、执行策略。
 * 浏览按钮组件待 M2 里程碑接入（2025.3 平台已移除 TextFieldWithBrowseButton 公开 API）。
 */
public final class PowerShellSettingsEditor extends SettingsEditor<PowerShellRunConfiguration> {

    private static final String[] EXECUTION_POLICIES = {"", "Bypass", "RemoteSigned"};

    private final JTextField scriptPathField = new JTextField();
    private final JTextField parametersField = new JTextField();
    private final JTextField workingDirectoryField = new JTextField();
    private final JComboBox<String> executionPolicyBox = new JComboBox<>(EXECUTION_POLICIES);
    private final JPanel rootPanel;

    public PowerShellSettingsEditor() {
        executionPolicyBox.setSelectedIndex(0);
        rootPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent("Script path:", scriptPathField)
                .addLabeledComponent("Script parameters:", parametersField)
                .addLabeledComponent("Working directory:", workingDirectoryField)
                .addLabeledComponent("Execution policy (empty = system default):", executionPolicyBox)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
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
