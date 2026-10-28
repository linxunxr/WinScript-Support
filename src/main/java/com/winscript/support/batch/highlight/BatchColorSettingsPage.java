package com.winscript.support.batch.highlight;

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
 * Batch 配色设置页：Settings → Editor → Color Scheme → Batch。
 */
public final class BatchColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = {
            new AttributesDescriptor("Comment", BatchHighlighterColors.COMMENT),
            new AttributesDescriptor("Keyword", BatchHighlighterColors.KEYWORD),
            new AttributesDescriptor("Variable", BatchHighlighterColors.VARIABLE),
            new AttributesDescriptor("String", BatchHighlighterColors.STRING),
            new AttributesDescriptor("Label", BatchHighlighterColors.LABEL),
            new AttributesDescriptor("Escape character", BatchHighlighterColors.ESCAPE),
            new AttributesDescriptor("Operator", BatchHighlighterColors.OPERATOR),
            new AttributesDescriptor("Bad character", BatchHighlighterColors.BAD_CHARACTER),
    };

    private static final Map<String, TextAttributesKey> TAGS = new HashMap<>();

    static {
        TAGS.put("comment", BatchHighlighterColors.COMMENT);
        TAGS.put("keyword", BatchHighlighterColors.KEYWORD);
        TAGS.put("variable", BatchHighlighterColors.VARIABLE);
        TAGS.put("string", BatchHighlighterColors.STRING);
        TAGS.put("label", BatchHighlighterColors.LABEL);
        TAGS.put("escape", BatchHighlighterColors.ESCAPE);
        TAGS.put("operator", BatchHighlighterColors.OPERATOR);
        TAGS.put("bad", BatchHighlighterColors.BAD_CHARACTER);
    }

    private static final String DEMO_TEXT = String.join("\n",
            "<comment>:: Batch color scheme demo</comment>",
            "<keyword>set</keyword> \"NAME=<variable>%~dp0</variable>\"",
            "<keyword>@echo</keyword> <string>\"hello\"</string> <variable>%NAME%</variable>",
            "<keyword>if</keyword> <variable>%1</variable>==<string>\"\"</string> <keyword>goto</keyword> <label>:usage</label>",
            "<keyword>echo</keyword> escaped <escape>^&amp;</escape> pipe <operator>|</operator> done",
            "<comment>rem unclosed variable follows</comment>",
            "<bad>%UNCLOSED</bad>");

    @Override
    public @NotNull String getDisplayName() {
        return "Batch";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new BatchSyntaxHighlighter();
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
