package com.winscript.support.powershell.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.powershell.lexer.PowerShellLexer;
import com.winscript.support.powershell.lexer.PowerShellTokens;
import org.jetbrains.annotations.NotNull;

/**
 * PowerShell 轻量错误标注：未闭合块注释 / here-string / 行内字符串。
 * 无 Parser/PSI，Annotator 在文件元素上自行跑 Lexer 全文扫描。
 */
public final class PowerShellErrorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiFile)) {
            return;
        }
        PsiFile file = (PsiFile) element;
        String text = file.getText();
        int baseOffset = file.getTextRange().getStartOffset();

        Lexer lexer = new PowerShellLexer();
        lexer.start(text, 0, text.length(), 0);
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            int start = baseOffset + lexer.getTokenStart();
            int end = baseOffset + lexer.getTokenEnd();
            String tokenText = lexer.getTokenText();

            if (tokenType == PowerShellTokens.BAD_CHARACTER) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unexpected character")
                        .range(new TextRange(start, end)).create();
            } else if (tokenType == PowerShellTokens.COMMENT && isUnclosedComment(tokenText)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unclosed block comment")
                        .range(new TextRange(start, end)).create();
            } else if (tokenType == PowerShellTokens.STRING && isUnclosedHereString(tokenText)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unclosed here-string")
                        .range(new TextRange(start, end)).create();
            } else if (tokenType == PowerShellTokens.STRING && isUnclosedInlineString(tokenText)) {
                holder.newAnnotation(HighlightSeverity.WARNING, "Unclosed string")
                        .range(new TextRange(start, end)).create();
            }
            lexer.advance();
        }
    }

    static boolean isUnclosedComment(String tokenText) {
        return tokenText.startsWith("<#") && !tokenText.endsWith("#>");
    }

    static boolean isUnclosedHereString(String tokenText) {
        if (tokenText.startsWith("@\"")) {
            return !tokenText.endsWith("\"@");
        }
        if (tokenText.startsWith("@'")) {
            return !tokenText.endsWith("'@");
        }
        return false;
    }

    static boolean isUnclosedInlineString(String tokenText) {
        if (tokenText.length() < 1 || tokenText.startsWith("@")) {
            return false;
        }
        char quote = tokenText.charAt(0);
        if (quote != '"' && quote != '\'') {
            return false;
        }
        // 仅剩开引号（单字符）或结尾不是闭合引号
        return tokenText.length() < 2 || tokenText.charAt(tokenText.length() - 1) != quote;
    }
}
