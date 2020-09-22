
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

class Anagram {
    // Input: cloud, face, slot, could, loud, lots, lost

    // Output: {cloud, could},  {slot, lots, lost}

    public static List<String> anagrams(final List<String> lst) {
        final List<String> result = new ArrayList<>();

        for (int i = 0; i < lst.size()-1; i++) {

            final String s1 = lst.get(i);

            if (result.contains(s1)) {
                continue;
            }

            final List<String> lst2 = buildAnagramList(s1, lst.subList(i+1, lst.size()));

            result.addAll(lst2);
        }

        return result;
    }


    /**
     * Build a list of words from lst that are anagrams of s1.
     * Duplicate words are not considered anagrams.
     */
    private static List<String> buildAnagramList(final String s1, final List<String> lst) {
        final List<String> result = new ArrayList<>();

        for (final String s2: lst) {
            if (areAnagrams(s1, s2)) {
                if (result.isEmpty()) {
                    // Make sure the first word in a set of anagrams is added.
                    result.add(s1);
                }

                // Add the anagram s2.
                result.add(s2);
            }
        }

        return result;
    }

    /**
     * Are two strings anagrams of each other.
     *
     * This considers two string that are equal to be anagrams.
     */
    private static boolean areAnagrams(final String s1, final String s2) {

        // This assumes toCharArray copies.
        return areAnagrams(s1.toCharArray(), s2.toCharArray());
    }

    private static boolean areAnagrams(final char[] s1, final char[] s2) {
        for (int i = 0; i < s1.length; i++) {

            boolean foundIt = false;

            for (int j = 0; j < s2.length; j++) {
                if (s1[i] == s2[j]) {

                    // NOTE: This assumes 0 may never be a valid character.
                    s2[j] = 0;
                    foundIt = true;
                    break;
                }
            }

            if (!foundIt) {
                return false;
            }
        }

        return true;
    }
}

public class ATest {
    @Test
    public void testAnagrams() {
        // Input: cloud, face, slot, could, loud, lots, lost
        System.out.println(
                Anagram.anagrams(Arrays.asList("cloud", "face", "slot", "could", "loud", "lots", "lost")));


    }
}



