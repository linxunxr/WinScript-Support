package com.winscript.support.batch.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

/**
 * 手写 Batch Lexer（框架级实现，M1 里程碑完善粒度）。
 * <p>
 * Batch 为行导向语法，无跨行状态，{@link #getState()} 恒返回 0；
 * 行首 {@code rem} / {@code ::} 整行注释，{@code %VAR%}、{@code %~dp0}、
 * {@code %1}、{@code !VAR!} 识别为变量，未闭合的 {@code %} 标记 BAD_CHARACTER。
 */
public final class BatchLexer extends LexerBase {

    private static final Set<String> KEYWORDS = Set.of(
            "if", "else", "for", "goto", "call", "set", "echo", "exit", "rem");

    private static final String OPERATOR_CHARS = "&|<>()=;,+@";

    private CharSequence buffer;
    private int bufferEnd;
    private int tokenStart;
    private int tokenEnd;
    private IElementType currentToken;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.bufferEnd = endOffset;
        this.tokenStart = startOffset;
        this.tokenEnd = startOffset;
        this.currentToken = null;
        advance();
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public @Nullable IElementType getTokenType() {
        return currentToken;
    }

    @Override
    public int getTokenStart() {
        return tokenStart;
    }

    @Override
    public int getTokenEnd() {
        return tokenEnd;
    }

    @Override
    public void advance() {
        tokenStart = tokenEnd;
        currentToken = tokenStart >= bufferEnd ? null : nextToken();
    }

    @Override
    public @NotNull LexerPosition getCurrentPosition() {
        return new Position(tokenEnd, getState());
    }

    @Override
    public void restore(@NotNull LexerPosition position) {
        tokenEnd = position.getOffset();
        tokenStart = tokenEnd;
        advance();
    }

