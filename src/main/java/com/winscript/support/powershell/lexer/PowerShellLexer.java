package com.winscript.support.powershell.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Set;

/**
 * 手写 PowerShell 轻量 Lexer（框架级实现，仅服务于词法高亮，不做完整语法）。
 * <p>
 * 与 Batch 不同，PowerShell 存在跨行结构，Lexer 用单一整数状态编码：
 * 0 = 普通代码，1 = 块注释 {@code <# ... #>}，2 = 双引号 here-string，
 * 3 = 单引号 here-string。状态参与增量重词法。
 */
public final class PowerShellLexer extends LexerBase {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_BLOCK_COMMENT = 1;
    private static final int STATE_HERE_STRING_DQ = 2;
    private static final int STATE_HERE_STRING_SQ = 3;

    private static final Set<String> KEYWORDS = Set.of(
            "function", "param", "if", "else", "elseif", "foreach", "while",
            "try", "catch", "throw", "return", "switch");

    /** 形如 -eq / -like / -match 的参数运算符。 */
    private static final Set<String> PARAM_OPERATORS = Set.of(
            "-eq", "-ne", "-ge", "-gt", "-lt", "-le",
            "-like", "-notlike", "-match", "-notmatch", "-replace",
            "-and", "-or", "-not", "-in", "-contains");

    private static final String OPERATOR_CHARS = "(){}[],;=<>+-*/%!&|";

    /** 数字字面量：十进制（可带下划线/小数）、十六进制、K/KB…T/TB 后缀。 */
    private static final java.util.regex.Pattern NUMBER_PATTERN =
            java.util.regex.Pattern.compile("(?i)(0x[0-9a-f_]+|\\d[\\d_]*(\\.\\d[\\d_]*)?)(kb|k|mb|m|gb|g|tb|t|d|l)?");

