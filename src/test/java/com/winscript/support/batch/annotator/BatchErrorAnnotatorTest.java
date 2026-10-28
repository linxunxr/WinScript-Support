package com.winscript.support.batch.annotator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatchErrorAnnotatorTest {

    @Test
    void detectsUnclosedQuote() {
        assertTrue(BatchErrorAnnotator.isUnclosedQuote("\"abc"));
        assertTrue(BatchErrorAnnotator.isUnclosedQuote("\""));
        assertFalse(BatchErrorAnnotator.isUnclosedQuote("\"abc\""));
        assertFalse(BatchErrorAnnotator.isUnclosedQuote("abc"));
    }
}
