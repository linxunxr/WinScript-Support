package com.winscript.support.powershell.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.powershell.lexer.PowerShellLexer;
import com.winscript.support.powershell.lexer.PowerShellTokens;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * PowerShell 语法高亮器：Token 到颜色键的映射。
 */
public final class PowerShellSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> TOKEN_MAP = Map.of(
            PowerShellTokens.COMMENT, PowerShellHighlighterColors.COMMENT,
            PowerShellTokens.KEYWORD, PowerShellHighlighterColors.KEYWORD,
            PowerShellTokens.VARIABLE, PowerShellHighlighterColors.VARIABLE,
            PowerShellTokens.STRING, PowerShellHighlighterColors.STRING,
            PowerShellTokens.OPERATOR, PowerShellHighlighterColors.OPERATOR,
            PowerShellTokens.BAD_CHARACTER, PowerShellHighlighterColors.BAD_CHARACTER);

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new PowerShellLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(TOKEN_MAP.get(tokenType));
    }
}
