package com.winscript.support.batch.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.batch.BatchLanguage;

/**
 * Batch 词法 Token 集（v1.0 最小集）。
 */
public final class BatchTokens {

    public static final IElementType WHITE_SPACE = new IElementType("BATCH_WHITE_SPACE", BatchLanguage.INSTANCE);
    public static final IElementType COMMENT = new IElementType("BATCH_COMMENT", BatchLanguage.INSTANCE);
    public static final IElementType KEYWORD = new IElementType("BATCH_KEYWORD", BatchLanguage.INSTANCE);
    public static final IElementType VARIABLE = new IElementType("BATCH_VARIABLE", BatchLanguage.INSTANCE);
    public static final IElementType STRING = new IElementType("BATCH_STRING", BatchLanguage.INSTANCE);
    public static final IElementType LABEL = new IElementType("BATCH_LABEL", BatchLanguage.INSTANCE);
    public static final IElementType ESCAPE = new IElementType("BATCH_ESCAPE", BatchLanguage.INSTANCE);
    public static final IElementType OPERATOR = new IElementType("BATCH_OPERATOR", BatchLanguage.INSTANCE);
    public static final IElementType WORD = new IElementType("BATCH_WORD", BatchLanguage.INSTANCE);

    /** 未闭合 % / 括号等词法错误统一落到 TokenType.BAD_CHARACTER。 */
    public static final IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

    private BatchTokens() {
    }
}
