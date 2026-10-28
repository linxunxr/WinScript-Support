package com.winscript.support.batch.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.winscript.support.batch.lexer.BatchLexer;
import com.winscript.support.batch.lexer.BatchTokens;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Batch 轻量错误标注：未闭合 %（BAD_CHARACTER）、未闭合引号、未配对括号。
 * 无 Parser/PSI，Annotator 在文件元素上自行跑 Lexer 全文扫描。
 */
public final class BatchErrorAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof PsiFile)) {
            return;
        }
        PsiFile file = (PsiFile) element;
        String text = file.getText();
        int baseOffset = file.getTextRange().getStartOffset();

        Deque<Integer> parenthesisStack = new ArrayDeque<>();
        Lexer lexer = new BatchLexer();
        lexer.start(text, 0, text.length(), 0);
        while (lexer.getTokenType() != null) {
            IElementType tokenType = lexer.getTokenType();
            int start = baseOffset + lexer.getTokenStart();
            int end = baseOffset + lexer.getTokenEnd();
            String tokenText = lexer.getTokenText();

            if (tokenType == BatchTokens.BAD_CHARACTER) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unclosed '%' variable")
                        .range(new TextRange(start, end)).create();
            } else if (tokenType == BatchTokens.STRING && isUnclosedQuote(tokenText)) {
                holder.newAnnotation(HighlightSeverity.WARNING, "Unclosed quote")
                        .range(new TextRange(start, end)).create();
            } else if (tokenType == BatchTokens.OPERATOR) {
                if ("(".equals(tokenText)) {
                    parenthesisStack.push(start);
                } else if (")".equals(tokenText) && !parenthesisStack.isEmpty()) {
                    parenthesisStack.pop();
                }
            }
            lexer.advance();
        }
        for (Integer offset : parenthesisStack) {
            holder.newAnnotation(HighlightSeverity.WARNING, "Unmatched '('")
                    .range(new TextRange(offset, offset + 1)).create();
        }
    }

    static boolean isUnclosedQuote(String tokenText) {
        if (tokenText.length() == 0 || tokenText.charAt(0) != '"') {
            return false;
        }
        // 仅剩开引号（单字符）或结尾不是闭合引号
        return tokenText.length() < 2 || !tokenText.endsWith("\"");
    }
}