    @Override
    public @NotNull CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return bufferEnd;
    }

    private IElementType nextToken() {
        int pos = tokenStart;
        char c = buffer.charAt(pos);

        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
            return whitespace(pos);
        }
        if (c == ':') {
            return colon(pos);
        }
        if (isLineStart() && matchesRem(pos)) {
            return finishComment(scanToEndOfLine(pos));
        }
        if (c == '%') {
            return percent(pos);
        }
        if (c == '!') {
            return delayedVariable(pos);
        }
        if (c == '"') {
            return quotedString(pos);
        }
        if (c == '^') {
            tokenEnd = Math.min(pos + 2, bufferEnd);
            return BatchTokens.ESCAPE;
        }
        if (OPERATOR_CHARS.indexOf(c) >= 0) {
            tokenEnd = pos + 1;
            return BatchTokens.OPERATOR;
        }
        return word(pos);
    }

    private IElementType whitespace(int pos) {
        while (pos < bufferEnd && " \t\r\n".indexOf(buffer.charAt(pos)) >= 0) {
            pos++;
        }
        tokenEnd = pos;
        return BatchTokens.WHITE_SPACE;
    }

    private IElementType colon(int pos) {
        if (pos + 1 < bufferEnd && buffer.charAt(pos + 1) == ':') {
            return finishComment(scanToEndOfLine(pos));
        }
        int scan = pos + 1;
        while (scan < bufferEnd && isLabelChar(buffer.charAt(scan))) {
            scan++;
        }
        if (scan > pos + 1) {
            tokenEnd = scan;
            return BatchTokens.LABEL;
        }
        tokenEnd = pos + 1;
        return BatchTokens.OPERATOR;
    }

    private IElementType percent(int pos) {
        // %~dp0 等参数修饰形式
        if (pos + 1 < bufferEnd && buffer.charAt(pos + 1) == '~') {
            int scan = pos + 2;
            while (scan < bufferEnd && "~dpnfxyz$".indexOf(buffer.charAt(scan)) >= 0) {
                scan++;
            }
            if (scan < bufferEnd && (Character.isDigit(buffer.charAt(scan)) || buffer.charAt(scan) == '*')) {
                scan++;
            }
            tokenEnd = Math.max(scan, pos + 2);
            return BatchTokens.VARIABLE;
        }
        // %%i（for 循环变量）
        if (pos + 1 < bufferEnd && buffer.charAt(pos + 1) == '%') {
            if (pos + 2 < bufferEnd && isWordChar(buffer.charAt(pos + 2))) {
                tokenEnd = pos + 3;
            } else {
                tokenEnd = pos + 2;
            }
            return BatchTokens.VARIABLE;
        }
        // %1 位置参数
        if (pos + 1 < bufferEnd && Character.isDigit(buffer.charAt(pos + 1))) {
            tokenEnd = pos + 2;
            return BatchTokens.VARIABLE;
        }
        // %VAR%：闭合符必须位于同一行且中间无空白
        int scan = pos + 1;
        while (scan < bufferEnd) {
            char ch = buffer.charAt(scan);
            if (ch == '\n' || ch == '\r' || ch == ' ' || ch == '\t') {
                break;
            }
            if (ch == '%') {
                tokenEnd = scan + 1;
                return BatchTokens.VARIABLE;
            }
            scan++;
        }
        tokenEnd = pos + 1;
        return BatchTokens.BAD_CHARACTER;
    }

    private IElementType delayedVariable(int pos) {
        int scan = pos + 1;
        while (scan < bufferEnd) {
            char ch = buffer.charAt(scan);
            if (ch == '\n' || ch == '\r') {
                break;
            }
            if (ch == '!') {
                tokenEnd = scan + 1;
                return BatchTokens.VARIABLE;
            }
            scan++;
        }
        tokenEnd = pos + 1;
        return BatchTokens.OPERATOR;
    }

    private IElementType quotedString(int pos) {
        int scan = pos + 1;
        while (scan < bufferEnd && buffer.charAt(scan) != '"' && buffer.charAt(scan) != '\n') {
            scan++;
        }
        // 闭合引号计入 token；行尾处截断，不吞换行符
        tokenEnd = (scan < bufferEnd && buffer.charAt(scan) == '"') ? scan + 1 : scan;
        return BatchTokens.STRING;
    }

    private IElementType word(int pos) {
        int scan = pos;
        while (scan < bufferEnd && isWordChar(buffer.charAt(scan))) {
            scan++;
        }
        tokenEnd = scan;
        String word = buffer.subSequence(pos, scan).toString().toLowerCase(Locale.ROOT);
        return KEYWORDS.contains(word) ? BatchTokens.KEYWORD : BatchTokens.WORD;
    }

    private IElementType finishComment(int end) {
        tokenEnd = end;
        return BatchTokens.COMMENT;
    }

    private int scanToEndOfLine(int pos) {
        int scan = pos;
        while (scan < bufferEnd && buffer.charAt(scan) != '\n') {
            scan++;
        }
        return scan;
    }

    private boolean isLineStart() {
        return tokenStart == 0 || buffer.charAt(tokenStart - 1) == '\n';
    }

    private boolean matchesRem(int pos) {
        if (pos + 3 > bufferEnd) {
            return false;
        }
        String head = buffer.subSequence(pos, pos + 3).toString().toLowerCase(Locale.ROOT);
        if (!head.equals("rem")) {
            return false;
        }
        int after = pos + 3;
        return after >= bufferEnd || " \t\r\n".indexOf(buffer.charAt(after)) >= 0;
    }

    private boolean isLabelChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '-';
    }

    private boolean isWordChar(char c) {
        if (Character.isWhitespace(c)) {
            return false;
        }
        return " \t\r\n\"^!%&|<>()=;,+@".indexOf(c) < 0;
    }

    private static final class Position implements LexerPosition {
        private final int offset;
        private final int state;

        Position(int offset, int state) {
            this.offset = offset;
            this.state = state;
        }

        @Override
        public int getOffset() {
            return offset;
        }

        @Override
        public int getState() {
            return state;
        }
    }
}
