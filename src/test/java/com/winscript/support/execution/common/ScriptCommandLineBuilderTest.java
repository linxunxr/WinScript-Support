package com.winscript.support.execution.common;

import com.intellij.openapi.util.io.FileUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 宏展开单元测试（纯静态逻辑，不依赖平台容器）。
 * 期望值经 {@link FileUtil#toSystemDependentName} 同步转换，保证跨平台通过。
 */
class ScriptCommandLineBuilderTest {

    @Test
    void expandsProjectAndUserMacros() {
        assertEquals(sys("D:/proj/run.bat"),
                ScriptCommandLineBuilder.expandMacros("$ProjectFileDir$/run.bat", "D:/proj", "demo", null));
        assertEquals("demo-copy",
                ScriptCommandLineBuilder.expandMacros("$ProjectName$-copy", "D:/proj", "demo", null));
    }

    @Test
    void expandsFileMacrosFromScriptPath() {
        String result = ScriptCommandLineBuilder.expandMacros(
                "$FileDir$ | $FileName$ | $FileNameWithoutExtension$ | $FileExtension$",
                "D:/proj", "demo", "D:/proj/scripts/hello.bat");
        assertEquals(sys("D:/proj/scripts") + " | hello.bat | hello | bat", result);
    }

    @Test
    void fallsBackToFileDirEqualsProjectDirWithoutScript() {
        assertEquals(sys("D:/proj/sub"),
                ScriptCommandLineBuilder.expandMacros("$FileDir$/sub", "D:/proj", "demo", null));
    }

    @Test
    void keepsUnknownMacrosUntouched() {
        assertEquals("$Unknown$-x",
                ScriptCommandLineBuilder.expandMacros("$Unknown$-x", "D:/proj", "demo", null));
    }

    @Test
    void handlesScriptPathWithoutDirectory() {
        assertEquals("hello | hello.ps1 | hello | ps1",
                ScriptCommandLineBuilder.expandMacros(
                        "$FileNameWithoutExtension$ | $FileName$ | $FileNameWithoutExtension$ | $FileExtension$",
                        "D:/proj", "demo", "hello.ps1"));
    }

    private static String sys(String path) {
        return FileUtil.toSystemDependentName(path);
    }
}
