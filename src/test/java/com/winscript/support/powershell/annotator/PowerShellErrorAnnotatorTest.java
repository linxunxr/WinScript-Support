package com.winscript.support.powershell.annotator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerShellErrorAnnotatorTest {

    @Test
    void detectsUnclosedBlockComment() {
        assertTrue(PowerShellErrorAnnotator.isUnclosedComment("<# never closed"));
        assertFalse(PowerShellErrorAnnotator.isUnclosedComment("<# closed #>"));
        assertFalse(PowerShellErrorAnnotator.isUnclosedComment("# line"));
    }

    @Test
    void detectsUnclosedHereString() {
        assertTrue(PowerShellErrorAnnotator.isUnclosedHereString("@\"\nopen"));
        assertFalse(PowerShellErrorAnnotator.isUnclosedHereString("@\"\nclosed\n\"@"));
        assertTrue(PowerShellErrorAnnotator.isUnclosedHereString("@'\nopen"));
        assertFalse(PowerShellErrorAnnotator.isUnclosedHereString("@'\nclosed\n'@"));
        assertFalse(PowerShellErrorAnnotator.isUnclosedHereString("'plain'"));
    }

    @Test
    void detectsUnclosedInlineString() {
        assertTrue(PowerShellErrorAnnotator.isUnclosedInlineString("\"open"));
        assertTrue(PowerShellErrorAnnotator.isUnclosedInlineString("'open"));
        assertTrue(PowerShellErrorAnnotator.isUnclosedInlineString("\""));
        assertFalse(PowerShellErrorAnnotator.isUnclosedInlineString("\"ok\""));
        assertFalse(PowerShellErrorAnnotator.isUnclosedInlineString("@\""));
    }
}
