package com.winscript.support.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComboBox
import javax.swing.JComponent

/**
 * 插件全局设置页：解释器路径与控制台编码。
 */
class WinScriptConfigurable : SearchableConfigurable {

    private val settings = WinScriptSettings.getInstance()

    private val batchInterpreterField = JBTextField()
    private val powershellInterpreterField = JBTextField()
    private val consoleEncodingField = JBTextField()
    private val executionPolicyBox = JComboBox(arrayOf("", "Bypass", "RemoteSigned"))

    private var panel: JComponent? = null

    override fun getId(): String = "com.winscript.support.settings"

    override fun getDisplayName(): String = "WinScript Support"

    override fun createComponent(): JComponent {
        panel = FormBuilder.createFormBuilder()
            .addLabeledComponent("Batch interpreter (cmd.exe):", batchInterpreterField)
            .addTooltip("Leave empty to use %ComSpec% (usually C:\\Windows\\System32\\cmd.exe)")
            .addLabeledComponent("PowerShell interpreter:", powershellInterpreterField)
            .addTooltip("Leave empty to auto-detect: pwsh.exe on PATH first, then powershell.exe")
            .addLabeledComponent("Console encoding:", consoleEncodingField)
            .addTooltip("e.g. UTF-8 or GBK; leave empty for language default (cmd = system code page, pwsh = UTF-8)")
            .addLabeledComponent("Default PowerShell execution policy:", executionPolicyBox)
            .addTooltip("Applied when a run configuration leaves its policy empty; empty = do not interfere")
            .panel
        return panel!!
    }

    override fun isModified(): Boolean =
            orEmpty(settings.batchInterpreter) != batchInterpreterField.text
                    || orEmpty(settings.powerShellInterpreter) != powershellInterpreterField.text
                    || orEmpty(settings.consoleEncoding) != consoleEncodingField.text
                    || selectedPolicy() != orEmpty(settings.defaultExecutionPolicy)

    override fun reset() {
        batchInterpreterField.text = orEmpty(settings.batchInterpreter)
        powershellInterpreterField.text = orEmpty(settings.powerShellInterpreter)
        consoleEncodingField.text = orEmpty(settings.consoleEncoding)
        executionPolicyBox.selectedItem = orEmpty(settings.defaultExecutionPolicy)
    }

    override fun apply() {
        settings.batchInterpreter = batchInterpreterField.text
        settings.powerShellInterpreter = powershellInterpreterField.text
        settings.consoleEncoding = consoleEncodingField.text
        settings.defaultExecutionPolicy = selectedPolicy()
    }

    private fun selectedPolicy(): String = (executionPolicyBox.selectedItem as? String) ?: ""

    private fun orEmpty(value: String?): String = value ?: ""
}