    private CharSequence buffer;
    private int bufferEnd;
    private int tokenStart;
    private int tokenEnd;
    private int state;
    private IElementType currentToken;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.bufferEnd = endOffset;
        this.tokenStart = startOffset;
        this.tokenEnd = startOffset;
        this.state = initialState;
        this.currentToken = null;
        advance();
    }

    @Override
    public int getState() {
        return state;
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
        return new Position(tokenEnd, state);
    }

    @Override
    public void restore(@NotNull LexerPosition position) {
        tokenEnd = position.getOffset();
        tokenStart = tokenEnd;
        state = position.getState();
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
        return switch (state) {
            case STATE_BLOCK_COMMENT -> blockComment(pos);
            case STATE_HERE_STRING_DQ -> hereString(pos, STATE_HERE_STRING_DQ, "\"@");
            case STATE_HERE_STRING_SQ -> hereString(pos, STATE_HERE_STRING_SQ, "'@");
            default -> normalToken(pos);
        };
    }

    private IElementType normalToken(int pos) {
        char c = buffer.charAt(pos);

        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
            return whitespace(pos);
        }
        if (c == '#') {
            tokenEnd = scanToEndOfLine(pos);
            return PowerShellTokens.COMMENT;
        }
        if (c == '<' && pos + 1 < bufferEnd && buffer.charAt(pos + 1) == '#') {
            return blockComment(pos);
        }
        if (c == '@' && pos + 1 < bufferEnd && (buffer.charAt(pos + 1) == '"' || buffer.charAt(pos + 1) == '\'')) {
            boolean startsHereString = pos + 2 >= bufferEnd
                    || buffer.charAt(pos + 2) == '\n' || buffer.charAt(pos + 2) == '\r';
            if (startsHereString) {
                boolean dq = buffer.charAt(pos + 1) == '"';
                return hereString(pos, dq ? STATE_HERE_STRING_DQ : STATE_HERE_STRING_SQ, dq ? "\"@" : "'@");
            }
        }
        if (c == '"') {
            return quotedString(pos, '"');
        }
        if (c == '\'') {
            return quotedString(pos, '\'');
        }
        if (c == '$') {
            return variable(pos);
        }
        if (c == ':' && pos + 1 < bufferEnd && buffer.charAt(pos + 1) == ':') {
            tokenEnd = pos + 2;
            return PowerShellTokens.OPERATOR;
        }
        if (c == '|') {
            tokenEnd = pos + 1;
            return PowerShellTokens.OPERATOR;
        }
        if (c == '`') {
            // 串外反引号转义（含续行）：吃掉 ` 与下一字符，避免 0 宽 token 死循环
            tokenEnd = Math.min(pos + 2, bufferEnd);
            return PowerShellTokens.ESCAPE;
        }
        if (c == '[' && pos + 1 < bufferEnd && Character.isLetter(buffer.charAt(pos + 1))) {
            return typeExpression(pos);
        }
        if (c == '-' ) {
            return dashToken(pos);
        }
        if (OPERATOR_CHARS.indexOf(c) >= 0) {
            tokenEnd = pos + 1;
            return PowerShellTokens.OPERATOR;
        }
        return word(pos);
    }

    private IElementType whitespace(int pos) {
        int scan = pos;
        while (scan < bufferEnd && " \t\r\n".indexOf(buffer.charAt(scan)) >= 0) {
            scan++;
        }
        tokenEnd = scan;
        return PowerShellTokens.WHITE_SPACE;
    }

    private IElementType blockComment(int pos) {
        state = STATE_NORMAL;
        int scan = pos + 2;
        while (scan + 1 < bufferEnd && !(buffer.charAt(scan) == '#' && buffer.charAt(scan + 1) == '>')) {
            scan++;
        }
        if (scan + 1 < bufferEnd) {
            tokenEnd = scan + 2;
        } else {
            // 未闭合：吞到缓冲区末尾，保持块注释状态供增量重词法
            state = STATE_BLOCK_COMMENT;
            tokenEnd = bufferEnd;
        }
        return PowerShellTokens.COMMENT;
    }

    private IElementType hereString(int pos, int forState, String terminator) {
        int scan = pos + 2;
        while (scan + 2 < bufferEnd) {
            if (buffer.charAt(scan) == '\n'
                    && buffer.charAt(scan + 1) == terminator.charAt(0)
                    && buffer.charAt(scan + 2) == terminator.charAt(1)) {
                state = STATE_NORMAL;
                tokenEnd = scan + 3;
                return PowerShellTokens.STRING;
            }
            scan++;
        }
        state = forState;
        tokenEnd = bufferEnd;
        return PowerShellTokens.STRING;
    }

    private IElementType quotedString(int pos, char quote) {
        int scan = pos + 1;
        while (scan < bufferEnd && buffer.charAt(scan) != quote && buffer.charAt(scan) != '\n') {
            // 反引号转义：`" `n 等
            if (buffer.charAt(scan) == '`' && scan + 1 < bufferEnd) {
                scan++;
            }
            scan++;
        }
        // 闭合引号计入 token；行尾/缓冲区末尾处截断，不吞换行符
        tokenEnd = (scan < bufferEnd && buffer.charAt(scan) == quote) ? scan + 1 : scan;
        return PowerShellTokens.STRING;
    }

    private IElementType variable(int pos) {
        int scan;
        if (pos + 1 < bufferEnd && buffer.charAt(pos + 1) == '{') {
            scan = pos + 2;
            while (scan < bufferEnd && buffer.charAt(scan) != '}') {
                scan++;
            }
            tokenEnd = Math.min(scan + 1, bufferEnd);
            return PowerShellTokens.VARIABLE;
        }
        scan = pos + 1;
        while (scan < bufferEnd && isIdentChar(buffer.charAt(scan))) {
            scan++;
        }
        // 可选作用域前缀：$env:VAR、$script:var
        if (scan < bufferEnd && buffer.charAt(scan) == ':'
                && scan + 1 < bufferEnd && isIdentChar(buffer.charAt(scan + 1))) {
            scan++;
            while (scan < bufferEnd && isIdentChar(buffer.charAt(scan))) {
                scan++;
            }
        }
        tokenEnd = Math.max(scan, pos + 1);
        return PowerShellTokens.VARIABLE;
    }

    private IElementType dashToken(int pos) {
        // '-' 本身不属于 wordChar，扫描从 '-' 之后开始，保证 token 至少前进一格
        int scan = pos + 1;
        while (scan < bufferEnd && isWordChar(buffer.charAt(scan))) {
            scan++;
        }
        tokenEnd = scan;
        String token = buffer.subSequence(pos, scan).toString().toLowerCase(Locale.ROOT);
        return PARAM_OPERATORS.contains(token) ? PowerShellTokens.OPERATOR : PowerShellTokens.WORD;
    }

    private IElementType typeExpression(int pos) {
        // [string] / [int[]] / [System.String] / [Parameter(Mandatory)]：
        // '[' 后以字母开头、无空白、方括号配对到行内的完整表达式；不满足则回退单字符运算符
        int scan = pos + 1;
        int depth = 1;
        while (scan < bufferEnd) {
            char ch = buffer.charAt(scan);
            if (ch == '\n' || ch == '\r' || ch == ' ' || ch == '\t') {
                break;
            }
            if (ch == '[') {
                depth++;
            } else if (ch == ']') {
                depth--;
                if (depth == 0) {
                    tokenEnd = scan + 1;
                    return PowerShellTokens.TYPE;
                }
            }
            scan++;
        }
        tokenEnd = pos + 1;
        return PowerShellTokens.OPERATOR;
    }

    private IElementType word(int pos) {
        int scan = pos;
        while (scan < bufferEnd && isWordChar(buffer.charAt(scan))) {
            scan++;
        }
        tokenEnd = scan;
        String word = buffer.subSequence(pos, scan).toString();
        if (KEYWORDS.contains(word.toLowerCase(Locale.ROOT))) {
            return PowerShellTokens.KEYWORD;
        }
        if (NUMBER_PATTERN.matcher(word).matches()) {
            return PowerShellTokens.NUMBER;
        }
        return PowerShellTokens.WORD;
    }

    private int scanToEndOfLine(int pos) {
        int scan = pos;
        while (scan < bufferEnd && buffer.charAt(scan) != '\n') {
            scan++;
        }
        return scan;
    }

    private boolean isIdentChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    private boolean isWordChar(char c) {
        if (Character.isWhitespace(c)) {
            return false;
        }
        return "(){}[],;=<>+-*/%!&|\"'`$#:".indexOf(c) < 0;
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
