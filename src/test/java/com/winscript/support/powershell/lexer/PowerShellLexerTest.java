package com.winscript.support.powershell.lexer;

import com.intellij.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PowerShell Lexer 冒烟测试：验证关键字、变量、字符串、块注释与 here-string。
 */
class PowerShellLexerTest {

    @Test
    void lexesKeywordVariableAndString() {
        List<String> tokens = tokenize("function Get-Name($param) {\n  return $env:PATH\n}\n");
        assertTrue(tokens.contains("PS_KEYWORD"), tokens::toString);
        assertTrue(tokens.contains("PS_VARIABLE"), tokens::toString);

        tokens = tokenize("$x = 'literal'\n");
        assertTrue(tokens.contains("PS_STRING"), tokens::toString);

        tokens = tokenize("Get-Process | Where-Object { $_.Name -match 'idea' }\n");
        assertTrue(tokens.contains("PS_OPERATOR"), tokens::toString);
    }

    @Test
    void lexesCommentsAndHereStrings() {
        List<String> tokens = tokenize("# line comment\n");
        assertTrue(tokens.contains("PS_COMMENT"), tokens::toString);

        tokens = tokenize("<# block\ncomment #>\n");
        assertTrue(tokens.contains("PS_COMMENT"), tokens::toString);

        tokens = tokenize("$text = @\"\nhello\n\"@\n");
        assertTrue(tokens.contains("PS_STRING"), tokens::toString);
    }

    private static List<String> tokenize(String text) {
        List<String> result = new ArrayList<>();
        Lexer lexer = new PowerShellLexer();
        lexer.start(text, 0, text.length(), 0);
        while (lexer.getTokenType() != null) {
            result.add(lexer.getTokenType().toString());
            lexer.advance();
        }
        return result;
    }
}
