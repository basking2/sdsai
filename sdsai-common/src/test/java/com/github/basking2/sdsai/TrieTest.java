package com.github.basking2.sdsai;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;


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
            assertEquals("Finding word "+w, w, trie.find(w));
        }

        for (String w : words1) {
            w = w + w;

            assertNull("Finding word "+w, trie.find(w));
        }

        for (String w: words1) {
            assertEquals(w, trie.remove(w));
        }

        trie.add("canary", "canary");

        for (final String w : words1) {
            assertNull("Finding word "+w, trie.find(w));
        }
        assertEquals("canary", trie.find("canary"));
    }
}
