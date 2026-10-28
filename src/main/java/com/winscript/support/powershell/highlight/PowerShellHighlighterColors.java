package com.winscript.support.powershell.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;

/**
 * PowerShell 高亮色键，全部挂接 IDE 默认主题的配色（不覆盖用户自定义）。
 */
public final class PowerShellHighlighterColors {

    public static final TextAttributesKey COMMENT = TextAttributesKey.createTextAttributesKey(
            "PS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    public static final TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey(
            "PS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);

    public static final TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey(
            "PS_VARIABLE", DefaultLanguageHighlighterColors.STATIC_FIELD);

    public static final TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey(
            "PS_STRING", DefaultLanguageHighlighterColors.STRING);

    public static final TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey(
            "PS_NUMBER", DefaultLanguageHighlighterColors.NUMBER);

    public static final TextAttributesKey TYPE = TextAttributesKey.createTextAttributesKey(
            "PS_TYPE", DefaultLanguageHighlighterColors.METADATA);

    public static final TextAttributesKey ESCAPE = TextAttributesKey.createTextAttributesKey(
            "PS_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);

    public static final TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey(
            "PS_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    public static final TextAttributesKey BAD_CHARACTER = TextAttributesKey.createTextAttributesKey(
            "PS_BAD_CHARACTER");

    private PowerShellHighlighterColors() {
    }
}
