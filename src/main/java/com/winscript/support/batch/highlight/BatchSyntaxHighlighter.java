package com.winscript.support.batch.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.batch.lexer.BatchLexer;
import com.winscript.support.batch.lexer.BatchTokens;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Batch 语法高亮器：Token 到颜色键的映射。
 */
public final class BatchSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Map<IElementType, TextAttributesKey> TOKEN_MAP = Map.of(
            BatchTokens.COMMENT, BatchHighlighterColors.COMMENT,
            BatchTokens.KEYWORD, BatchHighlighterColors.KEYWORD,
            BatchTokens.VARIABLE, BatchHighlighterColors.VARIABLE,
            BatchTokens.STRING, BatchHighlighterColors.STRING,
            BatchTokens.LABEL, BatchHighlighterColors.LABEL,
            BatchTokens.ESCAPE, BatchHighlighterColors.ESCAPE,
            BatchTokens.OPERATOR, BatchHighlighterColors.OPERATOR,
            BatchTokens.BAD_CHARACTER, BatchHighlighterColors.BAD_CHARACTER);

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new BatchLexer();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(TOKEN_MAP.get(tokenType));
    }
}
