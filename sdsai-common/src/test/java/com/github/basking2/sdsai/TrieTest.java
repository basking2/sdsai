/**
 * Copyright (c) 2018-2023 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrieTest {

    final List<String> words1 = Arrays.asList(
            "abacination",
            "abaciscus",
            "abacist",
            "aback",
            "abactinal",
            "abactinally",
            "abaction",
            "abactor",
            "abaculus",
            "abacus",
            "Abadite",
            "abaff",
            "abaft",
            "abaisance",
            "abaiser",
            "abaissed",
            "abalienate",
            "abalienation",
            "abalone",
            "Abama",
            "abampere",
            "abandon",
            "abandonable",
            "abandoned",
            "abandonedly",
            "abandonee",
            "abandoner"
    );

    @Test
    public void build() {
        final Trie<String> trie = new Trie<>();

        for (final String w : words1) {
            trie.add(w, w);
        }

        for (final String w : words1) {
            assertEquals(w, trie.find(w), "Finding word "+w);
        }

        for (String w : words1) {
            w = w + w;

            assertNull(trie.find(w), "Finding word "+w);
        }

        for (String w: words1) {
            assertEquals(w, trie.remove(w));
        }

        trie.add("canary", "canary");

        for (final String w : words1) {
            assertNull(trie.find(w), "Finding word "+w);
        }
        assertEquals("canary", trie.find("canary"));
    }

    @Test
    public void failToFindMissingIntermediateNodes() {
        final Trie<String> trie = new Trie<>();

        trie.add("abcd", "value1");
        trie.add("a", "value2");

        assertEquals("value1", trie.find("abcd"));
        assertEquals("value2", trie.find("a"));
        assertNull(trie.find("ab"));
        assertNull(trie.find("abc"));
    }
}
