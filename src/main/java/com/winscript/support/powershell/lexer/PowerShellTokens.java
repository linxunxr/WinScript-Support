package com.winscript.support.powershell.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.powershell.PowerShellLanguage;

/**
 * PowerShell 词法 Token 集（v1.0 最小集）。
 */
public final class PowerShellTokens {

    public static final IElementType WHITE_SPACE = new IElementType("PS_WHITE_SPACE", PowerShellLanguage.INSTANCE);
    public static final IElementType COMMENT = new IElementType("PS_COMMENT", PowerShellLanguage.INSTANCE);
    public static final IElementType KEYWORD = new IElementType("PS_KEYWORD", PowerShellLanguage.INSTANCE);
    public static final IElementType VARIABLE = new IElementType("PS_VARIABLE", PowerShellLanguage.INSTANCE);
    public static final IElementType STRING = new IElementType("PS_STRING", PowerShellLanguage.INSTANCE);
    public static final IElementType OPERATOR = new IElementType("PS_OPERATOR", PowerShellLanguage.INSTANCE);
    public static final IElementType WORD = new IElementType("PS_WORD", PowerShellLanguage.INSTANCE);

    /** 词法错误统一落到 TokenType.BAD_CHARACTER。 */
    public static final IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

    private PowerShellTokens() {
    }
}
