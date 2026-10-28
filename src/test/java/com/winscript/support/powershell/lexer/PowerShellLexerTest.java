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

    @Test
    void lexesNumbersAndTypeExpressions() {
        List<String> tokens = tokenize("$size = 1GB -gt 0x1F\r\n");
        assertTrue(tokens.contains("PS_NUMBER"), tokens::toString);
        assertTrue(tokens.stream().filter("PS_NUMBER"::equals).count() >= 2, tokens::toString);

        tokens = tokenize("[string]$name = [int[]]::Empty\r\n");
        assertTrue(tokens.contains("PS_TYPE"), tokens::toString);
        assertTrue(tokens.stream().filter("PS_TYPE"::equals).count() >= 2, tokens::toString);

        // 数组索引不是类型表达式
        tokens = tokenize("$items[0]\r\n");
        assertTrue(!tokens.contains("PS_TYPE"), tokens::toString);
    }

    @Test
    void survivesBareBacktickOutsideString() {
        // 串外裸反引号（续行/转义）不得造成 0 宽 token 死循环
        List<String> tokens = tokenize("Get-Process `\r\n  -Name idea\r\n");
        assertTrue(tokens.contains("PS_ESCAPE"), tokens::toString);
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
