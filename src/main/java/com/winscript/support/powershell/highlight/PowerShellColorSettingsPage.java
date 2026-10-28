package com.winscript.support.powershell.highlight;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * PowerShell 配色设置页：Settings → Editor → Color Scheme → PowerShell。
 */
public final class PowerShellColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = {
            new AttributesDescriptor("Comment", PowerShellHighlighterColors.COMMENT),
            new AttributesDescriptor("Keyword", PowerShellHighlighterColors.KEYWORD),
            new AttributesDescriptor("Variable", PowerShellHighlighterColors.VARIABLE),
            new AttributesDescriptor("String", PowerShellHighlighterColors.STRING),
            new AttributesDescriptor("Number", PowerShellHighlighterColors.NUMBER),
            new AttributesDescriptor("Type expression", PowerShellHighlighterColors.TYPE),
            new AttributesDescriptor("Escape character", PowerShellHighlighterColors.ESCAPE),
            new AttributesDescriptor("Operator", PowerShellHighlighterColors.OPERATOR),
            new AttributesDescriptor("Bad character", PowerShellHighlighterColors.BAD_CHARACTER),
    };

    private static final Map<String, TextAttributesKey> TAGS = new HashMap<>();

    static {
        TAGS.put("comment", PowerShellHighlighterColors.COMMENT);
        TAGS.put("keyword", PowerShellHighlighterColors.KEYWORD);
        TAGS.put("variable", PowerShellHighlighterColors.VARIABLE);
        TAGS.put("string", PowerShellHighlighterColors.STRING);
        TAGS.put("number", PowerShellHighlighterColors.NUMBER);
        TAGS.put("type", PowerShellHighlighterColors.TYPE);
        TAGS.put("escape", PowerShellHighlighterColors.ESCAPE);
        TAGS.put("operator", PowerShellHighlighterColors.OPERATOR);
        TAGS.put("bad", PowerShellHighlighterColors.BAD_CHARACTER);
    }

    private static final String DEMO_TEXT = String.join("\n",
            "<comment># PowerShell color scheme demo</comment>",
            "<keyword>function</keyword> Get-Greeting(<variable>$param</variable>) {",
            "  <type>[string]</type><variable>$env:PATH</variable> <operator>|</operator> Out-Null",
            "  <variable>$size</variable> = <number>1GB</number> <operator>-gt</operator> <number>0x1F</number>",
            "  <keyword>return</keyword> <string>\"hello `<escape>n</escape>$param\"</string>",
            "}",
            "<comment>&lt;# block comment #&gt;</comment>",
            "<variable>$items</variable> <operator>-match</operator> <string>'pattern'</string>");

    @Override
    public @NotNull String getDisplayName() {
        return "PowerShell";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new PowerShellSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return DEMO_TEXT;
    }

    @Override
    public @NotNull Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }
}
