package com.winscript.support.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

/**
 * 插件全局设置页：解释器路径与控制台编码。
 */
class WinScriptConfigurable : SearchableConfigurable {

    private val settings = WinScriptSettings.getInstance()

    private val batchInterpreterField = JBTextField()
    private val powershellInterpreterField = JBTextField()
    private val consoleEncodingField = JBTextField()

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
            .panel
        return panel!!
    }

    override fun isModified(): Boolean =
            orEmpty(settings.batchInterpreter) != batchInterpreterField.text
                    || orEmpty(settings.powerShellInterpreter) != powershellInterpreterField.text
                    || orEmpty(settings.consoleEncoding) != consoleEncodingField.text

    override fun reset() {
        batchInterpreterField.text = orEmpty(settings.batchInterpreter)
        powershellInterpreterField.text = orEmpty(settings.powerShellInterpreter)
        consoleEncodingField.text = orEmpty(settings.consoleEncoding)
    }

    override fun apply() {
        settings.batchInterpreter = batchInterpreterField.text
        settings.powerShellInterpreter = powershellInterpreterField.text
        settings.consoleEncoding = consoleEncodingField.text
    }

    private fun orEmpty(value: String?): String = value ?: ""
}
