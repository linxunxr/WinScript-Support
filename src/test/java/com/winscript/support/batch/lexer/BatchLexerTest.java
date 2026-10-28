package com.winscript.support.batch.lexer;

import com.intellij.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Batch Lexer 冒烟测试：验证注释、关键字、变量、标签与未闭合 % 的基本识别。
 */
class BatchLexerTest {

    @Test
    void lexesCommentKeywordAndVariable() {
        List<String> tokens = tokenize("rem setup comment\r\n@echo off\r\n");
        assertTrue(tokens.contains("BATCH_COMMENT"), tokens::toString);
        assertTrue(tokens.contains("BATCH_KEYWORD"), tokens::toString);

        tokens = tokenize("if \"%1\"==\"\" goto usage\r\n");
        assertTrue(tokens.contains("BATCH_KEYWORD"), tokens::toString);
        assertTrue(tokens.contains("BATCH_STRING"), tokens::toString);

        tokens = tokenize("if %ERRORLEVEL%==0 echo ok\r\n");
        assertTrue(tokens.contains("BATCH_VARIABLE"), tokens::toString);

        tokens = tokenize(":main\r\ncall :main\r\n");
        assertTrue(tokens.contains("BATCH_LABEL"), tokens::toString);

        tokens = tokenize("echo %~dp0 and !DELAYED!\r\n");
        assertTrue(tokens.stream().filter(t -> t.equals("BATCH_VARIABLE")).count() >= 2, tokens::toString);
    }

    @Test
    void marksUnclosedPercentAsBadCharacter() {
        List<String> tokens = tokenize("echo %UNCLOSED\r\n");
        assertTrue(tokens.contains("BAD_CHARACTER"), tokens::toString);
    }

    @Test
    void highlightsVariablesInsideQuotedString() {
        List<String> tokens = tokenize("echo \"Item %COUNT% of !NAME!\"\r\n");
        assertTrue(tokens.contains("BATCH_STRING"), tokens::toString);
        long variables = tokens.stream().filter("BATCH_VARIABLE"::equals).count();
        assertTrue(variables >= 2, tokens::toString);
    }

    @Test
    void lexesRemAfterAmpersand() {
        List<String> tokens = tokenize("dir & rem trailing comment\r\n");
        assertTrue(tokens.contains("BATCH_COMMENT"), tokens::toString);
    }

    private static List<String> tokenize(String text) {
        List<String> result = new ArrayList<>();
        Lexer lexer = new BatchLexer();
        lexer.start(text, 0, text.length(), 0);
        while (lexer.getTokenType() != null) {
            result.add(lexer.getTokenType().toString());
            lexer.advance();
        }
        return result;
    }
}
