package com.winscript.support.powershell.service;

/**
 * PowerShell 语言服务桥接接口（v1.x 预留）。
 * <p>
 * v1.0 该接口为 no-op：词法高亮由自研 Lexer 提供，不依赖任何外部服务。
 * v1.x 接入 PowerShell Editor Services（PSES）后，补全、诊断等能力通过
 * 本接口注入，无需改动 Lexer / 高亮层。
 */
public interface PowerShellLanguageServiceBridge {

    /** v1.0 使用的空实现。 */
    PowerShellLanguageServiceBridge NOOP = new PowerShellLanguageServiceBridge() {
    };

    /**
     * 语言服务（PSES 等）是否可用。
     */
    default boolean isAvailable() {
        return false;
    }
}
