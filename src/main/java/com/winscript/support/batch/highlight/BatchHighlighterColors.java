package com.winscript.support.batch.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * Batch 高亮色键，全部挂接 IDE 默认主题的配色（不覆盖用户自定义）。
 */
public final class BatchHighlighterColors {

    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "BATCH_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
            "BATCH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(
            "BATCH_VARIABLE", DefaultLanguageHighlighterColors.STATIC_FIELD);

    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
            "BATCH_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey LABEL = TextAttributesKey.createTextAttributesKey(
            "BATCH_LABEL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);

    public static final TextAttributesKey ESCAPE = TextAttributesKey.createTextAttributesKey(
            "BATCH_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);

    public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey(
            "BATCH_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "BATCH_BAD_CHARACTER");

    private BatchHighlighterColors() {
    }
}
