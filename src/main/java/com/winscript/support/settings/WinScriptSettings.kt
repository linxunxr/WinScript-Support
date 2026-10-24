package com.winscript.support.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

/**
 * 插件全局设置（应用级，持久化到 winscript-support.xml）。
 * 解释器路径为空时走自动探测（ComSpec / PATH）。
 */
@Service(Service.Level.APP)
@State(name = "WinScriptSettings", storages = [Storage("winscript-support.xml")])
class WinScriptSettings : PersistentStateComponent<WinScriptSettings.State> {

    class State {
        var batchInterpreter: String? = null
        var powerShellInterpreter: String? = null
        var consoleEncoding: String? = null
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(loaded: State) {
        myState = loaded
    }

    /** Batch 解释器覆盖路径，未配置为 null。 */
    var batchInterpreter: String?
        get() = myState.batchInterpreter
        set(value) {
            myState.batchInterpreter = value?.trim()?.takeIf { it.isNotEmpty() }
        }

    /** PowerShell 解释器覆盖路径，未配置为 null。 */
    var powerShellInterpreter: String?
        get() = myState.powerShellInterpreter
        set(value) {
            myState.powerShellInterpreter = value?.trim()?.takeIf { it.isNotEmpty() }
        }

    /** 控制台编码（如 UTF-8 / GBK），未配置为 null（跟随系统）。 */
    var consoleEncoding: String?
        get() = myState.consoleEncoding
        set(value) {
            myState.consoleEncoding = value?.trim()?.takeIf { it.isNotEmpty() }
        }

    companion object {
        @JvmStatic
        fun getInstance(): WinScriptSettings = service()
    }
}
